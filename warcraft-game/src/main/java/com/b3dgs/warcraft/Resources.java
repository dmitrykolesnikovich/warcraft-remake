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

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Alterable;

/**
 * Describes the resources.
 */
public final class Resources implements Updatable
{
    private final Alterable gold = new Alterable(99999);
    private final Alterable available = new Alterable(99);
    private final Alterable consumed = new Alterable(99);

    private double currentGold;

    /**
     * Create resources data.
     */
    public Resources()
    {
        super();
    }

    /**
     * Increase gold resource.
     * 
     * @param amount The amount of gold.
     */
    public void increaseGold(int amount)
    {
        gold.increase(amount);
    }

    /**
     * Decrease gold resource.
     * 
     * @param amount The amount of gold.
     */
    public void decreaseGold(int amount)
    {
        gold.decrease(amount);
    }

    /**
     * Increase available food.
     */
    public void increaseFood()
    {
        available.increase(1);
    }

    /**
     * Increase consumed food.
     */
    public void consumeFood()
    {
        consumed.increase(1);
    }

    /**
     * Get available food.
     * 
     * @return The available food.
     */
    public int getAvailableFood()
    {
        return available.getCurrent();
    }

    /**
     * Get consumed food.
     * 
     * @return The consumed food.
     */
    public int getConsumedFood()
    {
        return consumed.getCurrent();
    }

    /**
     * Get current gold resource.
     * 
     * @return The current gold resource.
     */
    public int getGold()
    {
        return (int) Math.round(currentGold);
    }

    /**
     * Check if can consume food.
     * 
     * @return <code>true</code> if enough food, <code>false</code> else.
     */
    public boolean isAvailableFood()
    {
        return consumed.getCurrent() < available.getCurrent();
    }

    /**
     * Check if has enough available gold.
     * 
     * @param amount The required amount of gold.
     * @return <code>true</code> if enough gold, <code>false</code> else.
     */
    public boolean isAvailableGold(int amount)
    {
        return gold.isEnough(amount);
    }

    @Override
    public void update(double extrp)
    {
        currentGold = (int) Math.ceil(UtilMath.curveValue(currentGold, gold.getCurrent(), 15) * 10) / 10.0;
    }
}
