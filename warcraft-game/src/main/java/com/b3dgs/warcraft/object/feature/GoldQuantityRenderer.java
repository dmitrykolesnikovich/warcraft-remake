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

import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Display current gold quantity.
 */
@FeatureInterface
public class GoldQuantityRenderer extends FeatureModel implements Routine
{
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 115;

    private final Text text;

    @FeatureGet private Extractable extractable;

    /**
     * Create food.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public GoldQuantityRenderer(Services services, Setup setup)
    {
        super();

        text = services.get(Text.class);
    }

    @Override
    public void render(Graphic g)
    {
        text.draw(g, TEXT_X, TEXT_Y, String.valueOf(extractable.getResourceQuantity()));
    }
}
