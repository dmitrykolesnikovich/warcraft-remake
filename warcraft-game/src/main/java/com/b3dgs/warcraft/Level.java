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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.warcraft.constant.Extension;
import com.b3dgs.warcraft.constant.Folder;

/**
 * List of levels with their file.
 */
public enum Level
{
    /** Forest level. */
    FOREST(WorldType.FOREST, "forest"),
    /** Swamp level. */
    SWAMP(WorldType.SWAMP, "swamp");

    /** Level file. */
    private final Media level;
    /** Level rip image. */
    private final Media rip;

    /**
     * Create the level.
     * 
     * @param world The level world.
     * @param level The level file name.
     */
    Level(WorldType world, String level)
    {
        final String folder = world.getFolder();
        this.level = Medias.create(Folder.MAPS, folder, level + Extension.LEVEL);
        rip = Medias.create(Folder.MAPS, folder, level + Extension.IMAGE);
    }

    /**
     * Get the level data file.
     * 
     * @return The level data file.
     */
    public Media getFile()
    {
        return level;
    }

    /**
     * Get the associated level rip.
     * 
     * @return The level rip image.
     */
    public Media getRip()
    {
        return rip;
    }
}
