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
package com.b3dgs.warcraft.world;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;

import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectionListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Handle world selection filter.
 */
public class WorldSelection
{
    private final AtomicReference<Race> race = new AtomicReference<>();
    private final AtomicBoolean moving = new AtomicBoolean();

    private final Player player;
    private final Hud hud;

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public WorldSelection(Services services)
    {
        super();

        player = services.get(Player.class);
        hud = services.get(Hud.class);

        final Selector selector = services.get(Selector.class);
        selector.addListener(new SelectionListener()
        {
            @Override
            public void notifySelectionStarted()
            {
                race.set(null);
                moving.set(false);
            }

            @Override
            public void notifySelected(List<Selectable> selection)
            {
                clearMenuIfNotOwned(selection);
            }
        });
        selector.setAccept(createFilter());
    }

    /**
     * Reset selection state.
     */
    public void reset()
    {
        race.set(null);
        moving.set(false);
    }

    private BiPredicate<List<Selectable>, Selectable> createFilter()
    {
        return (selected, selectable) ->
        {
            final EntityStats stats = selectable.getFeature(EntityStats.class);
            final Race current = stats.getRace();
            final boolean mover = stats.isMover();

            if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current) || !moving.get())
            {
                clearSelectedIfNextIsOwned(selected);
                if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current))
                {
                    race.set(current);
                }
            }
            if (mover)
            {
                moving.set(true);
            }
            if (stats.getHealthPercent() == 0 || moving.get() && !mover || !player.owns(current) && race.get() != null)
            {
                return false;
            }
            return race.compareAndSet(null, current) || current.equals(race.get()) && !Race.NEUTRAL.equals(current);
        };
    }

    private void clearSelectedIfNextIsOwned(List<Selectable> selected)
    {
        for (final Selectable old : selected)
        {
            old.onSelection(false);
        }
        selected.clear();
    }

    private void clearMenuIfNotOwned(List<Selectable> selected)
    {
        for (final Selectable current : selected)
        {
            if (!player.owns(current.getFeature(EntityStats.class).getRace()))
            {
                hud.clearMenus();
                break;
            }
        }
    }
}
