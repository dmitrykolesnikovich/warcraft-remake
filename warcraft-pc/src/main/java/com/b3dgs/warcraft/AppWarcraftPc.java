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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.adlmidi.AdlMidiFormat;
import com.b3dgs.lionengine.audio.wav.WavFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Program starts here.
 */
public final class AppWarcraftPc
{
    /** Default display. */
    private static final Resolution DEFAULT_RESOLUTION = new Resolution(1280, 720, 60);

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppWarcraftPc.class);
        Loader.start(Config.windowed(DEFAULT_RESOLUTION), Loading.class);
        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(new AdlMidiFormat());
        AdlMidiFormat.setDefaultBank(Constant.SOUND_BANK_ID);
    }

    /**
     * Private constructor.
     */
    private AppWarcraftPc()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
