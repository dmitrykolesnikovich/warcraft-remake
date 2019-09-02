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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Folder;
import com.b3dgs.warcraft.constant.Gfx;

/**
 * Loading screen.
 */
public final class Loading extends Sequence
{
    private static final String IMG_LOADING = "blizzard.png";

    private final Image background = Drawable.loadImage(Medias.create(Folder.MENU, IMG_LOADING));

    private boolean loaded;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Loading(Context context)
    {
        super(context, Constant.NATIVE);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        background.load();
        background.prepare();
        background.setOrigin(Origin.MIDDLE);
        background.setLocation(getWidth() / 2.0, getHeight() / 2.0);
    }

    @Override
    public void update(double extrp)
    {
        if (loaded)
        {
            for (final Gfx gfx : Gfx.values())
            {
                gfx.get().load();
            }
            end(Scene.class);
        }
        loaded = true;
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        background.dispose();
        loaded = false;
    }
}
