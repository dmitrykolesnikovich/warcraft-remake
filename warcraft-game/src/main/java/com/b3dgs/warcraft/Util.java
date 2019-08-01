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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Warehouse;

/**
 * Utility functions.
 */
public final class Util
{
    /**
     * Get image from path and set location.
     * 
     * @param path The image path.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The loaded image.
     */
    public static Image getImage(String path, int x, int y)
    {
        final Image image = Drawable.loadImage(Medias.create(path));
        image.load();
        image.prepare();
        image.setLocation(x, y);
        return image;
    }

    /**
     * Get player warehouse.
     * 
     * @param services The services reference.
     * @return The player warehouse, <code>null</code> if none.
     */
    public static Tiled getWarehouse(Services services)
    {
        final Handler handler = services.get(Handler.class);
        final Player player = services.get(Player.class);

        for (final Warehouse warehouse : handler.get(Warehouse.class))
        {
            if (player.owns(warehouse.getFeature(EntityStats.class).getRace()))
            {
                return warehouse;
            }
        }
        return null;
    }

    /**
     * Private.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
