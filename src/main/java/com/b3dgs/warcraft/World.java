/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.DeviceType;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.warcraft.effect.FactoryEffect;
import com.b3dgs.warcraft.effect.HandlerEffect;
import com.b3dgs.warcraft.entity.Entity;
import com.b3dgs.warcraft.entity.EntityType;
import com.b3dgs.warcraft.entity.FactoryEntity;
import com.b3dgs.warcraft.entity.FactoryProduction;
import com.b3dgs.warcraft.entity.HandlerEntity;
import com.b3dgs.warcraft.launcher.FactoryLauncher;
import com.b3dgs.warcraft.map.FogOfWar;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Minimap;
import com.b3dgs.warcraft.projectile.FactoryProjectile;
import com.b3dgs.warcraft.projectile.HandlerProjectile;
import com.b3dgs.warcraft.skill.FactorySkill;
import com.b3dgs.warcraft.weapon.FactoryWeapon;

/**
 * World implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class World
        extends WorldGame
{
    /** Keyboard. */
    private final Keyboard keyboard;
    /** Mouse. */
    private final Mouse mouse;
    /** Text reference. */
    private final TextGame text;
    /** Player 1. */
    private final Player player;
    /** Player 2. */
    private final Player cpu;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final Camera camera;
    /** Fog of war. */
    private final FogOfWar fogOfWar;
    /** Minimap. */
    private final Minimap minimap;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** The factory reference. */
    private final FactoryProjectile factoryProjectile;
    /** The factory skill. */
    private final FactorySkill factorySkill;
    /** The factory skill. */
    private final FactoryLauncher factoryLauncher;
    /** The factory production. */
    private final FactoryProduction factoryProduction;
    /** The factory weapon. */
    private final FactoryWeapon factoryWeapon;
    /** The factory effect. */
    private final FactoryEffect factoryEffect;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;
    /** Effect handler. */
    private final HandlerEffect handlerEffect;
    /** Arrows handler. */
    private final HandlerProjectile handlerProjectile;
    /** Timed message. */
    private final TimedMessage message;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     * @param config The game configuration.
     */
    World(Sequence sequence, GameConfig config)
    {
        super(sequence);
        keyboard = sequence.getInputDevice(DeviceType.KEYBOARD);
        mouse = sequence.getInputDevice(DeviceType.MOUSE);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        message = new TimedMessage(UtilityImage.createText(Text.DIALOG, 10, TextStyle.NORMAL));
        fogOfWar = new FogOfWar(config);
        player = new Player();
        cpu = new Player();
        map = new Map();

        camera = new Camera(map);
        cursor = new Cursor(mouse, camera, source, map, UtilityMedia.get("cursor.png"),
                UtilityMedia.get("cursor_over.png"), UtilityMedia.get("cursor_order.png"));
        controlPanel = new ControlPanel(cursor);

        handlerEffect = new HandlerEffect(camera);
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, fogOfWar);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);

        minimap = new Minimap(map, fogOfWar, controlPanel, handlerEntity, 3, 6);

        factoryProjectile = new FactoryProjectile();
        factoryLauncher = new FactoryLauncher(factoryProjectile, handlerProjectile);
        factoryWeapon = new FactoryWeapon(factoryLauncher);
        factoryProduction = new FactoryProduction();
        factorySkill = new FactorySkill(map, cursor, handlerEntity, factoryProduction, message);
        factoryEffect = new FactoryEffect();

        factoryEntity = new FactoryEntity(map, message, factoryEffect, factorySkill, factoryWeapon, handlerEntity,
                handlerEffect, handlerProjectile, source.getRate());
    }

    /**
     * Create an entity from its type.
     * 
     * @param type The entity type.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The entity instance.
     */
    private Entity createEntity(EntityType type, int tx, int ty)
    {
        final Entity entity = factoryEntity.create(type);
        entity.setLocation(tx, ty);
        handlerEntity.add(entity);
        return entity;
    }

    /*
     * WorldStrategy
     */

    @Override
    public void update(double extrp)
    {
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
        controlPanel.update(extrp, camera, cursor);
        handlerEntity.update(extrp);
        handlerProjectile.update(extrp);
        minimap.update(cursor, camera, handlerEntity, 11, 12);
        handlerEffect.update(extrp);
        message.update();
        player.update(extrp);

    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        handlerProjectile.render(g);
        handlerEffect.render(g);
        fogOfWar.render(g, camera);
        cursor.renderBox(g);
        controlPanel.renderCursorSelection(g, camera);
        controlPanel.render(g, cursor, camera);
        message.render(g);
        minimap.render(g, camera);
        cursor.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);
        map.createMiniMap();

        keyboard.setHorizontalControlNegative(Keyboard.LEFT);
        keyboard.setHorizontalControlPositive(Keyboard.RIGHT);
        keyboard.setVerticalControlNegative(Keyboard.DOWN);
        keyboard.setVerticalControlPositive(Keyboard.UP);

        camera.setView(72, 12, 240, 176);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 33, 3);

        fogOfWar.create(map);
        fogOfWar.setPlayerId(player.id);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(ColorRgba.GREEN);
        controlPanel.setPlayer(player);
        controlPanel.setClickSelection(Mouse.LEFT);

        camera.setControlPanel(controlPanel);

        handlerEntity.createLayers(map);
        handlerEntity.setPlayer(player);
        handlerEntity.setClickAssignment(Mouse.RIGHT);

        createEntity(EntityType.GOLD_MINE, 30, 13);
        createEntity(EntityType.GOLD_MINE, 58, 58);

        final Entity peon = createEntity(EntityType.PEON, 40, 8);
        peon.setPlayer(player);

        Entity grunt = createEntity(EntityType.GRUNT, 38, 5);
        grunt.setPlayer(player);

        grunt = createEntity(EntityType.GRUNT, 39, 5);
        grunt.setPlayer(player);

        final Entity spearman = createEntity(EntityType.SPEARMAN, 38, 9);
        spearman.setPlayer(player);

        final Entity townHall = createEntity(EntityType.TOWNHALL_ORC, 40, 5);
        townHall.setPlayer(player);

        final Entity peasant = createEntity(EntityType.PEASANT, 40, 10);
        peasant.setPlayer(cpu);

        handlerEntity.update(1.0);
        handlerEntity.updatePopulation();
    }
}
