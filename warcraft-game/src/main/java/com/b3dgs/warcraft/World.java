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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectionListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
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
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.world.WorldMap;
import com.b3dgs.warcraft.world.WorldMinimap;
import com.b3dgs.warcraft.world.WorldNavigator;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    private static final int VIEW_X = 72;
    private static final int VIEW_Y = 12;
    private static final ColorRgba TEXT_COLOR = new ColorRgba(240, 255, 220);
    private static final int TEXT_X = 74;
    private static final int TEXT_Y = 192;
    private static final int RESOURCES_WOOD_X = 170;
    private static final int RESOURCES_GOLD_X = 270;
    private static final int RESOURCES_Y = 2;

    private final Text text = services.add(Graphics.createText("Verdana", 9, TextStyle.NORMAL));
    private final Player player = services.add(new Player(Race.ORC));
    private final WorldMap worldMap = new WorldMap(services);
    private final MapTile map = services.get(MapTile.class);
    private final WorldMinimap minimap = new WorldMinimap(services);
    private final Cursor cursor = services.create(Cursor.class);
    private final Hud hud;
    private final Selector selector;
    private final WorldNavigator navigator;
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));
    private Audio music;

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        services.add(getInputDevice(InputDeviceDirectional.class));

        camera.setView(VIEW_X, VIEW_Y, source.getWidth() - VIEW_X, source.getHeight() - VIEW_Y, source.getHeight());

        handler.addComponent(services.add(new ComponentCollision()));

        hud = services.add(factory.create(Medias.create("hud.xml")));
        handler.add(hud);

        navigator = new WorldNavigator(services);

        selector = services.get(Selector.class);
        selector.addFeature(new LayerableModel(Constant.LAYER_SELECTION, Constant.LAYER_SELECTION_RENDER));
        selector.setClickableArea(camera);
        selector.setSelectionColor(Constant.COLOR_SELECTION);
        selector.setClickSelection(1);

        final AtomicReference<Race> race = new AtomicReference<>();
        final AtomicBoolean moving = new AtomicBoolean();
        selector.addListener(new SelectionListener()
        {
            @Override
            public void notifySelectionStarted()
            {
                race.set(null);
                moving.set(false);
            }

            @Override
            public void notifySelected(List<Selectable> selection)
            {
                for (final Selectable current : selection)
                {
                    if (!player.owns(current.getFeature(EntityStats.class).getRace()))
                    {
                        hud.clearMenus();
                        break;
                    }
                }
            }
        });
        selector.setAccept((selected, selectable) ->
        {
            final EntityStats stats = selectable.getFeature(EntityStats.class);
            final Race current = stats.getRace();
            final boolean mover = stats.isMover();

            if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current) || !moving.get())
            {
                for (final Selectable old : selected)
                {
                    old.onSelection(false);
                }
                selected.clear();
                if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current))
                {
                    race.set(current);
                }
            }
            if (mover)
            {
                moving.set(true);
            }
            if (moving.get() && !mover || !player.owns(current) && race.get() != null)
            {
                return false;
            }
            return race.compareAndSet(null, current) || current.equals(race.get()) && !Race.NEUTRAL.equals(current);
        });

        hud.addListener(() ->
        {
            cursor.setVisible(true);
            cursor.setSurfaceId(0);
            selector.setEnabled(true);
            hud.setCancelShortcut(() -> false);
        });

        text.setLocation(TEXT_X, TEXT_Y);
        text.setColor(TEXT_COLOR);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        worldMap.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        worldMap.load(file);
        minimap.load();

        camera.setLimits(map);

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor_order.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(pointer);
        cursor.setViewer(camera);

        spawn(Race.NEUTRAL, "goldmine", 27, 12);

        createBase(Race.ORC, 33, 10);
        createBase(Race.HUMAN, 48, 55);

        music = AudioFactory.loadAudio(Music.ORC_CAMPAIGN2.get());
        music.play();
    }

    /**
     * Create base world.
     * 
     * @param race The race reference.
     * @param tx The horizontal tile base.
     * @param ty The vertical tile base.
     */
    private void createBase(Race race, int tx, int ty)
    {
        spawn(race, Unit.WORKER.get(), tx, ty - 2);
        spawn(race, Unit.FARM.get(), tx + 3, ty - 5);
        spawn(race, Unit.BARRACKS.get(), tx + 6, ty - 2);
        player.increaseFood();

        final Transformable townhall = spawn(race, Unit.TOWNHALL.get(), tx, ty);
        camera.teleport(townhall.getX() + (townhall.getWidth() - camera.getWidth()) / 2,
                        townhall.getY() + (townhall.getHeight() - camera.getHeight()) / 2);
    }

    /**
     * Spawn a {@link Featurable} at specified location. Must have {@link Transformable} feature.
     * 
     * @param race The featurable race.
     * @param file The featurable file.
     * @param tx The horizontal tile spawn location.
     * @param ty The vertical tile spawn location.
     * @return The spawned featurable.
     * @throws LionEngineException If invalid media or missing feature.
     */
    private Transformable spawn(Race race, String file, int tx, int ty)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        final Featurable featurable = super.spawn(race.get(file), tx * tw, ty * th);
        featurable.getFeature(Pathfindable.class).setLocation(tx, ty);

        return featurable.getFeature(Transformable.class);
    }

    @Override
    public void update(double extrp)
    {
        text.setText(com.b3dgs.lionengine.Constant.EMPTY_STRING);

        pointer.update(extrp);
        cursor.update(extrp);
        navigator.update(extrp);
        player.update(extrp);

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        minimap.render(g);
        text.render(g);
        text.draw(g, RESOURCES_WOOD_X, RESOURCES_Y, Align.RIGHT, String.valueOf(player.getWood()));
        text.draw(g, RESOURCES_GOLD_X, RESOURCES_Y, Align.RIGHT, String.valueOf(player.getGold()));
        if (!cursor.hasClicked(2))
        {
            cursor.render(g);
        }
    }
}
