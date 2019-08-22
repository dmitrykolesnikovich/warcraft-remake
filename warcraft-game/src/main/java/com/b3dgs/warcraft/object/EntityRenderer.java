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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Entity rendering implementation.
 */
@FeatureInterface
public class EntityRenderer extends FeatureModel implements Displayable
{
    private final Viewer viewer;
    private final SpriteAnimated surface;
    private final Player player;
    private final FogOfWar fogOfWar;

    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Selectable selectable;
    @FeatureGet private Routines routines;
    @FeatureGet private EntityStats stats;

    private int animFrames;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public EntityRenderer(Services services, EntityModel model)
    {
        super();

        viewer = services.get(Viewer.class);
        player = services.get(Player.class);
        fogOfWar = services.get(FogOfWar.class);
        surface = model.getSurface();
    }

    /**
     * Draw entity selection area.
     * 
     * @param g The graphic output.
     */
    private void drawSelection(Graphic g)
    {
        g.setColor(player.getColor(stats.getRace()));
        g.drawRect(viewer, Origin.BOTTOM_LEFT, transformable, false);
    }

    /**
     * Update frame offset to match animation with orientation.
     */
    private void updateFrameOffset()
    {
        int frameOffset = pathfindable.getOrientation().ordinal();
        if (stats.getHealthPercent() == 0)
        {
            frameOffset /= Orientation.ORIENTATIONS_NUMBER_HALF;
        }
        else if (frameOffset > Orientation.ORIENTATIONS_NUMBER_HALF)
        {
            frameOffset = Orientation.ORIENTATIONS_NUMBER - frameOffset;
        }
        surface.setFrame(frameOffset * animFrames + animatable.getFrame());
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener(new AnimatorFrameListener()
        {
            @Override
            public void notifyAnimPlayed(Animation anim)
            {
                animFrames = anim.getFrames();
            }

            @Override
            public void notifyAnimFrame(int frame)
            {
                // Nothing to do
            }
        });
    }

    @Override
    public void render(Graphic g)
    {
        if (model.isVisible() && fogOfWar.isVisible(pathfindable))
        {
            updateFrameOffset();
            surface.setLocation(viewer, transformable);
            surface.setMirror(mirrorable.getMirror());

            if (model.isDisplay())
            {
                surface.render(g);
            }
            collidable.render(g);
            if (selectable.isSelected())
            {
                drawSelection(g);
            }

            routines.render(g);
        }
    }
}
