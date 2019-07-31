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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Folder;

/**
 * Represents something that can burn.
 */
@FeatureInterface
public class Burnable extends FeatureModel implements Routine, Recyclable
{
    private final Animation light = new Animation("light", 1, 4, 0.2, false, true);
    private final Animation strong = new Animation("strong", 5, 8, 0.2, false, true);
    private final SpriteAnimated burn;

    private final Viewer viewer;
    private final Renderable effect;
    private Renderable renderable = RenderableVoid.getInstance();

    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityStats stats;

    private int oldLife;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Burnable(Services services, Setup setup)
    {
        super();

        viewer = services.get(Viewer.class);

        burn = Drawable.loadSpriteAnimated(Medias.create(Folder.EFFECTS, "burning.png"), 4, 2);
        burn.setOrigin(Origin.CENTER_BOTTOM);
        burn.load();
        burn.prepare();

        effect = g ->
        {
            burn.setLocation(viewer, transformable);
            burn.render(g);
        };
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        burn.setFrameOffsets(-transformable.getWidth() / 2, -transformable.getHeight() / 2);
    }

    @Override
    public void update(double extrp)
    {
        final int current = stats.getLife();
        if (current != oldLife)
        {
            if (current == 0)
            {
                renderable = RenderableVoid.getInstance();
            }
            else if (current < Constant.HEALTH_PERCENT_ALERT)
            {
                burn.play(strong);
                renderable = effect;
            }
            else if (current < Constant.HEALTH_PERCENT_WARN)
            {
                burn.play(light);
                renderable = effect;
            }
            else
            {
                burn.stop();
                renderable = RenderableVoid.getInstance();
            }
            oldLife = current;
        }
        if (current < Constant.HEALTH_PERCENT_WARN)
        {
            burn.update(extrp);
        }
    }

    @Override
    public void render(Graphic g)
    {
        renderable.render(g);
    }

    @Override
    public void recycle()
    {
        burn.stop();
        renderable = RenderableVoid.getInstance();
        oldLife = 0;
    }
}
