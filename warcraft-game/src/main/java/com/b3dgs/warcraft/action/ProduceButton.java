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
package com.b3dgs.warcraft.action;

import java.util.List;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Build button action.
 */
public class ProduceButton extends ActionModel
{
    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ProduceButton(Services services, Setup setup)
    {
        super(services, setup);

        final Media target = Medias.create(setup.getText("media").split("/"));

        final Factory factory = services.get(Factory.class);
        final MapTile map = services.get(MapTile.class);
        actionable.setAction(() ->
        {
            final Featurable entity = factory.create(target);
            final Producible producible = entity.getFeature(Producible.class);
            producible.addListener(new ProducibleListener()
            {
                @Override
                public void notifyProductionStarted(Producer producer)
                {
                    // Nothing to do
                }

                @Override
                public void notifyProductionProgress(Producer producer)
                {
                    // Nothing to do
                }

                @Override
                public void notifyProductionEnded(Producer producer)
                {
                    final Pathfindable pathfindable = producible.getFeature(Pathfindable.class);
                    final CoordTile coord = map.getFeature(MapTilePath.class)
                                               .getFreeTileAround(pathfindable,
                                                                  producer.getFeature(Pathfindable.class));
                    pathfindable.setLocation(coord);
                }
            });
            final List<Selectable> selection = selector.getSelection();
            final int n = selection.size();
            for (int i = 0; i < n; i++)
            {
                final Producer producer = selection.get(i).getFeature(Producer.class);
                producer.addToProductionQueue(entity);
            }
        });
    }
}
