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

import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Handle world minimap data.
 */
public class WorldMinimap implements Resource, Renderable
{
    private final Camera camera;
    private final MapTile map;
    private final Handler handler;
    private final Minimap minimap;

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public WorldMinimap(Services services)
    {
        super();

        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        handler = services.get(Handler.class);

        minimap = new Minimap(map);
    }

    /**
     * Draw field of view.
     * 
     * @param g The graphic output.
     */
    private void drawFov(Graphic g)
    {
        g.setColor(ColorRgba.GREEN);
        camera.drawFov(g, Constant.MINIMAP_X, Constant.MINIMAP_Y, map.getTileWidth(), map.getTileHeight(), minimap);

        for (final Pathfindable entity : handler.get(Pathfindable.class))
        {
            g.drawRect(Constant.MINIMAP_X + entity.getInTileX(),
                       Constant.MINIMAP_Y - entity.getInTileY() - entity.getInTileHeight() + map.getInTileHeight(),
                       entity.getInTileWidth(),
                       entity.getInTileHeight(),
                       true);
        }
    }

    @Override
    public void render(Graphic g)
    {
        minimap.render(g);
        drawFov(g);
    }

    @Override
    public void load()
    {
        minimap.load();
        minimap.automaticColor();
        minimap.prepare();
        minimap.setLocation(Constant.MINIMAP_X, Constant.MINIMAP_Y);
    }

    @Override
    public boolean isLoaded()
    {
        return minimap.isLoaded();
    }

    @Override
    public void dispose()
    {
        minimap.dispose();
    }
}
