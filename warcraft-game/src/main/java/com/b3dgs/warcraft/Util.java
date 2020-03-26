/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Gfx;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.feature.Buildable;
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
     * @param mapPath The map path reference.
     * @param cut The cut tree.
     * @param transformable The transformable reference.
     * @return The next tree to cut, <code>null</code> if none.
     */
    public static Tile getClosestTree(MapTile map, MapTilePath mapPath, Tile cut, Transformable transformable)
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
                if (tree != null && Constant.CATEGORY_TREE.equals(mapPath.getCategory(tree)))
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
     * Get distance in tile.
     * 
     * @param map The map tile reference.
     * @param source The transformable source.
     * @param target The transformable target.
     * @return The distance in tile between transformable.
     */
    public static double getDistanceInTile(MapTile map, Transformable source, Transformable target)
    {
        return Math.round(UtilMath.getDistance(map.getInTileX(source),
                                               map.getInTileY(source),
                                               map.getInTileWidth(source),
                                               map.getInTileHeight(source),
                                               map.getInTileX(target),
                                               map.getInTileY(target),
                                               map.getInTileWidth(target),
                                               map.getInTileHeight(target)));
    }

    /**
     * Get distance in tile.
     * 
     * @param source The pathfindable source.
     * @param target The pathfindable target.
     * @return The distance in tile between transformable.
     */
    public static double getDistanceInTile(Tiled source, Tiled target)
    {
        return UtilMath.getDistance(source.getInTileX(), source.getInTileY(), target.getInTileX(), target.getInTileY());
    }

    /**
     * Teleport outside producer or production.
     * 
     * @param map The map tile reference.
     * @param producer The producer reference.
     * @param producible The producible reference.
     */
    public static void teleportOutside(MapTile map, Producer producer, Producible producible)
    {
        if (!producer.hasFeature(Buildable.class))
        {
            producer.getFeature(EntityModel.class).setVisible(true);
            Util.teleportOutside(map, producer, producible.getFeature(Pathfindable.class));
        }
        if (!producible.hasFeature(Buildable.class))
        {
            producible.getFeature(EntityModel.class).setVisible(true);
            Util.teleportOutside(map, producible, producer.getFeature(Pathfindable.class));
        }
    }

    /**
     * Teleport mover outside source.
     * 
     * @param map The map tile reference.
     * @param mover The mover reference.
     * @param source The source building.
     */
    public static void teleportOutside(MapTile map, FeatureProvider mover, Tiled source)
    {
        final Pathfindable pathfindable = mover.getFeature(Pathfindable.class);
        final CoordTile coord = map.getFeature(MapTilePath.class).getFreeTileAround(pathfindable, source);
        pathfindable.setLocation(coord);
    }

    /**
     * Switch extract and carry action depending of state.
     * 
     * @param actionable The actionable reference.
     * @param carry <code>true</code> if carry mode, <code>false</code> else.
     */
    public static void switchExtractCarryAction(Actionable actionable, boolean carry)
    {
        if (actionable.getDescription().startsWith(Constant.HUD_ACTION_CARRY))
        {
            actionable.setEnabled(carry);
        }
        else if (actionable.getDescription().startsWith(Constant.HUD_ACTION_EXTRACT))
        {
            actionable.setEnabled(!carry);
        }
    }

    /**
     * Private.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
