/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.warcraft.constant.Extension;
import com.b3dgs.warcraft.constant.Folder;

/**
 * List of available musics.
 * <p>
 * Music file name is enum name in lower case in enum race name in lower case folder.
 * </p>
 */
public enum Music
{
    /** Orc campaign 1. */
    ORC_CAMPAIGN1(Race.ORC),
    /** Orc campaign 2. */
    ORC_CAMPAIGN2(Race.ORC),
    /** Orc campaign 3. */
    ORC_CAMPAIGN3(Race.ORC);

    /** Associated media. */
    private final Media media;

    /**
     * Create music.
     * 
     * @param race The associated race (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    Music(Race race)
    {
        Check.notNull(race);

        final String folder = race.name().toLowerCase(Locale.ENGLISH);
        final String file = name().toLowerCase(Locale.ENGLISH) + Extension.MUSIC;
        media = Medias.create(Folder.MUSICS, folder, file.substring(race.name().length() + 1));
    }

    /**
     * Get the music media.
     * 
     * @return The music media.
     */
    public Media get()
    {
        return media;
    }
}
