/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Locale;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.warcraft.constant.Folder;

/**
 * List of available races.
 */
public enum Race
{
    /** Orc race. */
    ORC,
    /** Human race. */
    HUMAN,
    /** Neutral. */
    NEUTRAL;

    /** The race folder. */
    private final String folder = name().toLowerCase(Locale.ENGLISH);

    /**
     * Get a unit based on its race
     * 
     * @param unit The unit reference.
     * @return The unit media.
     */
    public Media get(Unit unit)
    {
        return Medias.create(Folder.ENTITIES, folder, unit.get() + Factory.FILE_DATA_DOT_EXTENSION);
    }
}
