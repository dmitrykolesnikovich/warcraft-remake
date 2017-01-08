/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerChecker;
import com.b3dgs.lionengine.game.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.selector.Selectable;
import com.b3dgs.lionengine.game.feature.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Entity representation base.
 */
public class Entity extends FeaturableModel
{
    private static final int FRAME_OFFSET = 8;

    private boolean visible = true;

    @Service private MapTile map;
    @Service private Viewer viewer;

    /**
     * Create entity.
     * 
     * @param setup The setup reference.
     */
    public Entity(Setup setup)
    {
        super(setup);

        addFeature(new LayerableModel(Constant.LAYER_ENTITY));
        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));
        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(setup));

        final FramesConfig config = FramesConfig.imports(setup);
        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(),
                                                                   config.getHorizontal(),
                                                                   config.getVertical());
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-FRAME_OFFSET, -FRAME_OFFSET);

        final Collidable collidable = addFeatureAndGet(new CollidableModel(setup));
        collidable.setGroup(Constant.LAYER_ENTITY);
        collidable.addCollision(Collision.AUTOMATIC);

        final Producer producer = addFeatureAndGet(new ProducerModel(setup));
        producer.setStepsPerSecond(1.0);
        producer.setChecker(new ProducerChecker()
        {
            @Override
            public boolean checkProduction(Featurable featurable)
            {
                final Producible producible = featurable.getFeature(Producible.class);
                return UtilMath.isBetween(transformable.getX(),
                                          producible.getX(),
                                          producible.getX() + producible.getWidth())
                       && UtilMath.isBetween(transformable.getY(),
                                             producible.getY() - producible.getHeight(),
                                             producible.getY());
            }
        });
        producer.addListener(new ProducerListener()
        {
            @Override
            public void notifyStartProduction(Featurable featurable)
            {
                visible = false;
            }

            @Override
            public void notifyProducing(Featurable featurable)
            {
            }

            @Override
            public void notifyProduced(Featurable featurable)
            {
                visible = true;
                final Transformable transformable = featurable.getFeature(Transformable.class);
                pathfindable.setLocation(map.getInTileX(transformable) - 2, map.getInTileY(transformable));
            }

            @Override
            public void notifyCanNotProduce(Featurable featurable)
            {
            }
        });

        final Producible producible = addFeatureAndGet(new ProducibleModel(setup));
        producible.addListener(new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted()
            {
            }

            @Override
            public void notifyProductionProgress()
            {
            }

            @Override
            public void notifyProductionEnded()
            {
                surface.setFrame(2);
            }
        });

        final Selectable selectable = addFeatureAndGet(new SelectableModel());
        addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                pathfindable.update(extrp);
                producer.update(extrp);
                surface.setLocation(viewer, transformable);
            }
        }));

        addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                if (visible)
                {
                    surface.render(g);
                    if (selectable.isSelected())
                    {
                        g.setColor(ColorRgba.GREEN);
                        g.drawRect(viewer,
                                   Origin.MIDDLE,
                                   transformable.getX() + FRAME_OFFSET,
                                   transformable.getY() + FRAME_OFFSET,
                                   transformable.getWidth(),
                                   transformable.getHeight(),
                                   false);
                    }
                }
            }
        }));
    }
}
