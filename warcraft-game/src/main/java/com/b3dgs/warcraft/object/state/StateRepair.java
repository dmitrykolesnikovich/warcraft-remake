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
package com.b3dgs.warcraft.object.state;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.State;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Repair state implementation.
 */
final class StateRepair extends State
{
    private final Attacker attacker = model.getFeature(Attacker.class);
    private final Pathfindable pathfindable = model.getFeature(Pathfindable.class);
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateRepair(EntityModel model, Animation animation)
    {
        super(model, animation);

        this.animation = animation;

        final EntityStats stats = model.getFeature(EntityStats.class);

        addTransition(StateIdle.class, () -> is(AnimState.FINISHED));
        addTransition(StateDie.class, () -> stats.getHealthPercent() == 0);
    }

    @Override
    public void enter()
    {
        super.enter();

        pathfindable.pointTo(attacker.getTarget().getFeature(Pathfindable.class));
        attacker.setAttackFrame(animation.getLast());
    }

    @Override
    public void update(double extrp)
    {
        if (attacker.getTarget().getFeature(EntityStats.class).isFullHealth())
        {
            attacker.stopAttack();
        }
    }
}
