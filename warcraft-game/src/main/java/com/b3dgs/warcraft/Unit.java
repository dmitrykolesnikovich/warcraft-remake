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

/**
 * List of standard units.
 */
public enum Unit
{
    /** Goldmine. */
    GOLDMINE,
    /** Townhall. */
    TOWNHALL,
    /** Farm. */
    FARM,
    /** Barracks. */
    BARRACKS,
    /** Lumbermill. */
    LUMBERMILL,
    /** Worker. */
    WORKER,

    /** Footman. */
    FOOTMAN,
    /** Archer. */
    ARCHER,
    /** Grunt. */
    GRUNT,
    /** Spearman. */
    SPEARMAN;

    /** The associated file. */
    private final String file = name().toLowerCase(Locale.ENGLISH);

    /**
     * Get the associated file without extension.
     * 
     * @return The associated file without extension.
     */
    public String get()
    {
        return file;
    }
}
