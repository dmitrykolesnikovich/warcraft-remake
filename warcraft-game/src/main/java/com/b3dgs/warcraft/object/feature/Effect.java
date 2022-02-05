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
package com.b3dgs.warcraft.object.feature;

import java.util.List;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.warcraft.Sfx;

/**
 * Effect implementation.
 */
@FeatureInterface
public final class Effect extends FeatureModel implements Routine
{
    private static final String NODE_EFFECT = "effect";
    private static final String ATT_DELAY = "delay";
    private static final String ANIM_IDLE = "idle";

    private final Tick tick = new Tick();
    private final List<Sfx> sfx;
    private final Animation animation;
    private final int delay;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Effect(Services services, Setup setup)
    {
        super(services, setup);

        sfx = Sfx.load(setup, Sfx.ATT_DEAD);
        animation = AnimationConfig.imports(setup).getAnimation(ANIM_IDLE);
        delay = setup.getInteger(-1, ATT_DELAY, NODE_EFFECT);
    }

    /**
     * Start effect.
     * 
     * @param width The source width.
     * @param height The source height.
     */
    public void start(int width, int height)
    {
        rasterable.setFrameOffsets(-width / 2, height / 2);
        animatable.play(animation);
        Sfx.playRandom(sfx);
        tick.start();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (delay < 0)
        {
            animatable.addListener((AnimatorStateListener) state ->
            {
                if (AnimState.FINISHED == state)
                {
                    identifiable.destroy();
                }
            });
        }
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (delay > -1 && tick.elapsed(delay))
        {
            identifiable.destroy();
        }
    }
}
