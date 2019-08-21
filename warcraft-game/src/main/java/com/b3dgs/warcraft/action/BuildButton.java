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
package com.b3dgs.warcraft.action;

import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.HudListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Sfx;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.constant.Gfx;
import com.b3dgs.warcraft.object.CostConfig;

/**
 * Build button action.
 */
public class BuildButton extends ActionModel
{
    private static final int TEXT_WOOD_X = 220;
    private static final int TEXT_GOLD_X = 265;
    private static final int TEXT_Y = 193;
    private static final int TEXT_OFFSET_X = 17;

    private final Image wood = Util.getImage(Gfx.HUD_WOOD, TEXT_WOOD_X, TEXT_Y - 2);
    private final Image gold = Util.getImage(Gfx.HUD_GOLD, TEXT_GOLD_X, TEXT_Y - 1);

    private final Media target;
    private final CostConfig config;
    private Rectangle area;
    private boolean valid;
    private Pathfindable mover;

    private final Factory factory = services.get(Factory.class);
    private final Viewer viewer = services.get(Viewer.class);
    private final Cursor cursor = services.get(Cursor.class);
    private final InputDevicePointer pointer = services.get(InputDevicePointer.class);
    private final Hud hud = services.get(Hud.class);
    private final Player player = services.get(Player.class);

    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public BuildButton(Services services, Setup setup)
    {
        super(services, setup);

        target = Medias.create(setup.getText("media").split("/"));
        config = CostConfig.imports(new Configurer(target));

        hud.addListener(new HudListener()
        {
            @Override
            public void notifyCreated(List<Selectable> selection, Actionable actionable)
            {
                mover = null;
                for (final Selectable selectable : selection)
                {
                    mover = selectable.getFeature(Pathfindable.class);
                    break;
                }
            }

            @Override
            public void notifyCanceled()
            {
                state.set(actionable);
                area = null;
                valid = false;
                mover = null;
            }
        });
    }

    @Override
    protected void action()
    {
        final SizeConfig size = SizeConfig.imports(new Xml(target));
        area = new Rectangle(0, 0, size.getWidth(), size.getHeight());
        hud.setCancelShortcut(() -> pointer.hasClickedOnce(3));
        cursor.setSurfaceId(Constant.CURSOR_ID);
        cursor.setVisible(false);
    }

    @Override
    protected boolean assign()
    {
        if (!valid)
        {
            return false;
        }
        for (final Selectable selectable : selector.getSelection())
        {
            if (player.isAvailableWood(config.getWood()) && player.isAvailableGold(config.getGold()))
            {
                player.decreaseWood(config.getWood());
                player.decreaseGold(config.getGold());

                final Featurable building = factory.create(target);
                final Producible producible = building.getFeature(Producible.class);
                producible.setLocation(area.getX(), area.getY());

                final Producer producer = selectable.getFeature(Producer.class);
                final Pathfindable pathfindable = producer.getFeature(Pathfindable.class);
                final Transformable transformable = producer.getFeature(Transformable.class);
                producer.setChecker(featurable -> UtilMath.getDistance(featurable.getFeature(Producible.class),
                                                                       transformable) < map.getTileWidth()
                                                  && pathfindable.isDestinationReached());

                pathfindable.setDestination(area);

                producer.addToProductionQueue(building);
            }
        }
        area = null;
        hud.clearMenus();
        hud.setCancelShortcut(() -> false);
        Sfx.NEUTRAL_BUILD.play();
        cursor.setVisible(true);
        return true;
    }

    @Override
    public void update(double extrp)
    {
        if (area != null)
        {
            area.set(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                     UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                     area.getWidthReal(),
                     area.getHeightReal());
            valid = mapPath.isAreaAvailable(area, mover);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (area != null && viewer.isViewable((Localizable) cursor, 0, 0))
        {
            if (valid)
            {
                g.setColor(Constant.COLOR_SELECTION);
            }
            else
            {
                g.setColor(Constant.COLOR_ENEMIES);
            }
            g.drawRect(viewer, Origin.BOTTOM_LEFT, area, false);
        }
        if (actionable.isOver())
        {
            text.draw(g, TEXT_WOOD_X + TEXT_OFFSET_X, TEXT_Y, Align.LEFT, String.valueOf(config.getWood()));
            text.draw(g, TEXT_GOLD_X + TEXT_OFFSET_X, TEXT_Y, Align.LEFT, String.valueOf(config.getGold()));
            wood.render(g);
            gold.render(g);
        }
    }
}
