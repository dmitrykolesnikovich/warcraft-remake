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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.helper.StateHelper;

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

    @Override
    public void exit()
    {
        super.exit();

        model.resetFlags();
    }
}
