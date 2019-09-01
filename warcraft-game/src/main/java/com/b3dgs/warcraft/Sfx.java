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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.warcraft.constant.Constant;
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
    /** Click. */
    NEUTRAL_CLICK(Race.NEUTRAL),
    /** Build. */
    NEUTRAL_BUILD(Race.NEUTRAL),
    /** Construct. */
    NEUTRAL_CONSTRUCT(Race.NEUTRAL),
    /** Explode 1. */
    NEUTRAL_EXPLODE1(Race.NEUTRAL),
    /** Explode 2. */
    NEUTRAL_EXPLODE2(Race.NEUTRAL),
    /** Sword 1. */
    NEUTRAL_SWORD1(Race.NEUTRAL),
    /** Sword 2. */
    NEUTRAL_SWORD2(Race.NEUTRAL),
    /** Sword 3. */
    NEUTRAL_SWORD3(Race.NEUTRAL),
    /** Bow fire. */
    NEUTRAL_BOWFIRE(Race.NEUTRAL),
    /** Arrow hit. */
    NEUTRAL_ARROWHIT(Race.NEUTRAL),
    /** Tree 1. */
    NEUTRAL_TREE1(Race.NEUTRAL),
    /** Tree 2. */
    NEUTRAL_TREE2(Race.NEUTRAL),
    /** Tree 3. */
    NEUTRAL_TREE3(Race.NEUTRAL),
    /** Tree 4. */
    NEUTRAL_TREE4(Race.NEUTRAL),

    /** Orc ready. */
    ORC_READY(Race.ORC),
    /** Orc work done. */
    ORC_WORKDONE(Race.ORC),
    /** Orc yes sir 1. */
    ORC_YESSIR1(Race.ORC),
    /** Orc yes sir 2. */
    ORC_YESSIR2(Race.ORC),
    /** Orc yes sir 3. */
    ORC_YESSIR3(Race.ORC),
    /** Orc yes sir 4. */
    ORC_YESSIR4(Race.ORC),
    /** Orc what 1. */
    ORC_WHAT1(Race.ORC),
    /** Orc what 2. */
    ORC_WHAT2(Race.ORC),
    /** Orc what 3. */
    ORC_WHAT3(Race.ORC),
    /** Orc what 4. */
    ORC_WHAT4(Race.ORC),
    /** Orc die. */
    ORC_DEAD(Race.ORC),

    /** Human ready. */
    HUMAN_READY(Race.HUMAN),
    /** Human work done. */
    HUMAN_WORKDONE(Race.HUMAN),
    /** Human yes sir 1. */
    HUMAN_YESSIR1(Race.HUMAN),
    /** Human yes sir 2. */
    HUMAN_YESSIR2(Race.HUMAN),
    /** Human what 1. */
    HUMAN_WHAT1(Race.HUMAN),
    /** Human what 2. */
    HUMAN_WHAT2(Race.HUMAN),
    /** Human what 3. */
    HUMAN_WHAT3(Race.HUMAN),
    /** Human what 4. */
    HUMAN_WHAT4(Race.HUMAN),
    /** Human die. */
    HUMAN_DEAD(Race.HUMAN);

    /** Node sfx name. */
    public static final String NODE_SFX = "sfx";
    /** Dead attribute name. */
    public static final String ATT_DEAD = "dead";

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
            Thread.sleep(com.b3dgs.lionengine.Constant.HUNDRED / 2);
        }
        catch (final InterruptedException exception)
        {
            Verbose.exception(exception);
        }
        for (final Sfx sfx : Sfx.values())
        {
            sfx.audio.stop();
        }
    }

    /**
     * Load sfx.
     * 
     * @param setup The setup reference.
     * @param attribute The attribute name.
     * @return The loaded sfx, <code>null</code> if none.
     * @throws LionEngineException If invalid configuration.
     */
    public static List<Sfx> load(Setup setup, String attribute)
    {
        if (setup.hasNode(NODE_SFX))
        {
            if (setup.getRoot().getChild(NODE_SFX).hasAttribute(attribute))
            {
                final String[] attributes = setup.getString(attribute, NODE_SFX).split(Constant.SFX_SEPARATOR);
                final List<Sfx> sfx = new ArrayList<>();
                for (final String current : attributes)
                {
                    sfx.add(Sfx.valueOf(current));
                }
                return sfx;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Play a random sound.
     * 
     * @param sfx The random sounds.
     */
    public static void playRandom(List<Sfx> sfx)
    {
        if (!sfx.isEmpty())
        {
            final int id = UtilRandom.getRandomInteger(sfx.size() - 1);
            sfx.get(id).play();
        }
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
        return Medias.create(Folder.SOUNDS,
                             folder,
                             file.substring(file.indexOf(com.b3dgs.lionengine.Constant.UNDERSCORE) + 1));
    }

    /**
     * Play sound.
     */
    public void play()
    {
        audio.setVolume(Constant.VOLUME_DEFAULT);
        audio.play();
    }
}
