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

import java.util.List;

import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.object.feature.EntitySfx;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Carry action.
 */
public class Carry extends ActionModel
{
    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Carry(Services services, Setup setup)
    {
        super(services, setup);

        actionable.setAction(() ->
        {
            final List<Selectable> selection = selector.getSelection();
            final int n = selection.size();
            for (int i = 0; i < n; i++)
            {
                final Selectable selectable = selection.get(i);
                final Tiled warehouse = Util.getWarehouse(services, selectable.getFeature(EntityStats.class).getRace());
                if (warehouse != null)
                {
                    selectable.getFeature(Pathfindable.class).setDestination(warehouse);
                    if (i == 0)
                    {
                        selectable.getFeature(EntitySfx.class).onOrdered();
                    }
                }
            }
        });
    }
}
