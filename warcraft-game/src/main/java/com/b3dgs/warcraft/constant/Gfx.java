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
package com.b3dgs.warcraft.constant;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;

/**
 * Graphic resources.
 */
public enum Gfx
{
    /** Game font. */
    GAME_FONT(Medias.create("font.png")),

    /** Action background. */
    HUD_ACTION_BACKGROUND(Medias.create("action_background.png")),
    /** Entity stats. */
    HUD_STATS(Medias.create("entity_stats.png")),
    /** Progress bar. */
    HUD_PROGRESS(Medias.create("progress.png")),
    /** Progress bar percent. */
    HUD_PROGRESS_PERCENT(Medias.create("progress_percent.png")),
    /** Gold resource icon. */
    HUD_GOLD(Medias.create("gold.png")),
    /** Wood resource icon. */
    HUD_WOOD(Medias.create("wood.png")),

    /** Construction progress. */
    BUILDING_CONSTRUCTION(Medias.create(Folder.EFFECTS, "construction.png")),
    /** Burning building. */
    BUILDING_BURNING(Medias.create(Folder.EFFECTS, "burning.png")),

    /** Fog of war hidden layer. */
    FOG_HIDDEN(Medias.create(Folder.FOG, "hide.png")),
    /** Fog of war fogged layer. */
    FOG_FOGGED(Medias.create(Folder.FOG, "fog.png"));

    /** Associated resource. */
    private Image image;

    /**
     * Create gfx.
     * 
     * @param media The media reference.
     */
    Gfx(Media media)
    {
        image = Drawable.loadImage(media);
    }

    /**
     * Get the associated resource.
     * 
     * @return The associated resource.
     */
    public Resource get()
    {
        return image;
    }

    /**
     * Get associated surface.
     * 
     * @return The associated surface.
     */
    public ImageBuffer getSurface()
    {
        return image.getSurface();
    }
}
