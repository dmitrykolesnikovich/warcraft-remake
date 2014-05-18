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
package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.warcraft.menu.Menu;

/**
 * Program starts here.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AppWarcraft
{
    /** Program title. */
    public static final String PROGRAM = "Warcraft Remake";
    /** Program version. */
    public static final Version VERSION = Version.create(0, 1, 0);
    /** Effects directory. */
    public static final String EFFECTS_DIR = "effect";
    /** Entities directory. */
    public static final String ENTITIES_DIR = "entity";
    /** Projectiles directory. */
    public static final String PROJECTILES_DIR = "projectile";
    /** Projectiles directory. */
    public static final String LAUNCHERS_DIR = "launcher";
    /** Skills directory. */
    public static final String SKILLS_DIR = "skill";
    /** Maps directory. */
    public static final String MAPS_DIR = "map";
    /** Tiles directory. */
    public static final String TILES_DIR = "tile";
    /** Weapons directory. */
    public static final String WEAPONS_DIR = "weapon";
    /** Sounds FX directory. */
    public static final String SFX_DIR = "sfx";
    /** Musics directory. */
    public static final String MUSICS_DIR = "music";
    /** Menu directory. */
    public static final String MENU_DIR = "menu";
    /** Enable sound. */
    private static final boolean ENABLE_SOUND = true;
    /** Program path. */
    public static final String PATH = "resources";

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        Engine.start(AppWarcraft.PROGRAM, AppWarcraft.VERSION, Verbose.CRITICAL, AppWarcraft.PATH);
        Music.setEnabled(AppWarcraft.ENABLE_SOUND);
        Sfx.setEnabled(AppWarcraft.ENABLE_SOUND);

        final Resolution output = new Resolution(640, 400, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        loader.start(Menu.class);
    }

    /**
     * Private constructor.
     */
    private AppWarcraft()
    {
        throw new RuntimeException();
    }
}
