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

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.constant.Folder;

/**
 * Represents something that can explode.
 */
@FeatureInterface
public class Explodable extends FeatureModel implements Routine
{
    private final Spawner spawner;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private EntityStats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Explodable(Services services, Setup setup)
    {
        super();

        spawner = services.get(Spawner.class);
    }

    @Override
    public void update(double extrp)
    {
        if (stats.getLife() == 0)
        {
            ((com.b3dgs.warcraft.object.Effect) spawner.spawn(Medias.create(Folder.EFFECTS, "explode.xml"),
                                                              transformable)).start(transformable.getWidth(),
                                                                                    transformable.getHeight());
            pathfindable.clearPath();
            identifiable.destroy();
        }
    }
}
