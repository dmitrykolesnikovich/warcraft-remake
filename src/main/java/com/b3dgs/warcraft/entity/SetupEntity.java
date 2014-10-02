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
package com.b3dgs.warcraft.entity;

import java.util.Locale;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.RaceType;

/**
 * Setup entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SetupEntity
        extends SetupSurfaceGame
{
    /** Race type. */
    public final RaceType race;
    /** Corpse. */
    public final ImageBuffer corpse;

    /**
     * Constructor.
     * 
     * @param media The config file.
     */
    public SetupEntity(Media media)
    {
        super(media, false);
        race = RaceType.valueOf(configurer.getText("race").toUpperCase(Locale.ENGLISH));
        if (RaceType.NEUTRAL == race)
        {
            corpse = null;
        }
        else
        {
            corpse = Core.GRAPHIC.getImageBuffer(
                    Core.MEDIA.create(AppWarcraft.EFFECTS_DIR, "corpse_" + race.getPath() + ".png"), false);
        }
    }
}
