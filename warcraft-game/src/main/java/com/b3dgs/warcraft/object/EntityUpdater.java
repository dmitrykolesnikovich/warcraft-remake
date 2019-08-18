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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Entity updating implementation.
 */
@FeatureInterface
public class EntityUpdater extends FeatureModel implements Refreshable
{
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private Extractor extractor;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Producer producer;
    @FeatureGet private Selectable selectable;
    @FeatureGet private Attacker attacker;
    @FeatureGet private EntityStats stats;
    @FeatureGet private Routines routines;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     */
    public EntityUpdater(Services services)
    {
        super();
    }

    /**
     * Update mirror depending on orientation.
     */
    private void updateMirror()
    {
        final int sx = UtilMath.getSign(pathfindable.getMoveX());
        final int sy = UtilMath.getSign(pathfindable.getMoveY());
        Orientation orientation = Orientation.get(sx, sy);
        if (orientation == null)
        {
            orientation = pathfindable.getOrientation();
        }
        if (orientation.ordinal() > Orientation.ORIENTATIONS_NUMBER_HALF)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void update(double extrp)
    {
        routines.update(extrp);
        stateHandler.update(extrp);
        if (stats.getHealthPercent() > 0)
        {
            pathfindable.update(extrp);
            attacker.update(extrp);
            producer.update(extrp);
            extractor.update(extrp);
        }
        stateHandler.postUpdate();
        updateMirror();
        mirrorable.update(extrp);
        animatable.update(extrp);
    }
}
