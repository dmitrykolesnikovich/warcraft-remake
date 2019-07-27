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
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.warcraft.Player;

/**
 * Represents food production.
 */
@FeatureInterface
public class FoodProduction extends FeatureModel implements Routine
{
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 115;

    private final Text text;
    private final Player player;

    @FeatureGet private Producible producible;
    @FeatureGet private EntityStats stats;

    /**
     * Create food.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public FoodProduction(Services services, Setup setup)
    {
        super();

        text = services.get(Text.class);
        player = services.get(Player.class);
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
                player.increaseFood();
            }
        });
    }

    @Override
    public void render(Graphic g)
    {
        if (player.owns(stats.getRace()))
        {
            text.draw(g, TEXT_X, TEXT_Y, player.getConsumedFood() + " of " + player.getAvailableFood());
        }
    }
}
