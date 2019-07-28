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

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.feature.tile.map.Orientable;
import com.b3dgs.lionengine.game.feature.tile.map.OrientableModel;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Projectile implementation.
 */
public final class Projectile extends FeaturableModel
{
    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Projectile(Services services, Setup setup)
    {
        super();

        addFeature(new LayerableModel(services, setup));
        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));
        final Orientable orientable = addFeatureAndGet(new OrientableModel(services));
        final Launchable launchable = addFeatureAndGet(new LaunchableModel());
        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setOrigin(Origin.MIDDLE);
        collidable.setGroup(Integer.valueOf(Constant.LAYER_PROJECTILE));
        collidable.addAccept(Integer.valueOf(Constant.LAYER_ENTITY));

        final SizeConfig config = SizeConfig.imports(setup);
        final SpriteTiled sprite = Drawable.loadSpriteTiled(setup.getSurface(), config.getWidth(), config.getHeight());
        sprite.setOrigin(Origin.MIDDLE);

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(extrp ->
        {
            launchable.update(extrp);
            sprite.setTile(orientable.getOrientation().ordinal());
        }));

        addFeature(new DisplayableModel(g ->
        {
            sprite.setLocation(viewer, transformable);
            sprite.render(g);
            collidable.render(g);
        }));
    }
}
