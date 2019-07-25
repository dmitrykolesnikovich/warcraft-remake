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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListener;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListener;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListener;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.TilePath;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.warcraft.Resources;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Warehouse;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateAbstract
{
    /** Handler reference. */
    protected final Handler handler;
    /** Map reference. */
    protected final MapTile map;
    /** Map path reference. */
    protected final MapTilePath mapPath;
    /** Map transition reference. */
    protected final MapTileTransition mapTransition;
    /** Resources reference. */
    protected final Resources resources;

    /** Identifiable reference. */
    protected final Identifiable identifiable;
    /** Model reference. */
    protected final EntityModel model;
    /** Animatable reference. */
    protected final Animatable animatable;
    /** Transformable reference. */
    protected final Transformable transformable;
    /** Pathfindable reference. */
    protected final Pathfindable pathfindable;
    /** Attacker reference. */
    protected final Attacker attacker;
    /** Producible reference. */
    protected final Producible producible;
    /** Extractor reference. */
    protected final Extractor extractor;
    /** Mirrorable reference. */
    protected final Mirrorable mirrorable;
    /** Collidable reference. */
    protected final Collidable collidable;
    /** State animation data. */
    protected final Animation animation;
    /** Stats reference. */
    protected final EntityStats stats;

    /** Move started flag. */
    protected final AtomicBoolean moveStarted = new AtomicBoolean();
    /** Move arrived flag. */
    protected final AtomicBoolean moveArrived = new AtomicBoolean();

    /** Attack started flag. */
    protected final AtomicBoolean attackStarted = new AtomicBoolean();

    /** Producible ended flag. */
    protected final AtomicBoolean producibleEnded = new AtomicBoolean();

    /** Extract resource flag. */
    protected final AtomicReference<String> extractResource = new AtomicReference<>();
    /** Carry resource flag. */
    protected final AtomicReference<String> carryResource = new AtomicReference<>();

    private final PathfindableListener pathfindableListener = new PathfindableListenerVoid()
    {
        @Override
        public void notifyStartMove()
        {
            moveStarted.set(true);
        }

        @Override
        public void notifyArrived()
        {
            moveArrived.set(true);
        }
    };
    private final AttackerListener attackerListener = new AttackerListenerVoid()
    {
        @Override
        public void notifyAttackStarted(Transformable target)
        {
            attackStarted.set(true);
        }
    };
    private final ProducibleListener producibleListener = new ProducibleListenerVoid()
    {
        @Override
        public void notifyProductionEnded(Producer producer)
        {
            producibleEnded.set(true);
        }
    };
    private final ExtractorListener extractorListener = new ExtractorListenerVoid()
    {
        @Override
        public void notifyStartGoToRessources(String type, Tiled resourceLocation)
        {
            pathfindable.setDestination(resourceLocation);
            carryResource.set(null);
        }

        @Override
        public void notifyStartExtraction(String type, Tiled resourceLocation)
        {
            extractResource.set(type);
            if (Resources.TYPE_WOOD.equals(type))
            {
                pathfindable.pointTo(resourceLocation);
            }
        }

        @Override
        public void notifyStartCarry(String type, int totalQuantity)
        {
            pathfindable.setDestination(handler.get(Warehouse.class).iterator().next());
            carryResource.set(type);

            if (Resources.TYPE_WOOD.equals(type))
            {
                final Tile tile = mapPath.getTile(extractor.getResourceLocation());
                final Tile cut = map.createTile(tile.getSheet(), Constant.TILE_NUM_TREE_CUT, tile.getX(), tile.getY());
                mapPath.loadTile(cut);
                map.setTile(cut);

                for (final Tile updated : mapTransition.resolve(cut))
                {
                    mapPath.loadTile(updated);
                    map.setTile(updated);
                }

                final Tile next = getClosestTree(cut);
                if (next != null)
                {
                    extractor.setResource(type, next);
                }
            }
        }

        @Override
        public void notifyStartDropOff(String type, int totalQuantity)
        {
            model.setVisible(false);
            if (Resources.TYPE_WOOD.equals(type))
            {
                resources.increaseWood(totalQuantity);
            }
            else if (Resources.TYPE_GOLD.equals(type))
            {
                resources.increaseGold(totalQuantity);
            }
        }

        @Override
        public void notifyDroppedOff(String type, int droppedQuantity)
        {
            if (droppedQuantity == 0)
            {
                model.setVisible(true);
            }
        }
    };

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected State(EntityModel model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;

        final Services services = model.getServices();
        handler = services.get(Handler.class);
        map = services.get(MapTile.class);
        mapPath = map.getFeature(MapTilePath.class);
        mapTransition = map.getFeature(MapTileTransition.class);
        resources = services.get(Resources.class);

        identifiable = model.getFeature(Identifiable.class);
        animatable = model.getFeature(Animatable.class);
        transformable = model.getFeature(Transformable.class);
        pathfindable = model.getFeature(Pathfindable.class);
        attacker = model.getFeature(Attacker.class);
        producible = model.getFeature(Producible.class);
        extractor = model.getFeature(Extractor.class);
        mirrorable = model.getFeature(Mirrorable.class);
        collidable = model.getFeature(Collidable.class);
        stats = model.getFeature(EntityStats.class);
    }

    /**
     * Check if is anim state.
     * 
     * @param state The expected anim state.
     * @return <code>true</code> if is state, <code>false</code> else.
     */
    protected final boolean is(AnimState state)
    {
        return animatable.is(state);
    }

    /**
     * Check if is current mirror state.
     * 
     * @param mirror The expected mirror to be.
     * @return <code>true</code> if is mirror, <code>false</code> else.
     */
    protected final boolean is(Mirror mirror)
    {
        return mirrorable.is(mirror);
    }

    private Tile getClosestTree(Tile cut)
    {
        double dist = Double.MAX_VALUE;
        Tile next = null;
        for (int tx = -1; tx < 2; tx++)
        {
            for (int ty = -1; ty < 2; ty++)
            {
                if (tx == 0 && ty == 0)
                {
                    continue;
                }
                final Tile tree = map.getTile(cut.getInTileX() + tx, cut.getInTileY() + ty);
                if (Constant.CATEGORY_TREE.equals(tree.getFeature(TilePath.class).getCategory()))
                {
                    final double cur = UtilMath.getDistance(tree, transformable);
                    if (cur < dist)
                    {
                        dist = cur;
                        next = tree;
                    }
                }
            }
        }
        return next;
    }

    @Override
    public void enter()
    {
        animatable.play(animation);
        pathfindable.addListener(pathfindableListener);
        attacker.addListener(attackerListener);
        producible.addListener(producibleListener);
        extractor.addListener(extractorListener);
    }

    @Override
    public void exit()
    {
        pathfindable.removeListener(pathfindableListener);
        attacker.removeListener(attackerListener);
        producible.removeListener(producibleListener);
        extractor.removeListener(extractorListener);

        moveStarted.set(false);
        moveArrived.set(false);
        attackStarted.set(false);
        producibleEnded.set(false);
        extractResource.set(null);
        carryResource.set(null);
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void update(double extrp)
    {
        // Nothing by default
    }
}
