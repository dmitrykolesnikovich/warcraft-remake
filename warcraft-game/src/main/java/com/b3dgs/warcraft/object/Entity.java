/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Locale;
import java.util.Set;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.ActionerModel;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerConfig;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListenerVoid;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorChecker;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FovableModel;
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
 * Entity representation base.
 */
public class Entity extends FeaturableModel
{
    private static final int PREFIX = State.class.getSimpleName().length();
    private static final String NODE_UNLOCK = "unlock";
    private static final String ATT_VALUES = "values";

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    public static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

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
                pathfindable.setLocation((int) (producible.getX() / 16), (int) (producible.getY() / 16));
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

                if (!producer.hasFeature(Buildable.class))
                {
                    producer.getFeature(EntityModel.class).setVisible(true);
                    teleportOutside(map, producer);
                }
                if (!producible.hasFeature(Buildable.class))
                {
                    producible.getFeature(EntityModel.class).setVisible(true);
                    teleportOutside(map, producible);
                }
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

    /**
     * Teleport producer outside producible area.
     * 
     * @param map The map tile reference.
     * @param mover The mover reference.
     */
    private static void teleportOutside(MapTile map, FeatureProvider mover)
    {
        final Pathfindable pathfindable = mover.getFeature(Pathfindable.class);
        final CoordTile coord = map.getFeature(MapTilePath.class)
                                   .getClosestAvailableTile(pathfindable, pathfindable, 16);
        pathfindable.setLocation(coord);
    }

    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Entity(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(services, setup));
        addFeature(new MirrorableModel());
        addFeature(new TransformableModel(setup));
        addFeature(new SelectableModel());
        addFeature(new AnimatableModel());
        addFeature(new ActionerModel(setup));
        addFeature(new FovableModel(services, setup));

        final MapTile map = services.get(MapTile.class);

        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));
        final Attacker attacker = addFeatureAndGet(new AttackerModel(setup));
        attacker.setAttackDistanceComputer((source, target) -> Util.getDistanceInTile(map, source, target));

        if (setup.hasNode(AttackerConfig.NODE_ATTACKER))
        {
            attacker.addListener(new AttackerListenerVoid()
            {
                @Override
                public void notifyReachingTarget(Transformable target)
                {
                    if (!pathfindable.isMoving() && !pathfindable.setDestination(target))
                    {
                        attacker.stopAttack();
                    }
                }

                @Override
                public void notifyAttackStarted(Transformable target)
                {
                    pathfindable.stopMoves();
                }
            });
        }

        final Producer producer = addFeatureAndGet(new ProducerModel(services));
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

        final ProduceProgress progress = services.get(ProduceProgress.class);
        final Producible producible = addFeatureAndGet(new ProducibleModel(setup));
        producible.addListener(createListener(services, setup, progress, producible, pathfindable));

        final EntityStats stats = addFeatureAndGet(new EntityStats(services, setup));

        final ExtractorModel extractor = addFeatureAndGet(new ExtractorModel(services, setup));
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

        final StateHandler stateHandler = addFeatureAndGet(new StateHandler(setup, Entity::getAnimationName));
        stateHandler.changeState(StateIdle.class);

        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setGroup(Integer.valueOf(Constant.LAYER_ENTITY));
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setCollisionVisibility(false);
        collidable.setOrigin(Origin.BOTTOM_LEFT);

        addFeature(new EntitySfx(services, setup));
        addFeature(new EntityModel(services, setup));
    }

    @Override
    public void addAfter(Services services, Setup setup)
    {
        addFeature(new Routines());

        final EntityModel model = getFeature(EntityModel.class);
        addFeature(new EntityUpdater(services));
        addFeature(new EntityRenderer(services, model));
    }
}
