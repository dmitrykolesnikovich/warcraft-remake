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
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Alterable;

/**
 * Describes the resources.
 */
public final class Resources implements Updatable
{
    private final Alterable wood = new Alterable(99999);
    private final Alterable gold = new Alterable(99999);
    private final Alterable available = new Alterable(99);
    private final Alterable consumed = new Alterable(99);

    private final Updatable updateWood;
    private final Updatable updateGold;

    private Updatable updaterWood = UpdatableVoid.getInstance();
    private Updatable updaterGold = UpdatableVoid.getInstance();

    private double currentGold;
    private double currentWood;

    /**
     * Create resources data.
     */
    public Resources()
    {
        super();

        updateWood = extrp ->
        {
            if (getWood() < wood.getCurrent())
            {
                currentWood = (int) Math.ceil(UtilMath.curveValue(currentWood, wood.getCurrent(), 15) * 10) / 10.0;
            }
            else
            {
                currentWood = wood.getCurrent();
                updaterWood = UpdatableVoid.getInstance();
            }
        };

        updateGold = extrp ->
        {
            if (getGold() < gold.getCurrent())
            {
                currentGold = (int) Math.ceil(UtilMath.curveValue(currentGold, gold.getCurrent(), 15) * 10) / 10.0;
            }
            else
            {
                currentGold = gold.getCurrent();
                updaterGold = UpdatableVoid.getInstance();
            }
        };
    }

    /**
     * Increase wood resource.
     * 
     * @param amount The amount of wood.
     */
    public void increaseWood(int amount)
    {
        wood.increase(amount);
        updaterWood = updateWood;
    }

    /**
     * Decrease wood resource.
     * 
     * @param amount The amount of wood.
     */
    public void decreaseWood(int amount)
    {
        wood.decrease(amount);
        updaterWood = updateWood;
    }

    /**
     * Increase gold resource.
     * 
     * @param amount The amount of gold.
     */
    public void increaseGold(int amount)
    {
        gold.increase(amount);
        updaterGold = updateGold;
    }

    /**
     * Decrease gold resource.
     * 
     * @param amount The amount of gold.
     */
    public void decreaseGold(int amount)
    {
        gold.decrease(amount);
        updaterGold = updateGold;
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
     * Get current wood resource.
     * 
     * @return The current wood resource.
     */
    public int getWood()
    {
        return (int) Math.round(currentWood);
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
     * Check if has enough available wood.
     * 
     * @param amount The required amount of wood.
     * @return <code>true</code> if enough wood, <code>false</code> else.
     */
    public boolean isAvailableWood(int amount)
    {
        return wood.isEnough(amount);
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
        updaterWood.update(extrp);
        updaterGold.update(extrp);
    }
}
