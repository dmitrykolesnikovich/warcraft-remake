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
package com.b3dgs.warcraft.action;

import java.util.List;

import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.warcraft.object.feature.EntitySfx;

/**
 * Attack action.
 */
public class Attack extends ActionModel
{
    /**
     * Create attack action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Attack(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    protected boolean assign()
    {
        final List<Selectable> selection = selector.getSelection();
        final int n = selection.size();
        for (int i = 0; i < n; i++)
        {
            final int tx = map.getInTileX(cursor);
            final int ty = map.getInTileY(cursor);
            final Selectable selectable = selection.get(i);

            for (final Integer id : mapPath.getObjectsId(tx, ty))
            {
                final Transformable transformable = handler.get(id).getFeature(Transformable.class);
                if (selectable.getFeature(Transformable.class) != transformable)
                {
                    selectable.getFeature(Attacker.class).attack(transformable);
                }
            }

            if (i == 0)
            {
                selectable.getFeature(EntitySfx.class).onOrdered();
            }
        }
        return true;
    }
}
