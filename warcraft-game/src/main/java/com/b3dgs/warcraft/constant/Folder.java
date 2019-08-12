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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;

/**
 * Folder constants.
 */
public final class Folder
{
    /** Entities folder. */
    public static final String ENTITIES = "entity";
    /** Effects folder. */
    public static final String EFFECTS = "effect";
    /** Items folder. */
    public static final String ACTIONS = "action";
    /** Monsters folder. */
    public static final String ORCS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "orc");
    /** Sceneries folder. */
    public static final String HUMANS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "human");
    /** Players folder. */
    public static final String NEUTRAL = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "neutral");
    /** Effects folder. */
    public static final String MAPS = "map";
    /** Fog of war folder. */
    public static final String FOG = "fog";
    /** Levels folder. */
    public static final String MENU = "menu";
    /** Musics folder. */
    public static final String MUSICS = "music";
    /** Sounds folder. */
    public static final String SOUNDS = "sfx";

    /**
     * Private constructor.
     */
    private Folder()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
