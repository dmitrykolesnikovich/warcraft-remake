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

import java.util.List;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.warcraft.Sfx;

/**
 * Effect implementation.
 */
public final class Effect extends FeaturableModel
{
    private static final String ANIM_IDLE = "idle";

    private final List<Sfx> sfx;
    private final SpriteAnimated surface;
    private final Animation animation;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Effect(Services services, Setup setup)
    {
        super();

        sfx = Sfx.load(setup, Sfx.ATT_DEAD);
        animation = AnimationConfig.imports(setup).getAnimation(ANIM_IDLE);

        addFeature(new LayerableModel(services, setup));
        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));

        final FramesConfig config = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        surface.setOrigin(Origin.MIDDLE);
        addFeature(new AnimatableModel(surface));

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(surface::update));
        addFeature(new DisplayableModel(g ->
        {
            surface.setLocation(viewer, transformable);
            surface.render(g);
        }));
    }

    /**
     * Start effect.
     * 
     * @param width The source width.
     * @param height The source height.
     */
    public void start(int width, int height)
    {
        surface.setFrameOffsets(-width / 2, -height / 2);
        surface.play(animation);
        Sfx.playRandom(sfx);
    }
}
