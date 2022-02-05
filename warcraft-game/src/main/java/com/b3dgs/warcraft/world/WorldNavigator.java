/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.List;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectorModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.warcraft.DeviceMapping;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.RightClickHandler;

/**
 * World navigator.
 */
public class WorldNavigator implements Updatable
{
    private static final int NAVIGATION_TICK = 3;

    private final Tick navigationDelay = new Tick();

    private final Camera camera;
    private final Cursor cursor;
    private final DeviceController device;
    private final Handler handler;
    private final MapTile map;
    private final MapTilePath mapPath;
    private final FogOfWar fogOfWar;
    private final Selector selector;
    private final SelectorModel selectorModel;

    private boolean selectorEnabled;
    private boolean selectorBackup;

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
        device = services.get(DeviceController.class);
        handler = services.get(Handler.class);
        map = services.get(MapTile.class);
        mapPath = map.getFeature(MapTilePath.class);
        fogOfWar = map.getFeature(FogOfWar.class);
        selector = services.get(Selector.class);

        selectorModel = selector.getFeature(SelectorModel.class);
        selectorEnabled = selectorModel.isEnabled();
        navigationDelay.start();
    }

    /**
     * Update map navigation with pointer device.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigationPointer(double extrp)
    {
        if (cursor.isPushed())
        {
            final int h = camera.getViewY() + camera.getHeight();
            final int marginY = map.getTileHeight() / 2;

            if (UtilMath.isBetween(cursor.getScreenY(), h - marginY, h))
            {
                camera.moveLocation(extrp, 0, -map.getTileHeight());
            }
            else if (UtilMath.isBetween(cursor.getScreenY(), camera.getViewY(), camera.getViewY() + marginY))
            {
                camera.moveLocation(extrp, 0, map.getTileHeight());
            }

            final int w = camera.getViewX() + camera.getWidth();
            final int marginX = map.getTileWidth() / 2;

            if (UtilMath.isBetween(cursor.getScreenX(), camera.getViewX(), camera.getViewX() + marginX))
            {
                camera.moveLocation(extrp, -map.getTileWidth(), 0);
            }
            else if (UtilMath.isBetween(cursor.getScreenX(), w - marginX, w))
            {
                camera.moveLocation(extrp, map.getTileWidth(), 0);
            }
        }
    }

    /**
     * Update map navigation with minimap.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigationMinimap(double extrp)
    {
        if (!selectorModel.isSelecting()
            && cursor.isPushed()
            && UtilMath.isBetween(cursor.getScreenX(), Constant.MINIMAP_X, Constant.MINIMAP_X + map.getInTileWidth())
            && UtilMath.isBetween(cursor.getScreenY(), Constant.MINIMAP_Y, Constant.MINIMAP_Y + map.getInTileHeight()))
        {
            final double x = (cursor.getScreenX() - Constant.MINIMAP_X) * map.getTileWidth();
            final double y = (map.getInTileHeight() + Constant.MINIMAP_Y - cursor.getScreenY()) * map.getTileHeight();
            camera.setLocation(UtilMath.getRounded(x - camera.getWidth() / 2.0, map.getTileWidth()),
                               UtilMath.getRounded(y - camera.getHeight() / 2.0, map.getTileHeight()));

            if (!selectorBackup)
            {
                selectorBackup = true;
                selectorEnabled = selectorModel.isEnabled();
                selectorModel.setEnabled(false);
            }
        }
        else if (selectorBackup)
        {
            selectorBackup = false;
            selectorModel.setEnabled(selectorEnabled);
        }
    }

    /**
     * Check if cursor is inside map view.
     * 
     * @return <code>true</code> if over map, <code>false</code> on Hud.
     */
    private boolean isCursorOverMap()
    {
        return UtilMath.isBetween(cursor.getScreenX(), camera.getViewX(), camera.getViewX() + camera.getWidth() - 8)
               && UtilMath.isBetween(cursor.getScreenY(),
                                     camera.getViewY(),
                                     camera.getViewY() + camera.getHeight() - 16);
    }

    @Override
    public void update(double extrp)
    {
        navigationDelay.update(extrp);

        if (navigationDelay.elapsed(NAVIGATION_TICK))
        {
            updateNavigationPointer(extrp);
            camera.moveLocation(extrp,
                                device.getHorizontalDirection() * map.getTileWidth(),
                                device.getVerticalDirection() * map.getTileHeight());
            navigationDelay.restart();
        }

        updateNavigationMinimap(extrp);
        updateCursorOver();

        if (isCursorOverMap() && cursor.isPushedOnce(DeviceMapping.ACTION_RIGHT))
        {
            final int marginX = map.getTileWidth() / 2;
            final int marginY = map.getTileHeight() / 2;
            if (UtilMath.isBetween(cursor.getScreenX(),
                                   camera.getViewX() + marginX,
                                   camera.getViewX() + camera.getWidth() - marginX)
                && UtilMath.isBetween(cursor.getScreenY(),
                                      camera.getViewY() + marginX,
                                      camera.getViewY() + camera.getHeight() - marginY))
            {
                checkRightClick();
            }
        }
    }

    /**
     * Update cursor if over entity.
     */
    private void updateCursorOver()
    {
        if (cursor.getSurfaceId().intValue() != Constant.CURSOR_ID_ORDER)
        {
            final int tx = map.getInTileX(cursor);
            final int ty = map.getInTileY(cursor);

            if (!cursor.isPushed() && fogOfWar.isVisited(tx, ty) && !fogOfWar.isFogged(tx, ty) && isValidEntity(tx, ty))
            {
                cursor.setRenderingOffset(-5, -5);
                cursor.setSurfaceId(Constant.CURSOR_ID_OVER);
            }
            else
            {
                cursor.setRenderingOffset(0, 0);
                cursor.setSurfaceId(Constant.CURSOR_ID);
            }
        }
    }

    /**
     * Check if pointing valid entity.
     * 
     * @param tx The horizontal tile pointed.
     * @param ty The vertical tile pointed.
     * @return <code>true</code> if valid over, <code>false</code> else.
     */
    private boolean isValidEntity(int tx, int ty)
    {
        for (final Integer id : mapPath.getObjectsId(tx, ty))
        {
            final Featurable featurable = handler.get(id);
            if (featurable.getFeature(EntityStats.class).getHealthPercent() > 0
                && featurable.getFeature(EntityModel.class).isVisible())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check right click shortcut action.
     */
    private void checkRightClick()
    {
        final List<Selectable> selection = selector.getSelection();
        final int n = selection.size();
        for (int i = 0; i < n; i++)
        {
            final Selectable selectable = selection.get(i);
            if (selectable.hasFeature(RightClickHandler.class))
            {
                selectable.getFeature(RightClickHandler.class).execute();
            }
        }
    }
}
