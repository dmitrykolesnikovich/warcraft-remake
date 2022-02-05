/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.warcraft.object;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerConfig;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListenerVoid;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorChecker;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListener;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.Fovable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.EntityModelHelper;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.ProduceProgress;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.feature.Buildable;
import com.b3dgs.warcraft.object.feature.EntitySfx;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.state.StateIdle;
import com.b3dgs.warcraft.object.state.StateProducing;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements Routine, Recyclable
{
    private static final String NODE_UNLOCK = "unlock";
    private static final String ATT_VALUES = "values";

    /**
     * Create production listener.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param progress The progress bar.
     * @param producible The producible in production.
     * @param pathfindable The producer pathfindable.
     * @return The created listener.
     */
    private static ProducibleListener createListener(Services services,
                                                     Setup setup,
                                                     ProduceProgress progress,
                                                     Producible producible,
                                                     Pathfindable pathfindable)
    {
        final MapTile map = services.get(MapTile.class);
        final Player player = services.get(Player.class);
        final Set<String> unlocks = loadUnlocks(producible);

        return new ProducibleListenerVoid()
        {
            @Override
            public void notifyProductionStarted(Producer producer)
            {
                final Pathfindable pathfindableProducer = producer.getFeature(Pathfindable.class);
                pathfindableProducer.stopMoves();
                pathfindableProducer.clearPath();
                pathfindable.setLocation(map.getInTileX(producible), map.getInTileY(producible));
                producible.getFeature(EntitySfx.class).onStarted();

                if (!producer.hasFeature(Buildable.class))
                {
                    producer.getFeature(EntityModel.class).setVisible(false);
                }
            }

            @Override
            public void notifyProductionEnded(Producer producer)
            {
                producible.getFeature(EntitySfx.class).onProduced();
                player.unlock(unlocks);
                Util.teleportOutside(map, producer, producible);
            }
        };
    }

    /**
     * Load defined unlocks.
     * 
     * @param producible The producible reference.
     * @return The loaded unlocks.
     */
    private static Set<String> loadUnlocks(Producible producible)
    {
        final Media media = producible.getMedia();
        if (media != null)
        {
            final Configurer configurer = new Configurer(media);
            if (configurer.hasNode(NODE_UNLOCK))
            {
                return new HashSet<>(Arrays.asList(configurer.getString(ATT_VALUES, NODE_UNLOCK)
                                                             .split(Constant.SFX_SEPARATOR)));
            }
        }
        return Collections.emptySet();
    }

    private final ExtractorListener extractorListener = new ExtractorListenerVoid()
    {
        @Override
        public void notifyStartGoToRessources(String type, Tiled resourceLocation)
        {
            if (carryResource == null)
            {
                pathfindable.setDestination(resourceLocation);
                gotoResource = true;
            }
        }

        @Override
        public void notifyStartExtraction(String type, Tiled resourceLocation)
        {
            extractResource = type;
            if (Constant.RESOURCE_WOOD.equals(type))
            {
                pathfindable.pointTo(resourceLocation);
            }
        }

        @Override
        public void notifyStartCarry(String type, int totalQuantity)
        {
            final Tiled warehouse = Util.getWarehouse(services, stats.getRace());
            if (warehouse != null)
            {
                pathfindable.setDestination(warehouse);
                extractResource = null;
                carryResource = type;

                if (Constant.RESOURCE_WOOD.equals(type))
                {
                    cutWood();
                }

                switchActionExtractCarry();
            }
            else
            {
                extractor.stopExtraction();
            }
        }

        @Override
        public void notifyStartDropOff(String type, int totalQuantity)
        {
            setVisible(false);
            if (player.owns(EntityModel.this))
            {
                player.increaseResource(type, totalQuantity);
            }
        }

        @Override
        public void notifyDroppedOff(String type, int droppedQuantity)
        {
            if (droppedQuantity == 0)
            {
                setVisible(true);
                carryResource = null;
            }
        }

        @Override
        public void notifyStopped()
        {
            gotoResource = false;
            extractResource = null;
        }
    };

    private final Player player = services.get(Player.class);
    private final Hud hud = services.get(Hud.class);
    private final Selector selector = services.get(Selector.class);
    private final MapTile map = services.get(MapTile.class);
    private final MapTilePath mapPath = map.getFeature(MapTilePath.class);
    private final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
    private final Viewer viewer = services.get(Viewer.class);
    private final FogOfWar fogOfWar = services.get(FogOfWar.class);

    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Selectable selectable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Fovable fovable;
    @FeatureGet private Extractor extractor;
    @FeatureGet private Attacker attacker;
    @FeatureGet private Producer producer;
    @FeatureGet private Producible producible;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private EntityStats stats;

    private boolean gotoResource;
    private String extractResource;
    private String carryResource;

    private boolean visible = true;
    private boolean display = true;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Draw entity selection area.
     * 
     * @param g The graphic output.
     */
    private void drawSelection(Graphic g)
    {
        g.setColor(player.getColor(this));
        g.drawRect(viewer, Origin.BOTTOM_LEFT, transformable, false);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        rasterable.setOrigin(Origin.BOTTOM_LEFT);

        pathfindable.setSpeed(0.8, 0.8);

        fovable.setCanUpdate(() -> player.owns(provider));

        attacker.setAttackDistanceComputer((source, target) -> Util.getDistanceInTile(map, source, target));
        attacker.setAttackChecker(target -> target.getFeature(EntityStats.class).getHealthPercent() > 0);

        producer.setStepsSpeed(1.0);
        if (Constant.DEBUG)
        {
            producer.setStepsSpeed(50);
        }
        producer.addListener(new ProducerListenerVoid()
        {
            @Override
            public void notifyStartProduction(Featurable featurable)
            {
                featurable.getFeature(StateHandler.class).changeState(StateProducing.class);
            }
        });

        extractor.setChecker(new ExtractorChecker()
        {
            @Override
            public boolean canExtract()
            {
                return pathfindable.isDestinationReached()
                       && Util.getDistanceInTile(pathfindable, extractor.getResourceLocation()) < 1.5;
            }

            @Override
            public boolean canCarry()
            {
                final Tiled warehouse = Util.getWarehouse(services, stats.getRace());
                if (warehouse == null)
                {
                    return false;
                }
                return Util.getDistanceInTile(pathfindable, warehouse) < 1.5;
            }
        });

        collidable.setGroup(Integer.valueOf(Constant.LAYER_ENTITY));
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setCollisionVisibility(false);
        collidable.setOrigin(Origin.BOTTOM_LEFT);

        if (setup.hasNode(AttackerConfig.NODE_ATTACKER))
        {
            attacker.addListener(new AttackerListenerVoid()
            {
                @Override
                public void notifyReachingTarget(Transformable target)
                {
                    if (!pathfindable.isMoving() && !pathfindable.setDestination(target)
                        || target.getFeature(EntityStats.class).getHealthPercent() == 0)
                    {
                        attacker.stopAttack();
                    }
                }

                @Override
                public void notifyAttackStarted(Transformable target)
                {
                    pathfindable.stopMoves();
                }

                @Override
                public void notifyAttackEnded(Transformable target, int damages)
                {
                    if (target.getFeature(EntityStats.class).getHealthPercent() == 0)
                    {
                        attacker.stopAttack();
                    }
                }
            });
        }

        final ProduceProgress progress = services.get(ProduceProgress.class);
        producible.addListener(createListener(services, setup, progress, producible, pathfindable));
        extractor.addListener(extractorListener);
    }

    @Override
    public void update(double extrp)
    {
        rasterable.setVisibility(visible && display && fogOfWar.isVisible(pathfindable));
    }

    @Override
    public void render(Graphic g)
    {
        if (visible && fogOfWar.isVisible(pathfindable))
        {
            if (selectable.isSelected())
            {
                drawSelection(g);
            }
        }
    }

    /**
     * Set the visible flag.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        collidable.setEnabled(visible);

        if (!visible && selector.getSelection().remove(selectable))
        {
            selectable.onSelection(false);
            hud.clearMenus();
        }
    }

    /**
     * Set display flag.
     * 
     * @param display The display flag.
     */
    public void setDisplay(boolean display)
    {
        this.display = display;
        rasterable.setVisibility(display);
    }

    /**
     * Check visible flag.
     * 
     * @return <code>true</code> if visible, <code>false</code> else.
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Get the services reference.
     * 
     * @return The services reference.
     */
    public Services getServices()
    {
        return services;
    }

    /**
     * Check if going to resource.
     * 
     * @return <code>true</code> if going to resource, <code>false</code> else.
     */
    public boolean isGotoResource()
    {
        return gotoResource;
    }

    /**
     * Get the extracting resource type.
     * 
     * @return The extracting resource type, <code>null</code> if none.
     */
    public String getExtractResource()
    {
        return extractResource;
    }

    /**
     * Get the carrying resource type.
     * 
     * @return The carrying resource type, <code>null</code> if none.
     */
    public String getCarryResource()
    {
        return carryResource;
    }

    /**
     * Reset states flag.
     */
    public void resetFlags()
    {
        extractResource = null;
    }

    /**
     * Cut wood tile and search next tree.
     */
    private void cutWood()
    {
        final Tile tile = mapPath.getTile(extractor.getResourceLocation());
        map.setTile(tile.getInTileX(), tile.getInTileY(), Constant.TILE_NUM_TREE_CUT);
        mapTransition.resolve(map.getTile(tile.getInTileX(), tile.getInTileY()));

        final Tile next = Util.getClosestTree(map, mapPath, tile, transformable);
        if (next != null)
        {
            extractor.setResource(Constant.RESOURCE_WOOD, next);
        }
        else
        {
            extractor.stopExtraction();
        }
    }

    /**
     * Switch action between extract or carry when needed.
     */
    private void switchActionExtractCarry()
    {
        if (player.owns(this))
        {
            for (final Actionable actionable : hud.getActive())
            {
                final boolean carry = carryResource != null;
                Util.switchExtractCarryAction(actionable, carry);
            }
        }
    }

    @Override
    public void recycle()
    {
        attacker.stopAttack();
        pathfindable.stopMoves();
        extractor.stopExtraction();
        collidable.setEnabled(true);
        selectable.onSelection(false);
        resetFlags();
        carryResource = null;
        visible = true;
        stateHandler.changeState(StateIdle.class);
    }
}
