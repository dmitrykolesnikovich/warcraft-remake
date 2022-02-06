/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.warcraft.android;

import android.os.Bundle;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.android.ActivityGame;
import com.b3dgs.lionengine.android.graphic.EngineAndroid;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.adlmidi.AdlMidiFormat;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.warcraft.Loading;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Android entry point.
 */
public final class ActivityWarcraft extends ActivityGame
{
    /**
     * Constructor.
     */
    public ActivityWarcraft()
    {
        super();
    }

    @Override
    protected void start(Bundle bundle)
    {
        EngineAndroid.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, this);
        AudioFactory.addFormat(AdlMidiFormat.getFailsafe());

        Loader.start(Config.fullscreen(Constant.NATIVE), Loading.class);
    }
}
