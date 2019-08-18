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
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.warcraft.Sfx;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Folder;
import com.b3dgs.warcraft.object.EntityModel;

/**
 * Represents something that can be built.
 */
@FeatureInterface
public class Buildable extends FeatureModel implements Routine, Recyclable, ProducibleListener
{
    private static final Animation PHASE1 = new Animation("phase1", 1, 1, 1.0, false, false);
    private static final Animation PHASE2 = new Animation("phase2", 2, 2, 1.0, false, false);

    private final SpriteAnimated building;

    private final Viewer viewer;
    private final Renderable effect;

    private Renderable renderable;
    private int phase;

    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Buildable(Services services, Setup setup)
    {
        super();

        viewer = services.get(Viewer.class);

        building = Drawable.loadSpriteAnimated(Medias.create(Folder.EFFECTS, "construction.png"), 3, 1);
        building.setOrigin(Origin.CENTER_BOTTOM);
        building.load();
        building.prepare();

        effect = g ->
        {
            building.setLocation(viewer, transformable);
            building.render(g);
        };
    }

    /**
     * Change construction phase.
     * 
     * @param phase The phase number.
     * @param display <code>true</code> to display unit, <code>false</code> else.
     */
    private void changePhase(int phase, boolean display)
    {
        this.phase = phase;
        model.setDisplay(display);
        if (display)
        {
            renderable = RenderableVoid.getInstance();
        }
        else
        {
            renderable = effect;
        }
        if (phase > 0)
        {
            Sfx.NEUTRAL_CONSTRUCT.play();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        building.setFrameOffsets(-transformable.getWidth() / 2, 0);
    }

    @Override
    public void render(Graphic g)
    {
        renderable.render(g);
    }

    @Override
    public void recycle()
    {
        building.stop();
        renderable = RenderableVoid.getInstance();
        phase = 0;
    }

    @Override
    public void notifyProductionStarted(Producer producer)
    {
        building.play(PHASE1);
        changePhase(0, false);
    }

    @Override
    public void notifyProductionProgress(Producer producer)
    {
        final int percent = producer.getProgressPercent();
        if (phase == 0 && percent > Constant.CONSTRUCT_PERCENT_PHASE1)
        {
            building.play(PHASE2);
            changePhase(1, false);
        }
        else if (phase == 1 && percent > Constant.CONSTRUCT_PERCENT_PHASE2)
        {
            building.stop();
            changePhase(2, true);
        }
    }

    @Override
    public void notifyProductionEnded(Producer producer)
    {
        // Nothing to do
    }
}
