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
package com.b3dgs.warcraft.map;

import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.strategy.map.FogOfWarStrategy;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.GameConfig;

/**
 * Fog of war implementation base.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FogOfWar
        extends FogOfWarStrategy<Tile>
{
    /**
     * Constructor.
     * 
     * @param config The game configuration.
     */
    public FogOfWar(GameConfig config)
    {
        final SpriteTiled hide = Drawable.loadSpriteTiled(UtilityMedia.get(AppWarcraft.TILES_DIR, "hide.png"), 16, 16);
        final SpriteTiled fog = Drawable.loadSpriteTiled(UtilityMedia.get(AppWarcraft.TILES_DIR, "fog.png"), 16, 16);
        hide.load(false);
        fog.load(false);
        setFogTiles(hide, fog);
        setFogOfWar(config.hide, config.fog);
    }
}
