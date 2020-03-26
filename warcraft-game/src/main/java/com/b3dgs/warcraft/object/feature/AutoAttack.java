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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
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
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.Fovable;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.Util;

/**
 * Check around to attack automatically on sight when idle.
 */
@FeatureInterface
public class AutoAttack extends FeatureModel implements Routine, Recyclable
{
    private static final int CHECK_DELAY = 30;

    private final Tick tick = new Tick();
    private final Updatable checker;

    private final MapTilePath mapPath = services.get(MapTilePath.class);
    private final Handler handler = services.get(Handler.class);

    private boolean force;

    @FeatureGet private Fovable fovable;
    @FeatureGet private Attacker attacker;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler state;
    @FeatureGet private EntityStats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public AutoAttack(Services services, Setup setup)
    {
        super(services, setup);

        final MapTile map = services.get(MapTile.class);
        checker = extrp ->
        {
            tick.update(extrp);
            if (canAutoAttack())
            {
                final Transformable target = findTarget();
                if (target != null
                    && (Util.getDistanceInTile(map, transformable, target) < 1.5
                        || pathfindable.setDestination(target)))
                {
                    attacker.attack(target);
                }
                tick.restart();
            }
        };
    }

    /**
     * Set force attack flag.
     * 
     * @param force <code>true</code> to force checking, <code>false</code> else.
     */
    public void setForce(boolean force)
    {
        this.force = force;
    }

    /**
     * Check if can auto attack.
     * 
     * @return <code>true</code> if can auto attack, <code>false</code> else.
     */
    private boolean canAutoAttack()
    {
        return tick.elapsed(CHECK_DELAY)
               && stats.getHealthPercent() > 0
               && (force || !pathfindable.isMoving())
               && (attacker.getTarget() == null
                   || attacker.getTarget().getFeature(EntityStats.class).getHealthPercent() == 0);
    }

    /**
     * Find closest target on sight.
     * 
     * @return The target found, <code>null</code> if none.
     */
    private Transformable findTarget()
    {
        int ray = 1;
        while (ray < fovable.getInTileFov())
        {
            final Transformable transformable = findTarget(ray);
            if (transformable != null)
            {
                return transformable;
            }
            ray++;
        }
        return null;
    }

    /**
     * Find closest target with the specified ray search.
     * 
     * @param ray The ray search.
     * @return The target found, <code>null</code> if none.
     */
    private Transformable findTarget(int ray)
    {
        for (int ox = -ray; ox <= ray; ox++)
        {
            for (int oy = -ray; oy <= ray; oy++)
            {
                final Transformable target = findTarget(ox, oy);
                if (target != null)
                {
                    return target;
                }
            }
        }
        return null;
    }

    /**
     * Find closest target on location.
     * 
     * @param ox The horizontal location offset.
     * @param oy The vertical location offset.
     * @return The target found, <code>null</code> if none.
     */
    private Transformable findTarget(int ox, int oy)
    {
        final int tx = pathfindable.getInTileX();
        final int ty = pathfindable.getInTileY();
        if (ox != 0 && oy != 0)
        {
            for (final Integer id : mapPath.getObjectsId(tx + ox, ty + oy))
            {
                final Featurable featurable = handler.get(id);
                final EntityStats statsTarget = featurable.getFeature(EntityStats.class);
                final Race race = statsTarget.getRace();
                if (!race.equals(Race.NEUTRAL) && !stats.getRace().equals(race) && statsTarget.getHealthPercent() > 0)
                {
                    return featurable.getFeature(Transformable.class);
                }
            }
        }
        return null;
    }

    @Override
    public void update(double extrp)
    {
        checker.update(extrp);
    }

    @Override
    public void recycle()
    {
        force = false;
        tick.restart();
    }
}
