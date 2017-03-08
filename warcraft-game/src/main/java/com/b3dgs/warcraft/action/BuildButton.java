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
package com.b3dgs.warcraft.action;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerChecker;
import com.b3dgs.lionengine.game.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.warcraft.EntityStats;

/**
 * Build button action.
 */
public class BuildButton extends ActionModel
{
    private final Media target;
    private Rectangle area;

    @Service private Factory factory;
    @Service private Viewer viewer;
    @Service private Selector selector;
    @Service private MapTile map;

    /**
     * Create build button action.
     * 
     * @param setup The setup reference.
     */
    public BuildButton(Setup setup)
    {
        super(setup);

        target = Medias.create(setup.getText("media").split("/"));
    }

    @Override
    protected void action()
    {
        final SizeConfig size = SizeConfig.imports(new Xml(target));
        area = new Rectangle(0, 0, size.getWidth(), size.getHeight());
        cursor.setVisible(false);
    }

    @Override
    protected void assign()
    {
        for (final Selectable selectable : selector.getSelection())
        {
            final Featurable building = factory.create(target);
            final Producible producible = building.getFeature(Producible.class);
            producible.setLocation(area.getX(), area.getY());

            final Producer producer = selectable.getFeature(Producer.class);
            final Transformable transformable = producer.getFeature(Transformable.class);
            producer.setChecker(new ProducerChecker()
            {
                @Override
                public boolean checkProduction(Featurable featurable)
                {
                    return UtilMath.getDistance(featurable.getFeature(Producible.class),
                                                transformable) < map.getTileWidth();
                }
            });

            producer.addToProductionQueue(building);

            final Pathfindable pathfindable = producer.getFeature(Pathfindable.class);
            pathfindable.setDestination(area);

            final EntityStats stats = producer.getFeature(EntityStats.class);
            producer.addListener(new ProducerListener()
            {
                @Override
                public void notifyStartProduction(Featurable featurable)
                {
                    stats.setVisible(false);
                }

                @Override
                public void notifyProducing(Featurable featurable)
                {
                    // Nothing to do
                }

                @Override
                public void notifyProduced(Featurable featurable)
                {
                    pathfindable.clearPath();

                    final CoordTile coord = map.getFeature(MapTilePath.class)
                                               .getFreeTileAround(pathfindable,
                                                                  featurable.getFeature(Pathfindable.class));
                    pathfindable.setLocation(coord);

                    stats.setVisible(true);
                }

                @Override
                public void notifyCanNotProduce(Featurable featurable)
                {
                    // Nothing to do
                }
            });
        }
        area = null;
        cursor.setVisible(true);
    }

    @Override
    protected void update(double extrp)
    {
        if (area != null)
        {
            area.set(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                     UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                     area.getWidthReal(),
                     area.getHeightReal());
        }
    }

    @Override
    protected void render(Graphic g)
    {
        if (area != null && viewer.isViewable((Localizable) cursor, 0, 0))
        {
            g.setColor(ColorRgba.GREEN);
            g.drawRect(viewer, Origin.BOTTOM_LEFT, area.getX(), area.getY(), area.getWidth(), area.getHeight(), false);
        }
    }
}
