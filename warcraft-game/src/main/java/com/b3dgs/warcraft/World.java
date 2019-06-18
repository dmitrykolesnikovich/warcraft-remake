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

import java.io.IOException;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    // private static final int MOVE_FACTOR = 1;
    private static final int MINIMAP_X = 3;
    private static final int MINIMAP_Y = 6;
    private static final int VIEW_X = 72;
    private static final int VIEW_Y = 12;
    private static final ColorRgba TEXT_COLOR = new ColorRgba(240, 255, 220);
    private static final int TEXT_X = 74;
    private static final int TEXT_Y = 192;

    private final Text text = services.add(Graphics.createText("Verdana", 9, TextStyle.NORMAL));
    private final MapTile map = services.create(MapTileGame.class);
    private final Minimap minimap = new Minimap(map);
    private final Cursor cursor = services.create(Cursor.class);
    private final InputDevicePointer pointer = getInputDevice(InputDevicePointer.class);
    // private final Keyboard keyboard = services.add(getInputDevice(Keyboard.class));

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        camera.setView(VIEW_X, VIEW_Y, source.getWidth() - VIEW_X, source.getHeight() - VIEW_Y, source.getHeight());

        handler.addComponent(new ComponentCollision());

        map.addFeature(new MapTileViewerModel(services));
        map.addFeature(new MapTilePersisterModel(services));
        map.addFeature(new MapTileGroupModel());
        map.addFeature(new MapTilePathModel(services));
        handler.add(map);

        final Hud hud = factory.create(Medias.create("Hud.xml"));
        handler.add(hud);

        final Selector selector = services.get(Selector.class);
        selector.addFeature(new LayerableModel(Constant.LAYER_SELECTION, Constant.LAYER_SELECTION_RENDER));
        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(1);
        selector.getFeature(Collidable.class).addAccept(Constant.LAYER_ENTITY);

        text.setLocation(TEXT_X, TEXT_Y);
        text.setColor(TEXT_COLOR);

        services.add(Integer.valueOf(source.getRate()));
    }

    /**
     * Update map navigation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNavigation(double extrp)
    {
        if (pointer.getClick() > 1)
        {
            camera.moveLocation(extrp, -pointer.getMoveX(), pointer.getMoveY());
        }
        // if (keyboard.isPressed(Keyboard.UP))
        // {
        // camera.moveLocation(extrp, 0, map.getTileHeight() * MOVE_FACTOR);
        // }
        // if (keyboard.isPressed(Keyboard.DOWN))
        // {
        // camera.moveLocation(extrp, 0, -map.getTileHeight() * MOVE_FACTOR);
        // }
        // if (keyboard.isPressed(Keyboard.LEFT))
        // {
        // camera.moveLocation(extrp, -map.getTileWidth() * MOVE_FACTOR, 0);
        // }
        // if (keyboard.isPressed(Keyboard.RIGHT))
        // {
        // camera.moveLocation(extrp, map.getTileWidth() * MOVE_FACTOR, 0);
        // }
    }

    /**
     * Draw field of view.
     * 
     * @param g The graphic output.
     */
    private void drawFov(Graphic g)
    {
        g.setColor(ColorRgba.GREEN);
        camera.drawFov(g, MINIMAP_X, MINIMAP_Y, map.getTileWidth(), map.getTileHeight(), minimap);
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
        map.getFeature(MapTileGroup.class).loadGroups(Medias.create(map.getMedia().getParentPath(), "groups.xml"));
        final Media pathfinding = Medias.create(map.getMedia().getParentPath(), "pathfinding.xml");
        map.getFeature(MapTilePath.class).loadPathfinding(pathfinding);

        minimap.load();
        minimap.automaticColor();
        minimap.prepare();
        minimap.setLocation(MINIMAP_X, MINIMAP_Y);

        camera.setLimits(map);

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor_order.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(pointer);
        cursor.setViewer(camera);

        final int baseX = 10;
        final int baseY = 10;
        createBase(baseX, baseY);
    }

    /**
     * Create base world.
     * 
     * @param x The horizontal base.
     * @param y The vertical base.
     */
    private void createBase(int x, int y)
    {
        final Featurable peon = factory.create(Medias.create(Constant.FOLDER_ENTITY, Constant.FOLDER_ORC, "Peon.xml"));
        peon.getFeature(Pathfindable.class).setLocation(x, y);
        handler.add(peon);

        final Featurable grunt = factory.create(Medias.create(Constant.FOLDER_ENTITY,
                                                              Constant.FOLDER_ORC,
                                                              "Grunt.xml"));
        grunt.getFeature(Pathfindable.class).setLocation(x + 2, y + 1);
        handler.add(grunt);

        camera.teleport(grunt.getFeature(Transformable.class).getX() - camera.getWidth() / 2,
                        grunt.getFeature(Transformable.class).getY() - camera.getHeight() / 2);
    }

    @Override
    public void update(double extrp)
    {
        text.setText(com.b3dgs.lionengine.Constant.EMPTY_STRING);

        updateNavigation(extrp);
        pointer.update(extrp);
        cursor.update(extrp);

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        minimap.render(g);
        text.render(g);
        drawFov(g);
        if (!cursor.hasClicked(2))
        {
            cursor.render(g);
        }
    }
}
