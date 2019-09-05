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

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.tile.map.Orientable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.Sfx;

/**
 * Represents ability to throw something.
 */
@FeatureInterface
public class Thrower extends FeatureModel implements Routine
{
    private final Viewer viewer = services.get(Viewer.class);

    @FeatureGet private Attacker attacker;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Pathfindable pathfindable;
    @FeatureGet private EntitySfx sfx;

    /**
     * Create thrower.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Thrower(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        attacker.addListener(new AttackerListenerVoid()
        {
            @Override
            public void notifyAttackEnded(Transformable target, int damages)
            {
                sfx.onAttacked();
                launcher.fire(attacker.getTarget());
            }
        });
        launcher.addListener((LaunchableListener) launchable ->
        {
            launchable.getFeature(Orientable.class).setOrientation(pathfindable.getOrientation());
            launchable.getFeature(Collidable.class).addListener((other, w, b) ->
            {
                if (other != collidable)
                {
                    if (viewer.isViewable(other.getFeature(Transformable.class), 0, 0))
                    {
                        Sfx.NEUTRAL_ARROWHIT.play();
                    }
                    if (other.getFeature(EntityStats.class).applyDamages(attacker.getAttackDamages()))
                    {
                        attacker.stopAttack();
                    }
                    launchable.getFeature(Identifiable.class).destroy();
                }
            });
        });
    }

    @Override
    public void update(double extrp)
    {
        launcher.update(extrp);
    }
}
