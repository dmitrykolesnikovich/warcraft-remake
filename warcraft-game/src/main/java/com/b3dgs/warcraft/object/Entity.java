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
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Entity representation base.
 */
public class Entity extends FeaturableModel
{
    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Entity(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(services, setup));
        addFeature(new TransformableModel(setup));
        addFeature(new SelectableModel());
        addFeature(new EntityStats(services, setup));

        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));

        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setGroup(Integer.valueOf(Constant.LAYER_ENTITY));
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setCollisionVisibility(false);
        collidable.setOrigin(Origin.BOTTOM_LEFT);

        final Producer producer = addFeatureAndGet(new ProducerModel(services, setup));
        producer.setStepsPerSecond(1.0);

        final MapTile map = services.get(MapTile.class);

        final EntityModel model = addFeatureAndGet(new EntityModel(services, setup));
        final SpriteAnimated surface = model.getSurface();

        final Producible producible = addFeatureAndGet(new ProducibleModel(setup));
        producible.addListener(new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted(Producer producer)
            {
                pathfindable.setLocation(map.getInTileX(producible), map.getInTileY(producible));
                surface.setFrame(1);
            }

            @Override
            public void notifyProductionProgress(Producer producer)
            {
                // Nothing to do
            }

            @Override
            public void notifyProductionEnded(Producer producer)
            {
                surface.setFrame(2);
            }
        });

        addFeature(new EntityUpdater(services));
        addFeature(new EntityRenderer(services, model));
    }
}
