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

import java.util.Locale;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;

/**
 * Display current gold quantity.
 */
@FeatureInterface
public class GoldQuantityRenderer extends FeatureModel implements Routine
{
    private static final String GOLD_LEFT = "GOLD LEFT";
    private static final int GOLD_LEFT_TEXT_X = 5;
    private static final int GOLD_LEFT_TEXT_Y = 118;
    private static final int AMOUNT_TEXT_X = 10;
    private static final int AMOUNT_TEXT_Y = 128;

    private final SpriteFont text;

    @FeatureGet private Extractable extractable;

    /**
     * Create food.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public GoldQuantityRenderer(Services services, Setup setup)
    {
        super(services, setup);

        text = services.get(SpriteFont.class);
    }

    @Override
    public void render(Graphic g)
    {
        text.draw(g, GOLD_LEFT_TEXT_X, GOLD_LEFT_TEXT_Y, Align.LEFT, GOLD_LEFT);
        text.draw(g,
                  AMOUNT_TEXT_X,
                  AMOUNT_TEXT_Y,
                  Align.LEFT,
                  String.valueOf(extractable.getResourceQuantity()).toUpperCase(Locale.ENGLISH));
    }
}
