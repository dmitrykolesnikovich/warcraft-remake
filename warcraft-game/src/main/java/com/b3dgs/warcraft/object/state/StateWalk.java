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
package com.b3dgs.warcraft.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.State;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Repairer;

/**
 * Walk state implementation.
 */
final class StateWalk extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateWalk(EntityModel model, Animation animation)
    {
        super(model, animation);

        final EntityStats stats = model.getFeature(EntityStats.class);
        final boolean repairer = model.hasFeature(Repairer.class);

        addTransition(StateExtractWood.class,
                      () -> model.isMoveArrived() && Constant.RESOURCE_WOOD.equals(model.getExtractResource()));
        addTransition(StateExtractGold.class,
                      () -> model.isMoveArrived() && Constant.RESOURCE_GOLD.equals(model.getExtractResource()));
        addTransition(StateIdle.class,
                      () -> model.isMoveArrived() && !model.isAttackStarted() && model.getExtractResource() == null);
        addTransition(StateAttack.class, () -> model.isMoveArrived() && model.isAttackStarted() && !repairer);
        addTransition(StateRepair.class, () -> model.isMoveArrived() && model.isAttackStarted() && repairer);
        addTransition(StateDie.class, () -> stats.getHealthPercent() == 0);
    }
}
