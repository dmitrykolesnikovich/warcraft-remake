/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.warcraft.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.SizeData;
import com.b3dgs.lionengine.game.strategy.ability.producer.FactoryProductionStrategy;
import com.b3dgs.warcraft.map.Map;

/**
 * The production factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryProduction
        extends FactoryProductionStrategy<Entity, ProductionCost, ProducibleEntity>
{
    /**
     * Constructor.
     */
    public FactoryProduction()
    {
        super();
    }

    /*
     * FactoryProductionStrategy
     */

    @Override
    public ProducibleEntity create(Media media)
    {
        final Configurable configurable = getSetup(media).getConfigurable();
        final int step = configurable.getInteger("steps", "cost");
        final int gold = configurable.getInteger("gold", "cost");
        final int wood = configurable.getInteger("wood", "cost");
        final SizeData sizeData = configurable.getSize();

        final ProductionCost cost = new ProductionCost(step, gold, wood);
        final ProducibleEntity producible = new ProducibleEntity(media, cost, sizeData.getWidth() / Map.TILE_WIDTH,
                sizeData.getHeight() / Map.TILE_HEIGHT);

        return producible;
    }

    @Override
    public ProducibleEntity create(Media media, int tx, int ty)
    {
        final ProducibleEntity producible = create(media);

        producible.setLocation(tx, ty);

        return producible;
    }

    @Override
    protected SetupGame createSetup(Media media)
    {
        return new SetupGame(media);
    }
}
