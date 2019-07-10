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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListener;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListenerVoid;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateAbstract
{
    /** Map reference. */
    protected final MapTile map;

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

        map = model.getMap();

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

        pathfindable.addListener(new PathfindableListenerVoid()
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
        });
        attacker.addListener(new AttackerListenerVoid()
        {
            @Override
            public void notifyAttackStarted(Transformable target)
            {
                attackStarted.set(true);
            }
        });
        producible.addListener(new ProducibleListenerVoid()
        {
            @Override
            public void notifyProductionEnded(Producer producer)
            {
                producibleEnded.set(true);
            }
        });
        extractor.addListener(new ExtractorListener()
        {
            @Override
            public void notifyStartGoToRessources(Enum<?> type, Tiled resourceLocation)
            {
                // Nothing to do
            }

            @Override
            public void notifyStartExtraction(Enum<?> type, Tiled resourceLocation)
            {
                // Nothing to do
            }

            @Override
            public void notifyStartDropOff(Enum<?> type, int totalQuantity)
            {
                // Nothing to do
            }

            @Override
            public void notifyStartCarry(Enum<?> type, int totalQuantity)
            {
                // Nothing to do
            }

            @Override
            public void notifyExtracted(Enum<?> type, int currentQuantity)
            {
                // Nothing to do

            }

            @Override
            public void notifyDroppedOff(Enum<?> type, int droppedQuantity)
            {
                // Nothing to do
            }
        });
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

    @Override
    public void enter()
    {
        animatable.play(animation);
    }

    @Override
    public void exit()
    {
        moveStarted.set(false);
        moveArrived.set(false);
        attackStarted.set(false);
        producibleEnded.set(false);
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
