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
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Warehouse;

/**
 * Handle world minimap data.
 */
public class WorldMinimap implements Resource, Renderable
{
    private final Camera camera;
    private final MapTile map;
    private final Handler handler;
    private final Player player;
    private final Minimap minimap;
    private final FogOfWar fogOfWar;
    private ImageBuffer buffer;

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
        player = services.get(Player.class);
        fogOfWar = services.get(FogOfWar.class);

        minimap = new Minimap(map);
    }

    /**
     * Draw entities.
     * 
     * @param g The graphic output.
     */
    private void drawEntities(Graphic g)
    {
        for (final Pathfindable entity : handler.get(Pathfindable.class))
        {
            final EntityStats stats = entity.getFeature(EntityStats.class);
            if (stats.getHealthPercent() > 0 && entity.getFeature(EntityModel.class).isVisible())
            {
                if (fogOfWar.isVisible(entity))
                {
                    final Race race = stats.getRace();
                    if (player.owns(race) && entity.hasFeature(Warehouse.class))
                    {
                        g.setColor(Constant.COLOR_WAREHOUSE);
                    }
                    else
                    {
                        g.setColor(player.getColor(race));
                    }
                    g.drawRect(getX(entity.getInTileX()),
                               getY(entity.getInTileY(), entity.getInTileHeight()),
                               entity.getInTileWidth(),
                               entity.getInTileHeight(),
                               true);
                }
            }
        }
    }

    /**
     * Draw field of view.
     * 
     * @param g The graphic output.
     */
    private void drawFov(Graphic g)
    {
        g.setColor(Constant.COLOR_VIEW);
        camera.drawFov(g, Constant.MINIMAP_X, Constant.MINIMAP_Y, map.getTileWidth(), map.getTileHeight(), minimap);
    }

    /**
     * Get horizontal on minimap.
     * 
     * @param tx The location in tile to get.
     * @return The location on minimap.
     */
    private int getX(int tx)
    {
        return Constant.MINIMAP_X + tx;
    }

    /**
     * Get vertical on minimap.
     * 
     * @param ty The location in tile to get.
     * @param th The height.
     * @return The location on minimap.
     */
    private int getY(int ty, int th)
    {
        return Constant.MINIMAP_Y - ty + map.getInTileHeight() - th;
    }

    @Override
    public void render(Graphic g)
    {
        minimap.render(g);
        g.drawImage(buffer, Constant.MINIMAP_X, Constant.MINIMAP_Y);
        drawEntities(g);
        drawFov(g);
    }

    @Override
    public void load()
    {
        minimap.load();
        minimap.automaticColor();
        minimap.prepare();
        minimap.setLocation(Constant.MINIMAP_X, Constant.MINIMAP_Y);

        buffer = Graphics.createImageBuffer(map.getInTileWidth(), map.getInTileHeight(), ColorRgba.BLACK);
        fogOfWar.addListener((tx, ty) -> buffer.setRgb(tx, map.getInTileHeight() - ty - 1, 0));
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
