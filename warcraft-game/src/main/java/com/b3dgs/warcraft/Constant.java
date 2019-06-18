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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String NAME = "Warcraft Remake";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 0, 2);
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** Entity folder. */
    public static final String FOLDER_ENTITY = "entity";
    /** Action folder. */
    public static final String FOLDER_ACTION = "action";
    /** Orc folder. */
    public static final String FOLDER_ORC = "orc";
    /** Human folder. */
    public static final String FOLDER_HUMAN = "human";
    /** Neutral folder. */
    public static final String FOLDER_NEUTRAL = "neutral";
    /** Map folder. */
    public static final String FOLDER_MAP = "map";
    /** Menu folder. */
    public static final String FOLDER_MENU = "menu";

    /** Buildings layer. */
    public static final int LAYER_BUILDING = 1;
    /** Entity layer. */
    public static final int LAYER_ENTITY = LAYER_BUILDING + 1;
    /** Hud layer. */
    public static final int LAYER_HUD = LAYER_ENTITY + 1;
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

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
