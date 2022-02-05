/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.constant.Gfx;
import com.b3dgs.warcraft.object.CostConfig;
import com.b3dgs.warcraft.object.EntityModel;

/**
 * Produce button action.
 */
public class ProduceButton extends ActionModel
{
    private static final int TEXT_WOOD_X = 280;
    private static final int TEXT_GOLD_X = 325;
    private static final int TEXT_Y = 209;
    private static final int TEXT_OFFSET_X = 17;
    private static final String NODE_MEDIA = "media";

    private final Image wood = Util.getImage(Gfx.HUD_WOOD, TEXT_WOOD_X, TEXT_Y - 2);
    private final Image gold = Util.getImage(Gfx.HUD_GOLD, TEXT_GOLD_X, TEXT_Y - 1);
    private final CostConfig config;

    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ProduceButton(Services services, Setup setup)
    {
        super(services, setup);

        final Media target = Medias.create(setup.getText(NODE_MEDIA));
        final Factory factory = services.get(Factory.class);
        final Player player = services.get(Player.class);

        config = CostConfig.imports(new Configurer(target));

        actionable.setAction(() ->
        {
            if (player.isAvailableFood()
                && player.isAvailableWood(config.getWood())
                && player.isAvailableGold(config.getGold()))
            {
                player.decreaseResource(config.getWood(), config.getGold());

                final Featurable entity = factory.create(target);
                entity.getFeature(EntityModel.class).setVisible(false);

                final List<Selectable> selection = selector.getSelection();
                final int n = selection.size();
                for (int i = 0; i < n; i++)
                {
                    final Producer producer = selection.get(i).getFeature(Producer.class);
                    final Transformable transformable = producer.getFeature(Transformable.class);
                    entity.getFeature(Producible.class)
                          .setLocation(transformable.getX() + transformable.getWidth() / 2,
                                       transformable.getY() + transformable.getHeight() / 2);
                    producer.addToProductionQueue(entity);
                }
            }
        });
    }

    @Override
    public void render(Graphic g)
    {
        if (actionable.isOver())
        {
            text.draw(g, TEXT_WOOD_X + TEXT_OFFSET_X, TEXT_Y, Align.LEFT, String.valueOf(config.getWood()));
            text.draw(g, TEXT_GOLD_X + TEXT_OFFSET_X, TEXT_Y, Align.LEFT, String.valueOf(config.getGold()));
            wood.render(g);
            gold.render(g);
        }
    }
}
