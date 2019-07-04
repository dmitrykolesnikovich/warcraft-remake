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
package com.b3dgs.warcraft.world;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.warcraft.constant.Constant;

/**
 * World navigator.
 */
public class WorldNavigator implements Updatable
{
    private final Camera camera;
    private final Cursor cursor;
    private final MapTile map;
    private final Selector selector;
    private final InputDevicePointer pointer;
    private final InputDeviceDirectional directional;

    /**
     * Create the navigator.
     * 
     * @param services The services reference.
     */
    public WorldNavigator(Services services)
    {
        super();

        camera = services.get(Camera.class);
        cursor = services.get(Cursor.class);
        map = services.get(MapTile.class);
        selector = services.get(Selector.class);
        pointer = services.get(InputDevicePointer.class);
        directional = services.get(InputDeviceDirectional.class);
    }

    /**
     * Update map navigation with directional device.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigationDirectional(double extrp)
    {
        if (directional.getVerticalDirection() > 0)
        {
            camera.moveLocation(extrp, 0, map.getTileHeight());
        }
        else if (directional.getVerticalDirection() < 0)
        {
            camera.moveLocation(extrp, 0, -map.getTileHeight());
        }
        if (directional.getHorizontalDirection() < 0)
        {
            camera.moveLocation(extrp, -map.getTileWidth(), 0);
        }
        else if (directional.getHorizontalDirection() > 0)
        {
            camera.moveLocation(extrp, map.getTileWidth(), 0);
        }
    }

    /**
     * Update map navigation with pointer device.
     * 
     * @param extrp The extrapolation value.
     * @return <code>true</code> if map moved, <code>false</code> else.
     */
    private boolean updateNavigationPointer(double extrp)
    {
        boolean updated = false;
        if (pointer.getClick() > 1)
        {
            final int h = camera.getViewY() + camera.getHeight() - map.getTileHeight();
            final int marginY = map.getTileHeight() / 2;

            if (UtilMath.isBetween(pointer.getY(), h, h + marginY))
            {
                camera.moveLocation(extrp, 0, -map.getTileHeight());
                updated = true;
            }
            else if (UtilMath.isBetween(pointer.getY(), camera.getViewY(), camera.getViewY() + marginY))
            {
                camera.moveLocation(extrp, 0, map.getTileHeight());
                updated = true;
            }

            final int w = camera.getViewX() + camera.getWidth() - map.getTileWidth();
            final int marginX = map.getTileWidth() / 2;

            if (UtilMath.isBetween(pointer.getX(), camera.getViewX(), camera.getViewX() + marginX))
            {
                camera.moveLocation(extrp, -map.getTileWidth(), 0);
                updated = true;
            }
            else if (UtilMath.isBetween(pointer.getX(), w, w + marginX))
            {
                camera.moveLocation(extrp, map.getTileWidth(), 0);
                updated = true;
            }
        }
        return updated;
    }

    /**
     * Update map navigation with minimap.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigationMinimap(double extrp)
    {
        if (pointer.getClick() > 0
            && UtilMath.isBetween(pointer.getX(), Constant.MINIMAP_X, Constant.MINIMAP_X + map.getInTileWidth())
            && UtilMath.isBetween(pointer.getY(), Constant.MINIMAP_Y, Constant.MINIMAP_Y + map.getInTileHeight()))
        {
            final int x = (pointer.getX() - Constant.MINIMAP_X) * map.getTileWidth();
            final int y = (map.getInTileHeight() + Constant.MINIMAP_Y - pointer.getY()) * map.getTileHeight();
            camera.setLocation(x - camera.getWidth() / 2.0, y - camera.getHeight() / 2.0);
        }
    }

    /**
     * Check if cursor is inside map view.
     * 
     * @return <code>true</code> if over map, <code>false</code> on Hud.
     */
    private boolean isCursorOverMap()
    {
        return UtilMath.isBetween(pointer.getX(), camera.getViewX(), camera.getViewX() + camera.getWidth() - 8)
               && UtilMath.isBetween(pointer.getY(), camera.getViewY(), camera.getViewY() + camera.getHeight() - 16);
    }

    @Override
    public void update(double extrp)
    {
        updateNavigationDirectional(extrp);
        updateNavigationMinimap(extrp);

        if (!updateNavigationPointer(extrp) && isCursorOverMap() && cursor.hasClickedOnce(3))
        {
            for (final Selectable selectable : selector.getSelection())
            {
                selectable.getFeature(Pathfindable.class).setDestination(cursor);
            }
        }
    }
}
