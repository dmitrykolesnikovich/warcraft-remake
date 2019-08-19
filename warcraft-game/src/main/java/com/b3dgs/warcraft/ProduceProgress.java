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

import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Progress bar handler for production progress.
 */
public class ProduceProgress implements Renderable
{
    private static final int PROGRESS_X = 2;
    private static final int PROGRESS_Y = 107;
    private static final int PROGRESS_WIDTH = 62;
    private static final int PROGRESS_HEIGHT = 5;
    private static final int PROGRESS_OFFSET = 2;

    /**
     * Create progress bar.
     * 
     * @return The created progress bar.
     */
    private static Bar createBar()
    {
        final Bar bar = new Bar(PROGRESS_WIDTH, PROGRESS_HEIGHT);
        bar.setLocation(PROGRESS_X + PROGRESS_OFFSET, PROGRESS_Y + PROGRESS_OFFSET);
        bar.setWidthPercent(0);
        bar.setHeightPercent(com.b3dgs.lionengine.Constant.HUNDRED);
        bar.setColorForeground(Constant.COLOR_HEALTH_GOOD);
        return bar;
    }

    private final Image progressBackground = Util.getImage("progress.png", PROGRESS_X, PROGRESS_Y);
    private final Image progressPercent = Util.getImage("progress_percent.png", PROGRESS_X, PROGRESS_Y);
    private final Bar progressBar = createBar();

    private boolean producing;

    /**
     * Start progress.
     */
    public void start()
    {
        producing = true;
        progressBar.setWidthPercent(0);
    }

    /**
     * Update progress.
     * 
     * @param percent The progress percent.
     */
    public void update(int percent)
    {
        progressBar.setWidthPercent(percent);
    }

    /**
     * Stop progress.
     */
    public void stop()
    {
        producing = false;
    }

    @Override
    public void render(Graphic g)
    {
        if (producing)
        {
            progressBackground.render(g);
            progressBar.render(g);
            progressPercent.render(g);
        }
    }
}
