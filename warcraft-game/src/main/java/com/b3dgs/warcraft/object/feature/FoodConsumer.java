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
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListenerVoid;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Represents food consumption.
 */
@FeatureInterface
public class FoodConsumer extends FeatureModel
{
    private final Player player;

    @FeatureGet private Producible producible;

    /**
     * Create consumer.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public FoodConsumer(Services services, Setup setup)
    {
        super();

        player = services.get(Player.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (!Constant.DEBUG)
        {
            producible.addListener(new ProducibleListenerVoid()
            {
                @Override
                public void notifyProductionEnded(Producer producer)
                {
                    player.consumeFood();
                }
            });
        }
    }
}
