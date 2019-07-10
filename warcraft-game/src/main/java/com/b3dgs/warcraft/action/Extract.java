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

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Extract action.
 */
public class Extract extends ActionModel
{
    private final MapTilePath mapPath = map.getFeature(MapTilePath.class);

    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Extract(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    protected void assign()
    {
        final int tx = map.getInTileX(cursor);
        final int ty = map.getInTileY(cursor);

        final List<Selectable> selection = selector.getSelection();
        final int n = selection.size();
        for (int i = 0; i < n; i++)
        {
            final Extractor extractor = selection.get(i).getFeature(Extractor.class);
            for (final Integer id : mapPath.getObjectsId(tx, ty))
            {
                final Featurable featurable = handler.get(id);
                if (featurable.hasFeature(Extractable.class))
                {
                    final Extractable extractable = featurable.getFeature(Extractable.class);
                    extractor.setResource(extractable);
                    extractor.getFeature(Pathfindable.class).setDestination(extractable);
                    extractor.startExtraction();
                }
            }
        }
    }
}
