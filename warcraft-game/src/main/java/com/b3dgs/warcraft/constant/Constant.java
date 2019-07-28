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
package com.b3dgs.warcraft.constant;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String NAME = "Warcraft Remake";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 0, 4);
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** Buildings layer. */
    public static final int LAYER_BUILDING = 1;
    /** Entity layer. */
    public static final int LAYER_ENTITY = LAYER_BUILDING + 1;
    /** Projectile layer. */
    public static final int LAYER_PROJECTILE = LAYER_ENTITY + 1;
    /** Hud layer. */
    public static final int LAYER_HUD = LAYER_PROJECTILE + 1;
    /** Selection layer. */
    public static final int LAYER_SELECTION = LAYER_HUD + 1;

    /** Selection render layer. */
    public static final int LAYER_SELECTION_RENDER = LAYER_HUD + 1;
    /** Hud render layer. */
    public static final int LAYER_HUD_RENDER = LAYER_SELECTION_RENDER + 1;
    /** Hud menus render layer. */
    public static final int LAYER_MENUS_RENDER = LAYER_HUD_RENDER + 1;

    /** Entity info X. */
    public static final int ENTITY_INFO_X = 2;
    /** Entity info X. */
    public static final int ENTITY_INFO_Y = 72;

    /** Minimap horizontal location. */
    public static final int MINIMAP_X = 3;
    /** Minimap vertical location. */
    public static final int MINIMAP_Y = 6;

    /** Tile path category tree. */
    public static final String CATEGORY_TREE = "tree";
    /** Tile number tree cut. */
    public static final int TILE_NUM_TREE_CUT = 124;

    /** Sfx attribute separator. */
    public static final String SFX_SEPARATOR = ";";
    /** Default volume. */
    public static final int VOLUME_DEFAULT = 50;
    /** Sound bank id. */
    public static final Integer SOUND_BANK_ID = Integer.valueOf(43);

    /** Color camera view. */
    public static final ColorRgba COLOR_VIEW = new ColorRgba(200, 200, 200);
    /** Color current selection. */
    public static final ColorRgba COLOR_SELECTION = new ColorRgba(0, 200, 0);
    /** Color allies. */
    public static final ColorRgba COLOR_ALLIES = new ColorRgba(0, 200, 0);
    /** Color neutral. */
    public static final ColorRgba COLOR_NEUTRAL = new ColorRgba(200, 200, 200);
    /** Color enemies. */
    public static final ColorRgba COLOR_ENEMIES = new ColorRgba(200, 0, 0);
    /** Color health good. */
    public static final ColorRgba COLOR_HEALTH_GOOD = new ColorRgba(0, 200, 0);
    /** Color health warning. */
    public static final ColorRgba COLOR_HEALTH_WARN = new ColorRgba(200, 200, 0);
    /** Color health alert. */
    public static final ColorRgba COLOR_HEALTH_ALERT = new ColorRgba(200, 0, 0);

    /** Cursor id. */
    public static final int CURSOR_ID = 0;
    /** Cursor id order. */
    public static final int CURSOR_ID_ORDER = 1;
    /** Cursor id over. */
    public static final int CURSOR_ID_OVER = 2;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
