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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Effect feature implementation.
 */
@FeatureInterface
public final class Effect extends FeatureModel
{
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Animatable animatable;

    /**
     * Create effect.
     * 
     * @param setup The setup reference.
     */
    public Effect(Setup setup)
    {
        super();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (AnimState.FINISHED == state)
            {
                identifiable.destroy();
            }
        });
    }
}
