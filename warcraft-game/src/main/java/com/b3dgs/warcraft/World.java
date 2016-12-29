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

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.Keyboard;
import com.b3dgs.lionengine.io.Mouse;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    private static final int MOVE_FACTOR = 1;
    private static final int MINIMAP_X = 3;
    private static final int MINIMAP_Y = 6;
    private static final int VIEW_X = 72;
    private static final int VIEW_Y = 12;

    private final MapTile map = services.create(MapTileGame.class);
    private final Minimap minimap = new Minimap(map);
    private final Cursor cursor = services.create(Cursor.class);
    private final Mouse mouse = getInputDevice(Mouse.class);
    private final Keyboard keyboard = services.add(getInputDevice(Keyboard.class));
    private final Image hud = Drawable.loadImage(Medias.create("hud.png"));

    /**
     * Create the world.
     * 
     * @param context The context reference.
     */
    public World(Context context)
    {
        super(context);

        final Resolution source = context.getConfig().getSource();
        camera.setView(VIEW_X, VIEW_Y, source.getWidth() - VIEW_X, source.getHeight() - VIEW_Y, source.getHeight());

        handler.addComponent(new ComponentCollision());

        map.addFeature(new MapTileViewerModel());
        map.addFeature(new MapTilePersisterModel());
        handler.add(map);

        final Selector selector = new Selector();
        selector.addFeature(new LayerableModel(1));
        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(Mouse.LEFT);
        handler.add(selector);
    }

    /**
     * Update map navigation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigation(double extrp)
    {
        if (keyboard.isPressed(Keyboard.UP))
        {
            camera.moveLocation(extrp, 0, map.getTileHeight() * MOVE_FACTOR);
        }
        if (keyboard.isPressed(Keyboard.DOWN))
        {
            camera.moveLocation(extrp, 0, -map.getTileHeight() * MOVE_FACTOR);
        }
        if (keyboard.isPressed(Keyboard.LEFT))
        {
            camera.moveLocation(extrp, -map.getTileWidth() * MOVE_FACTOR, 0);
        }
        if (keyboard.isPressed(Keyboard.RIGHT))
        {
            camera.moveLocation(extrp, map.getTileWidth() * MOVE_FACTOR, 0);
        }
    }

    /**
     * Draw field of view.
     * 
     * @param g The graphic output.
     */
    private void drawFov(Graphic g)
    {
        g.setColor(ColorRgba.GREEN);

        final int x = (int) Math.floor((camera.getX() + camera.getViewX()) / map.getTileWidth());
        final int y = (int) -Math.floor((camera.getY() + camera.getHeight()) / map.getTileHeight());
        final int width = camera.getWidth() / map.getTileWidth();
        final int height = camera.getHeight() / map.getTileHeight();
        g.drawRect(MINIMAP_X + x, MINIMAP_Y + y + minimap.getHeight(), width, height, false);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.getFeature(MapTilePersister.class).save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.getFeature(MapTilePersister.class).load(file);

        minimap.load();
        minimap.automaticColor();
        minimap.prepare();
        minimap.setLocation(MINIMAP_X, MINIMAP_Y);

        camera.setLimits(map);

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        hud.load();
        hud.prepare();
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);

        super.update(extrp);

        updateNavigation(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        hud.render(g);
        minimap.render(g);
        cursor.render(g);
        drawFov(g);
    }
}
