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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.Player;

/**
 * Right click move implementation.
 */
@FeatureInterface
public class RightClickMove extends FeatureModel implements RightClickHandler
{
    private final Cursor cursor = services.get(Cursor.class);
    private final Player player = services.get(Player.class);

    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Attacker attacker;
    @FeatureGet private EntitySfx sfx;

    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public RightClickMove(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void execute()
    {
        if (player.owns(this))
        {
            attacker.stopAttack();
            pathfindable.setDestination(cursor);
            sfx.onOrdered();
        }
    }
}
