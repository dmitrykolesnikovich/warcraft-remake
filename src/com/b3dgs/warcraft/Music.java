/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;

/**
 * Handle the music.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Music
{
    /** Blank. */
    HUMANS("humans"),
    /** Blizzard. */
    ORCS("orcs"),
    /** Click. */
    MENU("menu");

    /** Audio file extension. */
    private static final String AUDIO_FILE_EXTENSION = ".mid";

    /** Sound enabled. */
    private static boolean enabled;
    /** Volume. */
    private static int volume = 80;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        Music.enabled = enabled;
    }

    /**
     * Set the global music volume.
     * 
     * @param volume The music volume.
     */
    public static void setGlobalVolume(int volume)
    {
        Music.volume = volume;
    }

    /**
     * Load all music.
     */
    public static void loadAll()
    {
        for (final Music music : Music.values())
        {
            music.load();
        }
    }

    /**
     * Stop all music.
     */
    public static void stopAll()
    {
        for (final Music music : Music.values())
        {
            music.stop();
        }
    }

    /** Media. */
    private final Media media;
    /** Music module. */
    private Midi midi;

    /**
     * Constructor.
     * 
     * @param music The music.
     */
    private Music(String music)
    {
        media = UtilityMedia.get(AppWarcraft.MUSICS_DIR, music + Music.AUDIO_FILE_EXTENSION);
    }

    /**
     * Stop music.
     */
    public void stop()
    {
        midi.stop();
    }

    /**
     * Play the music.
     */
    public void play()
    {
        if (Music.enabled)
        {
            midi.setVolume(Music.volume);
            midi.play(true);
        }
    }

    /**
     * Load the music.
     */
    private void load()
    {
        if (midi == null)
        {
            midi = AudioMidi.loadMidi(media);
            if (media.getPath().contains("menu"))
            {
                midi.setLoop(6300, midi.getTicks() - 3680);
            }
        }
    }
}
