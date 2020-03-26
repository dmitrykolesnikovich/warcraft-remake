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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateAbstract
{
    /** Model reference. */
    protected final EntityModel model;

    private final Animation animation;
    private final Mirrorable mirrorable;
    private final Animatable animatable;

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

        animatable = model.getFeature(Animatable.class);
        mirrorable = model.getFeature(Mirrorable.class);
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
        super.exit();

        model.resetFlags();
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
