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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.Fovable;

/**
 * Check around to attack automatically on sight.
 */
@FeatureInterface
public class AutoAttack extends FeatureModel implements Routine, Recyclable
{
    private static final int CHECK_DELAY = 60;

    private final Tick tick = new Tick();
    private final Updatable checker;

    private final MapTilePath mapPath;
    private final Handler handler;

    private Updatable current;

    @FeatureGet private Fovable fovable;
    @FeatureGet private Attacker attacker;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private EntityStats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public AutoAttack(Services services, Setup setup)
    {
        super();

        mapPath = services.get(MapTilePath.class);
        handler = services.get(Handler.class);

        checker = extrp ->
        {
            tick.update(extrp);
            if (tick.elapsed(CHECK_DELAY))
            {
                final Transformable target = findTarget();
                if (target != null && pathfindable.setDestination(target))
                {
                    attacker.attack(target);
                    current = UpdatableVoid.getInstance();
                }
                tick.restart();
            }
        };
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        attacker.addListener(new AttackerListenerVoid()
        {
            @Override
            public void notifyAttackEnded(int damages, Transformable target)
            {
                current = checker;
            }
        });
    }

    private Transformable findTarget()
    {
        int ray = 1;
        final int tx = pathfindable.getInTileX() + 1;
        final int ty = pathfindable.getInTileY() + 1;
        while (ray < fovable.getInTileFov())
        {
            for (int x = -ray - 1; x < ray; x++)
            {
                for (int y = -ray - 1; y < ray; y++)
                {
                    if (x != tx && y != ty)
                    {
                        for (final Integer id : mapPath.getObjectsId(tx + x, ty + y))
                        {
                            final Featurable featurable = handler.get(id);
                            if (!stats.getRace().equals(featurable.getFeature(EntityStats.class).getRace()))
                            {
                                return featurable.getFeature(Transformable.class);
                            }
                        }
                    }
                }
            }
            ray++;
        }
        return null;
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        tick.restart();
        current = checker;
    }
}
