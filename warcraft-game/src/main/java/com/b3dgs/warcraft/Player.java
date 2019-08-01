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
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Describes the player stats.
 */
public final class Player implements Updatable
{
    /** Wood type. */
    public static final String TYPE_WOOD = "wood";
    /** Gold type. */
    public static final String TYPE_GOLD = "gold";
    /** Curve speed. */
    private static final double CURVE_SPEED = 15.0;
    /** Curve round. */
    private static final double CURVE_ROUND = 10.0;

    /**
     * Check if type is wood.
     * 
     * @param type The type to check.
     * @return <code>true</code> if wood, <code>false</code> else.
     */
    public static boolean isWood(String type)
    {
        return TYPE_WOOD.equals(type);
    }

    /**
     * Check if type is gold.
     * 
     * @param type The type to check.
     * @return <code>true</code> if gold, <code>false</code> else.
     */
    public static boolean isGold(String type)
    {
        return TYPE_GOLD.equals(type);
    }

    private final Race race;
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
     * 
     * @param race The race reference.
     */
    public Player(Race race)
    {
        super();

        this.race = race;

        updateWood = extrp ->
        {
            if (!UtilMath.isBetween(getWood(), wood.getCurrent() - 4, wood.getCurrent() + 4))
            {
                currentWood = curve(currentWood, wood.getCurrent());
            }
            else
            {
                currentWood = wood.getCurrent();
                updaterWood = UpdatableVoid.getInstance();
            }
        };

        updateGold = extrp ->
        {
            if (!UtilMath.isBetween(getGold(), gold.getCurrent() - 4, gold.getCurrent() + 4))
            {
                currentGold = curve(currentGold, gold.getCurrent());
            }
            else
            {
                currentGold = gold.getCurrent();
                updaterGold = UpdatableVoid.getInstance();
            }
        };

        wood.set(com.b3dgs.lionengine.Constant.THOUSAND);
        gold.set(com.b3dgs.lionengine.Constant.THOUSAND);
        if (Constant.DEBUG)
        {
            wood.set(com.b3dgs.lionengine.Constant.THOUSAND * com.b3dgs.lionengine.Constant.THOUSAND);
            gold.set(com.b3dgs.lionengine.Constant.THOUSAND * com.b3dgs.lionengine.Constant.THOUSAND);
        }

        currentWood = wood.getCurrent();
        currentGold = gold.getCurrent();
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
     * Get the player race.
     * 
     * @return The player race.
     */
    public Race getRace()
    {
        return race;
    }

    /**
     * Get the race color.
     * 
     * @param other The other race to compare.
     * @return The race color.
     */
    public ColorRgba getColor(Race other)
    {
        final ColorRgba color;
        if (owns(other))
        {
            color = Constant.COLOR_ALLIES;
        }
        else if (Race.NEUTRAL.equals(other))
        {
            color = Constant.COLOR_NEUTRAL;
        }
        else
        {
            color = Constant.COLOR_ENEMIES;
        }
        return color;
    }

    /**
     * Check if player owns.
     * 
     * @param other The other reference.
     * @return <code>true</code> if owns, <code>false</code> else.
     */
    public boolean owns(Race other)
    {
        return race.equals(other);
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

    /**
     * Curve resource value.
     * 
     * @param current The current resource value.
     * @param dest The destination resource value.
     * @return The curved value.
     */
    private double curve(double current, int dest)
    {
        return (int) Math.ceil(UtilMath.curveValue(current, dest, CURVE_SPEED) * CURVE_ROUND) / CURVE_ROUND;
    }

    @Override
    public void update(double extrp)
    {
        updaterWood.update(extrp);
        updaterGold.update(extrp);
    }
}
