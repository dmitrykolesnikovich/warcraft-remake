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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.object.state.StateDead;
import com.b3dgs.warcraft.object.state.StateDeadGold;
import com.b3dgs.warcraft.object.state.StateDeadWood;

/**
 * Represents something that can decay.
 */
@FeatureInterface
public class Decayable extends FeatureModel implements Routine, Recyclable
{
    private static final String NODE_DECAY = "decay";
    private static final String ATT_CORPSE = "corpse";
    private static final String ATT_DELAY = "delay";

    private final Tick tick = new Tick();
    private final Updatable checkDead;
    private final Updatable checkCorpse;

    private Updatable check;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private EntityStats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Decayable(Services services, Setup setup)
    {
        super(services, setup);

        final Spawner spawner = services.get(Spawner.class);

        final int delay = setup.getIntegerDefault(0, ATT_DELAY, NODE_DECAY);
        final Media media = Medias.create(setup.getString(ATT_CORPSE, NODE_DECAY));

        checkCorpse = extrp ->
        {
            tick.update(extrp);
            if (tick.elapsed(delay))
            {
                ((com.b3dgs.warcraft.object.Effect) spawner.spawn(media,
                                                                  transformable)).start(transformable.getWidth(),
                                                                                        transformable.getHeight());
                pathfindable.clearPath();
                identifiable.destroy();
            }
        };
        checkDead = extrp ->
        {
            if (!tick.isStarted() && animatable.is(AnimState.FINISHED) && isDead())
            {
                tick.start();
                check = checkCorpse;
            }
        };
    }

    /**
     * Check if dead.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    private boolean isDead()
    {
        return stateHandler.isState(StateDead.class)
               || stateHandler.isState(StateDeadGold.class)
               || stateHandler.isState(StateDeadWood.class);
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
    }

    @Override
    public void recycle()
    {
        tick.stop();
        check = checkDead;
    }
}
