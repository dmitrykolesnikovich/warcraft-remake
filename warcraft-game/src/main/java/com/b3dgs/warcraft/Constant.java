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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;

/**
 * Game constants.
 */
public final class Constant
{
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

    /** Entity layer. */
    public static final int LAYER_ENTITY = 1;
    /** Hud layer. */
    public static final int LAYER_HUD = LAYER_ENTITY + 1;
    /** Selection layer. */
    public static final int LAYER_SELECTION = LAYER_HUD + 1;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
