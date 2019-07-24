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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.warcraft.Resources;
import com.b3dgs.warcraft.object.CostConfig;
import com.b3dgs.warcraft.object.feature.EntitySfx;
import com.b3dgs.warcraft.object.feature.FoodConsumer;

/**
 * Produce button action.
 */
public class ProduceButton extends ActionModel
{
    /**
     * Create progress bar.
     * 
     * @param actionable The actionable reference.
     * @return The created progress bar.
     */
    private static Bar createBar(Actionable actionable)
    {
        final Area area = actionable.getButton();
        final Bar bar = new Bar(area.getWidth(), area.getHeight());
        bar.setLocation((int) area.getX(), (int) area.getY());
        bar.setWidthPercent(0);
        bar.setHeightPercent(100);
        bar.setColorForeground(ColorRgba.GREEN);
        return bar;
    }

    /** Production progress bar. */
    private final Bar progress = createBar(actionable);

    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ProduceButton(Services services, Setup setup)
    {
        super(services, setup);

        final Media target = Medias.create(setup.getText("media").split(Constant.SLASH));
        final Factory factory = services.get(Factory.class);
        final Resources resources = services.get(Resources.class);

        actionable.setAction(() ->
        {
            final CostConfig config = CostConfig.imports(new Configurer(target));
            if (resources.isAvailableFood()
                && resources.isAvailableWood(config.getWood())
                && resources.isAvailableGold(config.getGold()))
            {
                resources.decreaseWood(config.getWood());
                resources.decreaseGold(config.getGold());

                final Featurable entity = factory.create(target);
                if (entity.hasFeature(FoodConsumer.class))
                {
                    resources.consumeFood();
                }
                final Producible producible = entity.getFeature(Producible.class);
                producible.addListener(createListener(producible));

                final List<Selectable> selection = selector.getSelection();
                final int n = selection.size();
                for (int i = 0; i < n; i++)
                {
                    final Producer producer = selection.get(i).getFeature(Producer.class);
                    producer.addToProductionQueue(entity);
                }
            }
        });
    }

    /**
     * Create production listener.
     * 
     * @param producible The producible in production.
     * @return The created listener.
     */
    private ProducibleListener createListener(Producible producible)
    {
        return new ProducibleListenerVoid()
        {
            @Override
            public void notifyProductionStarted(Producer producer)
            {
                producible.getFeature(EntitySfx.class).onStarted();
            }

            @Override
            public void notifyProductionProgress(Producer producer)
            {
                progress.setWidthPercent(producer.getProgressPercent());
            }

            @Override
            public void notifyProductionEnded(Producer producer)
            {
                teleportOutside(producible, producer);
                progress.setWidthPercent(0);
                producible.getFeature(EntitySfx.class).onProduced();
            }
        };
    }

    /**
     * Teleport producer outside producible area.
     * 
     * @param producible The producible reference.
     * @param producer The producer to teleport.
     */
    private void teleportOutside(Producible producible, Producer producer)
    {
        final Pathfindable pathfindable = producible.getFeature(Pathfindable.class);
        final CoordTile coord = map.getFeature(MapTilePath.class)
                                   .getFreeTileAround(pathfindable, producer.getFeature(Pathfindable.class));
        pathfindable.setLocation(coord);
    }

    @Override
    public void render(Graphic g)
    {
        progress.render(g);
    }
}
