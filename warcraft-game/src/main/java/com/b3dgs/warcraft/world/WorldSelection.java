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
package com.b3dgs.warcraft.world;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.HudListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectionListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.action.Locker;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.feature.EntityStats;

/**
 * Handle world selection filter.
 */
public class WorldSelection
{
    private static void switchExtractCarry(List<Selectable> selection, Actionable actionable)
    {
        for (final Selectable selectable : selection)
        {
            final boolean carry = selectable.getFeature(EntityModel.class).getCarryResource() != null;
            Util.switchExtractCarryAction(actionable, carry);
        }
    }

    private final AtomicReference<Race> race = new AtomicReference<>();
    private final AtomicBoolean moving = new AtomicBoolean();

    private final Player player;
    private final Hud hud;
    private final FogOfWar fogOfWar;

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
        fogOfWar = services.get(FogOfWar.class);

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

        final Cursor cursor = services.get(Cursor.class);
        hud.addListener(new HudListener()
        {
            @Override
            public void notifyCreated(List<Selectable> selection, Actionable actionable)
            {
                if (actionable.getFeature(Locker.class).isLocked(player))
                {
                    actionable.setEnabled(false);
                }
                else
                {
                    switchExtractCarry(selection, actionable);
                }
            }

            @Override
            public void notifyCanceled()
            {
                cursor.setVisible(true);
                cursor.setSurfaceId(0);
                selector.setEnabled(true);
                hud.setCancelShortcut(() -> false);
            }
        });
    }

    /**
     * Reset selection state.
     */
    public void reset()
    {
        race.set(null);
        moving.set(false);
    }

    private void clearMenuIfNotOwned(List<Selectable> selected)
    {
        for (final Selectable current : selected)
        {
            if (!player.owns(current))
            {
                hud.clearMenus();
                break;
            }
        }
    }

    private BiPredicate<List<Selectable>, Selectable> createFilter()
    {
        return (selected, selectable) ->
        {
            if (!selectable.hasFeature(EntityStats.class))
            {
                return false;
            }
            final EntityStats entity = selectable.getFeature(EntityStats.class);
            final Race current = entity.getRace();

            if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current) || !moving.get())
            {
                clearSelectedIfNextIsOwned(selected);
                if (Race.NEUTRAL.equals(race.get()) && !Race.NEUTRAL.equals(current))
                {
                    race.set(current);
                }
            }

            final boolean mover = entity.isMover();
            if (mover)
            {
                moving.set(true);
            }

            if (isInvalid(entity, mover))
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

    private boolean isInvalid(EntityStats entity, boolean mover)
    {
        return entity.getHealthPercent() == 0
               || moving.get() && !mover
               || !player.owns(entity) && race.get() != null
               || !entity.getFeature(EntityModel.class).isVisible()
               || !fogOfWar.isVisible(entity.getFeature(Pathfindable.class));
    }
}
