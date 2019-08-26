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
package com.b3dgs.warcraft;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.TilePath;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Gfx;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Warehouse;

/**
 * Utility functions.
 */
public final class Util
{
    /**
     * Get image from path and set location.
     * 
     * @param gfx The image gfx.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The loaded image.
     */
    public static Image getImage(Gfx gfx, int x, int y)
    {
        final Image image = Drawable.loadImage(gfx.getSurface());
        image.setLocation(x, y);
        return image;
    }

    /**
     * Get owner warehouse.
     * 
     * @param services The services reference.
     * @param race The owner race.
     * @return The owner warehouse, <code>null</code> if none.
     */
    public static Warehouse getWarehouse(Services services, Race race)
    {
        final Handler handler = services.get(Handler.class);

        for (final Warehouse warehouse : handler.get(Warehouse.class))
        {
            if (race.equals(warehouse.getFeature(EntityStats.class).getRace()))
            {
                return warehouse;
            }
        }
        return null;
    }

    /**
     * Get the closest next tree around cut.
     * 
     * @param map The map tile reference.
     * @param cut The cut tree.
     * @param transformable The transformable reference.
     * @return The next tree to cut.
     */
    public static Tile getClosestTree(MapTile map, Tile cut, Transformable transformable)
    {
        double dist = Double.MAX_VALUE;
        Tile next = null;
        for (int tx = -1; tx < 2; tx++)
        {
            for (int ty = -1; ty < 2; ty++)
            {
                if (tx == 0 && ty == 0)
                {
                    continue;
                }
                final Tile tree = map.getTile(cut.getInTileX() + tx, cut.getInTileY() + ty);
                if (Constant.CATEGORY_TREE.equals(tree.getFeature(TilePath.class).getCategory()))
                {
                    final double cur = UtilMath.getDistance(tree, transformable);
                    if (cur < dist)
                    {
                        dist = cur;
                        next = tree;
                    }
                }
            }
        }
        return next;
    }

    /**
     * Private.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
