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
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.warcraft.Player;

/**
 * Represents food production.
 */
@FeatureInterface
public class FoodProduction extends FeatureModel implements Routine
{
    private static final int TEXT_X = 3;
    private static final int TEXT_Y = 124;
    private static final int TEXT_OFFSET_X = 38;
    private static final String FOOD_USAGE = "FOOD USAGE:";
    private static final String FOOD_GROWN = "GROWN ";
    private static final String FOOD_USED = "USED ";
    private static final int FOOD_COUNT = 4;

    private final SpriteFont text = services.get(SpriteFont.class);
    private final Player player = services.get(Player.class);

    @FeatureGet private Producible producible;

    /**
     * Create food.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public FoodProduction(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        producible.addListener(new ProducibleListenerVoid()
        {
            @Override
            public void notifyProductionEnded(Producer producer)
            {
                player.increaseFood(FOOD_COUNT);
            }
        });
    }

    @Override
    public void render(Graphic g)
    {
        if (player.owns(this))
        {
            text.draw(g, TEXT_X, TEXT_Y, Align.LEFT, FOOD_USAGE);
            text.draw(g, TEXT_X + TEXT_OFFSET_X, TEXT_Y + 12, Align.RIGHT, FOOD_GROWN);
            text.draw(g, TEXT_X + TEXT_OFFSET_X, TEXT_Y + 22, Align.RIGHT, FOOD_USED);
            text.draw(g, TEXT_X + TEXT_OFFSET_X, TEXT_Y + 12, Align.LEFT, String.valueOf(player.getAvailableFood()));
            text.draw(g, TEXT_X + TEXT_OFFSET_X, TEXT_Y + 22, Align.LEFT, String.valueOf(player.getConsumedFood()));
        }
    }
}
