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
package com.b3dgs.warcraft.action;

import java.util.List;

import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.object.feature.EntitySfx;

/**
 * Stop action.
 */
public class Stop extends ActionModel
{
    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Stop(Services services, Setup setup)
    {
        super(services, setup);

        actionable.setAction(() ->
        {
            final List<Selectable> selection = selector.getSelection();
            final int n = selection.size();
            for (int i = 0; i < n; i++)
            {
                selection.get(i).getFeature(Pathfindable.class).stopMoves();
                selection.get(i).getFeature(Attacker.class).stopAttack();
                selection.get(i).getFeature(Extractor.class).stopExtraction();
            }
            if (n == 1)
            {
                selection.get(0).getFeature(EntitySfx.class).onOrdered();
            }
        });
    }
}
