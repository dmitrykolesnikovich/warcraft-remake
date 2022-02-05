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
package com.b3dgs.warcraft.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.State;
import com.b3dgs.warcraft.object.feature.EntitySfx;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Extract wood state implementation.
 */
final class StateExtractWood extends State
{
    private final EntitySfx sfx = model.getFeature(EntitySfx.class);
    private final Extractor extractor = model.getFeature(Extractor.class);
    private final AnimatorFrameListener listener;
    private final double old;
    private boolean cut;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateExtractWood(EntityModel model, Animation animation)
    {
        super(model, animation);

        final Animatable animatable = model.getFeature(Animatable.class);
        listener = frame ->
        {
            if (!cut && animatable.getFrame() == animation.getLast())
            {
                cut = true;
                sfx.onAttacked();
            }
            else if (animatable.getFrame() == animation.getFirst())
            {
                cut = false;
            }
        };

        final EntityStats stats = model.getFeature(EntityStats.class);

        addTransition(StateCarryWood.class, () -> Constant.RESOURCE_WOOD.equals(model.getCarryResource()));
        addTransition(StateDie.class, () -> stats.getHealthPercent() == 0);
        addTransition(StateIdle.class, () -> !pathfindable.isMoving() && !model.isGotoResource());
        addTransition(StateWalk.class,
                      () -> pathfindable.isMoving()
                            && model.getExtractResource() == null
                            && model.getCarryResource() == null);

        old = extractor.getExtractionSpeed();
    }

    @Override
    public void enter()
    {
        super.enter();

        animatable.addListener(listener);
        cut = false;
        extractor.setExtractionSpeed(old / 12);
    }

    @Override
    public void exit()
    {
        super.exit();

        animatable.removeListener(listener);
        extractor.setExtractionSpeed(old);
    }
}
