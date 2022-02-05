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
package com.b3dgs.warcraft.object;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the entity stats data.
 */
public final class StatsConfig
{
    /** Stats node name. */
    public static final String NODE_STATS = "stats";
    /** Health attribute name. */
    public static final String ATT_HEALTH = "health";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 29;

    /**
     * Import the data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static StatsConfig imports(Configurer configurer)
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
    public static StatsConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_STATS);
        final int health = node.getInteger(ATT_HEALTH);

        return new StatsConfig(health);
    }

    /**
     * Export the node from data.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(StatsConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_STATS);
        node.writeInteger(ATT_HEALTH, config.getHealth());

        return node;
    }

    /** The health value. */
    private final int health;

    /**
     * Create configuration.
     * 
     * @param health The health value.
     */
    public StatsConfig(int health)
    {
        super();

        this.health = health;
    }

    /**
     * Get the health value.
     * 
     * @return The health value.
     */
    public int getHealth()
    {
        return health;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + health;
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
        final StatsConfig other = (StatsConfig) object;
        return other.health == health;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [health=")
                                            .append(health)
                                            .append("]")
                                            .toString();
    }
}
