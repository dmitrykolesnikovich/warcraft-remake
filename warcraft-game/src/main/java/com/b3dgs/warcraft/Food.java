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

import com.b3dgs.lionengine.game.Alterable;

/**
 * Describes the food.
 */
public class Food
{
    private final Alterable available = new Alterable(99);
    private final Alterable consumed = new Alterable(99);

    /**
     * Create food data.
     */
    public Food()
    {
        super();
    }

    /**
     * Increase available food.
     */
    public void increase()
    {
        available.increase(1);
    }

    /**
     * Increase consumed food.
     */
    public void consume()
    {
        consumed.increase(1);
    }

    /**
     * Check if can consume food.
     * 
     * @return <code>true</code> if enough food, <code>false</code> else.
     */
    public boolean isAvailable()
    {
        return consumed.getCurrent() < available.getCurrent();
    }
}
