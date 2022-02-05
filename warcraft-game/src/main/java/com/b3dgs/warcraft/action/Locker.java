/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.warcraft.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Handle the action lock.
 */
@FeatureInterface
public class Locker extends FeatureModel
{
    private static final String NODE_LOCK = "lock";
    private static final String ATT_VALUES = "values";

    /**
     * Load defined locks.
     * 
     * @param setup The setup reference.
     * @return The loaded unlocks.
     */
    private static Set<String> loadLocks(Setup setup)
    {
        if (setup.hasNode(NODE_LOCK))
        {
            return new HashSet<>(Arrays.asList(setup.getString(ATT_VALUES, NODE_LOCK).split(Constant.SFX_SEPARATOR)));
        }
        return Collections.emptySet();
    }

    private final Set<String> lockers;

    /**
     * Create feature
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Locker(Services services, Setup setup)
    {
        super(services, setup);

        lockers = loadLocks(setup);
    }

    /**
     * Check if locked.
     * 
     * @param player The player reference.
     * @return <code>true</code> if unlocked, <code>false</code> else.
     */
    public boolean isLocked(Player player)
    {
        for (final String locker : lockers)
        {
            if (!player.isUnlocked(locker))
            {
                return true;
            }
        }
        return false;
    }
}
