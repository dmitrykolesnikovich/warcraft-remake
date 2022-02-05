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

import java.io.IOException;
import java.util.Locale;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * List of world types.
 */
public enum WorldType
{
    /** Forest world. */
    FOREST("Forest"),
    /** Swamp world. */
    SWAMP("Swamp");

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static WorldType load(FileReading file) throws IOException
    {
        return WorldType.valueOf(file.readString());
    }

    /** Associated folder. */
    private final String folder = name().toLowerCase(Locale.ENGLISH);
    /** Title displayed. */
    private final String title;

    /**
     * Constructor.
     * 
     * @param title The displayed title (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    WorldType(String title)
    {
        Check.notNull(title);

        this.title = title;
    }

    /**
     * Save the world type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
    }

    /**
     * Get the world folder.
     * 
     * @return The world folder.
     */
    public String getFolder()
    {
        return folder;
    }

    @Override
    public String toString()
    {
        return title;
    }
}
