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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
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
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Folder;

/**
 * World game representation.
 */
public class World extends WorldGame
{
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
    private final Hud hud;
    private final Selector selector;
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));
    private final InputDeviceDirectional directional = services.add(getInputDevice(InputDeviceDirectional.class));

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

        hud = services.add(factory.create(Medias.create("Hud.xml")));
        handler.add(hud);

        selector = services.get(Selector.class);
        selector.addFeature(new LayerableModel(Constant.LAYER_SELECTION, Constant.LAYER_SELECTION_RENDER));
        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(1);
        selector.getFeature(Collidable.class).addAccept(Integer.valueOf(Constant.LAYER_ENTITY));

        hud.addListener(() ->
        {
            cursor.setVisible(true);
            cursor.setSurfaceId(0);
            selector.setEnabled(true);
            hud.setCancelShortcut(() -> false);
        });

        text.setLocation(TEXT_X, TEXT_Y);
        text.setColor(TEXT_COLOR);

        services.add(Integer.valueOf(source.getRate()));
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
            && UtilMath.isBetween(pointer.getX(), MINIMAP_X, MINIMAP_X + map.getInTileWidth())
            && UtilMath.isBetween(pointer.getY(), MINIMAP_Y, MINIMAP_Y + map.getInTileHeight()))
        {
            final int x = (pointer.getX() - MINIMAP_X) * map.getTileWidth();
            final int y = (map.getInTileHeight() + MINIMAP_Y - pointer.getY()) * map.getTileHeight();
            camera.setLocation(x - camera.getWidth() / 2.0, y - camera.getHeight() / 2.0);
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
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        spawn(Medias.create(Folder.ORCS, "Peon.xml"), x * tw, y * th);

        final Featurable grunt = spawn(Medias.create(Folder.ORCS, "Grunt.xml"), (x + 2) * tw, (y + 1) * th);
        camera.teleport(grunt.getFeature(Transformable.class).getX() - camera.getWidth() / 2,
                        grunt.getFeature(Transformable.class).getY() - camera.getHeight() / 2);
    }

    @Override
    public void update(double extrp)
    {
        text.setText(com.b3dgs.lionengine.Constant.EMPTY_STRING);

        pointer.update(extrp);
        cursor.update(extrp);
        updateNavigationDirectional(extrp);
        updateNavigationMinimap(extrp);

        super.update(extrp);

        if (!updateNavigationPointer(extrp) && cursor.hasClickedOnce(3))
        {
            for (final Selectable selectable : selector.getSelection())
            {
                selectable.getFeature(Pathfindable.class).setDestination(cursor);
            }
        }
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
