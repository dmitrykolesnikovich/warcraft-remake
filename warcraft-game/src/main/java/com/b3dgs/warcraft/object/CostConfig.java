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
package com.b3dgs.warcraft.object;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the entity cost data.
 */
public final class CostConfig
{
    /** Cost node name. */
    public static final String NODE_COST = "cost";
    /** Wood attribute name. */
    public static final String ATT_WOOD = "wood";
    /** Gold attribute name. */
    public static final String ATT_GOLD = "gold";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 27;

    /**
     * Import the data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static CostConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Import the data from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static CostConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_COST);
        final int wood = node.readInteger(ATT_WOOD);
        final int gold = node.readInteger(ATT_GOLD);

        return new CostConfig(wood, gold);
    }

    /**
     * Export the node from data.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(CostConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_COST);
        node.writeInteger(ATT_WOOD, config.getWood());
        node.writeInteger(ATT_GOLD, config.getGold());

        return node;
    }

    /** The wood value. */
    private final int wood;
    /** The gold value. */
    private final int gold;

    /**
     * Create configuration.
     * 
     * @param wood The wood value.
     * @param gold The gold value.
     */
    public CostConfig(int wood, int gold)
    {
        super();

        this.wood = wood;
        this.gold = gold;
    }

    /**
     * Get the wood value.
     * 
     * @return The wood value.
     */
    public int getWood()
    {
        return wood;
    }

    /**
     * Get the gold value.
     * 
     * @return The gold value.
     */
    public int getGold()
    {
        return gold;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + wood;
        result = prime * result + gold;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final CostConfig other = (CostConfig) object;
        return other.wood == wood && other.gold == gold;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [wood=")
                                            .append(wood)
                                            .append(", gold=")
                                            .append(gold)
                                            .append("]")
                                            .toString();
    }
}
