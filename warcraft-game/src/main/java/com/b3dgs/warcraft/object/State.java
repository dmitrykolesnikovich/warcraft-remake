/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.helper.StateHelper;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateHelper<EntityModel>
{
    /** Attacker reference. */
    protected final Attacker attacker = model.getFeature(Attacker.class);
    /** Producer reference. */
    protected final Producible producible = model.getFeature(Producible.class);
    /** Pathfindable reference. */
    protected final Pathfindable pathfindable = model.getFeature(Pathfindable.class);

    private final EntityStats stats = model.getFeature(EntityStats.class);
    private int frameOffsetOld;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected State(EntityModel model, Animation animation)
    {
        super(model, animation);
    }

    /**
     * Update mirror depending on orientation.
     */
    private void updateMirror()
    {
        final int orientation = pathfindable.getOrientation().ordinal();
        if (orientation > Orientation.ORIENTATIONS_NUMBER_HALF)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    /**
     * Update frame offset to match animation with orientation.
     */
    private void updateFrameOffset()
    {
        int frameOffset = pathfindable.getOrientation().ordinal();
        if (frameOffset != frameOffsetOld)
        {
            frameOffsetOld = frameOffset;
            if (stats.getHealthPercent() == 0)
            {
                frameOffset /= Orientation.ORIENTATIONS_NUMBER_HALF;
            }
            else if (frameOffset > Orientation.ORIENTATIONS_NUMBER_HALF)
            {
                frameOffset = Orientation.ORIENTATIONS_NUMBER - frameOffset;
            }
            rasterable.setAnimOffset(frameOffset * animation.getFrames());
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        frameOffsetOld = -1;
        updateFrameOffset();
        updateMirror();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        updateFrameOffset();
        updateMirror();
    }

    @Override
    public void exit()
    {
        super.exit();

        model.resetFlags();
    }
}
