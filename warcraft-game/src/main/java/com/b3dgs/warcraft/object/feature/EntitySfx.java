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

import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Sfx;

/**
 * Represents sound handler.
 */
@FeatureInterface
public class EntitySfx extends FeatureModel
{
    private static final String ATT_STARTED = "started";
    private static final String ATT_PRODUCED = "produced";
    private static final String ATT_SELECTED = "selected";
    private static final String ATT_ORDERED = "ordered";
    private static final String ATT_ATTACKED = "attacked";

    private final List<Sfx> started;
    private final List<Sfx> produced;
    private final List<Sfx> selected;
    private final List<Sfx> ordered;
    private final List<Sfx> attacked;
    private final List<Sfx> dead;

    private final Viewer viewer;
    private final Player player;
    private final FogOfWar fogOfWar;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Pathfindable pathfindable;

    /**
     * Create producing.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @throws LionEngineException If invalid configuration.
     */
    public EntitySfx(Services services, Setup setup)
    {
        super();

        started = Sfx.load(setup, ATT_STARTED);
        produced = Sfx.load(setup, ATT_PRODUCED);
        selected = Sfx.load(setup, ATT_SELECTED);
        ordered = Sfx.load(setup, ATT_ORDERED);
        attacked = Sfx.load(setup, ATT_ATTACKED);
        dead = Sfx.load(setup, Sfx.ATT_DEAD);

        viewer = services.get(Viewer.class);
        player = services.get(Player.class);
        fogOfWar = services.get(FogOfWar.class);
    }

    /**
     * Called on production started.
     */
    public void onStarted()
    {
        if (isVisible())
        {
            Sfx.playRandom(started);
        }
    }

    /**
     * Called on production ended.
     */
    public void onProduced()
    {
        if (isVisible() && player.owns(this))
        {
            Sfx.playRandom(produced);
        }
    }

    /**
     * Called on selected.
     */
    public void onSelected()
    {
        if (isVisible() && player.owns(this))
        {
            Sfx.playRandom(selected);
        }
    }

    /**
     * Called on ordered.
     */
    public void onOrdered()
    {
        if (isVisible())
        {
            Sfx.playRandom(ordered);
        }
    }

    /**
     * Called on attacked.
     */
    public void onAttacked()
    {
        if (isVisible())
        {
            Sfx.playRandom(attacked);
        }
    }

    /**
     * Called on dead.
     */
    public void onDead()
    {
        if (isVisible())
        {
            Sfx.playRandom(dead);
        }
    }

    /**
     * Check if visible on camera and not fogged.
     * 
     * @return <code>true</code> if truly visible, <code>false</code> else.
     */
    private boolean isVisible()
    {
        return viewer.isViewable(transformable, 0, 0) && fogOfWar.isVisible(pathfindable);
    }
}
