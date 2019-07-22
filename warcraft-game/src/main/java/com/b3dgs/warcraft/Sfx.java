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

import java.util.Locale;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.warcraft.constant.Extension;
import com.b3dgs.warcraft.constant.Folder;

/**
 * List of available sounds fx.
 * <p>
 * Sound file name is enum name in lower case in enum race name in lower case folder.
 * </p>
 */
public enum Sfx
{
    /** Click */
    NEUTRAL_CLICK(Race.NEUTRAL),
    /** Sword 1. */
    NEUTRAL_SWORD1(Race.NEUTRAL),
    /** Sword 2. */
    NEUTRAL_SWORD2(Race.NEUTRAL),
    /** Sword 3. */
    NEUTRAL_SWORD3(Race.NEUTRAL),
    /** Tree 1. */
    NEUTRAL_TREE1(Race.NEUTRAL),
    /** Tree 2. */
    NEUTRAL_TREE2(Race.NEUTRAL),
    /** Tree 3. */
    NEUTRAL_TREE3(Race.NEUTRAL),
    /** Tree 4. */
    NEUTRAL_TREE4(Race.NEUTRAL),

    /** Orcs ready. */
    ORCS_READY(Race.ORC),
    /** Orcs work done. */
    ORCS_WORKDONE(Race.ORC),
    /** Orcs yes sir 1. */
    ORCS_YESSIR1(Race.ORC),
    /** Orcs yes sir 2. */
    ORCS_YESSIR2(Race.ORC),
    /** Orcs yes sir 3. */
    ORCS_YESSIR3(Race.ORC),
    /** Orcs what 1. */
    ORCS_WHAT1(Race.ORC),
    /** Orcs what 2. */
    ORCS_WHAT2(Race.ORC),
    /** Orcs what 3. */
    ORCS_WHAT3(Race.ORC),
    /** Orcs what 4. */
    ORCS_WHAT4(Race.ORC),
    /** Orcs die. */
    ORCS_DEAD(Race.ORC);

    private static Sfx[] SWORD = new Sfx[]
    {
        NEUTRAL_SWORD1, NEUTRAL_SWORD2, NEUTRAL_SWORD3
    };
    private static Sfx[] TREE = new Sfx[]
    {
        NEUTRAL_TREE1, NEUTRAL_TREE2, NEUTRAL_TREE3, NEUTRAL_TREE4
    };
    private static Sfx[] ORCS_WHAT = new Sfx[]
    {
        ORCS_WHAT1, ORCS_WHAT2, ORCS_WHAT3, ORCS_WHAT4
    };
    private static Sfx[] ORCS_YESSIR = new Sfx[]
    {
        ORCS_YESSIR1, ORCS_YESSIR2, ORCS_YESSIR3
    };

    /**
     * Cache sfx.
     */
    public static void cache()
    {
        for (final Sfx sfx : Sfx.values())
        {
            sfx.audio.setVolume(0);
            sfx.play();
        }
        try
        {
            Thread.sleep(Constant.HUNDRED / 2);
        }
        catch (final InterruptedException exception)
        {
            Verbose.exception(exception);
        }
        for (final Sfx sfx : Sfx.values())
        {
            sfx.audio.stop();
            sfx.audio.setVolume(50);
        }
    }

    /**
     * Play a random sword sound.
     */
    public static void playRandomSword()
    {
        playRandom(SWORD);
    }

    /**
     * Play a random tree cut sound.
     */
    public static void playRandomTreeCut()
    {
        playRandom(TREE);
    }

    /**
     * Play a random orc selection sound.
     */
    public static void playRandomOrcSelect()
    {
        playRandom(ORCS_WHAT);
    }

    /**
     * Play a random orc confirm sound.
     */
    public static void playRandomOrcConfirm()
    {
        playRandom(ORCS_YESSIR);
    }

    /**
     * Play a random sound.
     * 
     * @param sfx The random sounds.
     */
    private static void playRandom(Sfx[] sfx)
    {
        final int id = UtilRandom.getRandomInteger(sfx.length - 1);
        sfx[id].play();
    }

    /** Associated race. */
    private final Race race;
    /** Audio handler. */
    private final Audio audio;

    /**
     * Create Sfx.
     * 
     * @param race The associated race (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    Sfx(Race race)
    {
        Check.notNull(race);

        this.race = race;
        audio = AudioFactory.loadAudio(get());
    }

    /**
     * Get the sound media.
     * 
     * @return The sound media.
     */
    public Media get()
    {
        final String folder = race.name().toLowerCase(Locale.ENGLISH);
        final String file = name().toLowerCase(Locale.ENGLISH) + Extension.SFX;
        return Medias.create(Folder.SOUNDS, folder, file.substring(file.indexOf(Constant.UNDERSCORE) + 1));
    }

    /**
     * Play sound.
     */
    public void play()
    {
        audio.play();
    }
}
