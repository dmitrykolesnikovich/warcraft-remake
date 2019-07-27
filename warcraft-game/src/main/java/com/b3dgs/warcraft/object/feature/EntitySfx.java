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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Sfx;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Represents sound handler.
 */
@FeatureInterface
public class EntitySfx extends FeatureModel
{
    /**
     * Load sfx.
     * 
     * @param setup The setup reference.
     * @param attribute The attribute name.
     * @return The loaded sfx, <code>null</code> if none.
     * @throws LionEngineException If invalid configuration.
     */
    private static List<Sfx> load(Setup setup, String attribute)
    {
        if (setup.hasNode(NODE_SFX))
        {
            if (setup.getRoot().getChild(NODE_SFX).hasAttribute(attribute))
            {
                final String[] attributes = setup.getString(attribute, NODE_SFX).split(Constant.SFX_SEPARATOR);
                final List<Sfx> sfx = new ArrayList<>();
                for (final String current : attributes)
                {
                    sfx.add(Sfx.valueOf(current));
                }
                return sfx;
            }
        }
        return Collections.emptyList();
    }

    private static final String NODE_SFX = "sfx";
    private static final String ATT_STARTED = "started";
    private static final String ATT_PRODUCED = "produced";
    private static final String ATT_SELECTED = "selected";
    private static final String ATT_ORDERED = "ordered";
    private static final String ATT_ATTACKED = "attacked";
    private static final String ATT_DEAD = "dead";

    private final List<Sfx> started;
    private final List<Sfx> produced;
    private final List<Sfx> selected;
    private final List<Sfx> ordered;
    private final List<Sfx> attacked;
    private final List<Sfx> dead;

    private final Player player;

    @FeatureGet private EntityStats stats;

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

        started = load(setup, ATT_STARTED);
        produced = load(setup, ATT_PRODUCED);
        selected = load(setup, ATT_SELECTED);
        ordered = load(setup, ATT_ORDERED);
        attacked = load(setup, ATT_ATTACKED);
        dead = load(setup, ATT_DEAD);

        player = services.get(Player.class);
    }

    /**
     * Called on production started.
     */
    public void onStarted()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(started);
        }
    }

    /**
     * Called on production ended.
     */
    public void onProduced()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(produced);
        }
    }

    /**
     * Called on selected.
     */
    public void onSelected()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(selected);
        }
    }

    /**
     * Called on ordered.
     */
    public void onOrdered()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(ordered);
        }
    }

    /**
     * Called on attacked.
     */
    public void onAttacked()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(attacked);
        }
    }

    /**
     * Called on dead.
     */
    public void onDead()
    {
        if (player.owns(stats.getRace()))
        {
            Sfx.playRandom(dead);
        }
    }
}
