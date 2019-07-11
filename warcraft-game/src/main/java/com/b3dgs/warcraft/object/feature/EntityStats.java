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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Icon renderer feature.
 */
@FeatureInterface
public class EntityStats extends FeatureModel implements Routine
{
    private static final ColorRgba COLOR_LIFE = new ColorRgba(0, 200, 0);
    private static final int ENTITY_INFO_MARGIN = 4;
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 98;
    private static final int BAR_LIFE_X = 31;
    private static final int BAR_LIFE_Y = 16;
    private static final int BAR_RED_PERCENT = 25;
    private static final int BAR_YELLOW_PERCENT = 50;

    private final Alterable health = new Alterable(60);
    private final Bar barHealth = new Bar(27, 3);
    private final String name;
    private final Image icon;

    private final Text text;

    /**
     * Create icon provider.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityStats(Services services, Setup setup)
    {
        super();

        text = services.get(Text.class);

        name = setup.getString("name");
        final Media media = setup.getIconFile();
        if (media != null)
        {
            icon = Drawable.loadImage(media);
            icon.load();
            icon.setLocation(Constant.ENTITY_INFO_X + ENTITY_INFO_MARGIN, Constant.ENTITY_INFO_Y + ENTITY_INFO_MARGIN);
        }
        else
        {
            icon = null;
        }
        health.fill();
        barHealth.setColorForeground(COLOR_LIFE);
        barHealth.setLocation((int) (icon.getX() + BAR_LIFE_X), (int) (icon.getY() + BAR_LIFE_Y));
    }

    /**
     * Apply damages.
     * 
     * @param damages The damages to apply.
     * @return <code>true</code> if empty health, <code>false</code> else.
     */
    public boolean applyDamages(int damages)
    {
        health.decrease(damages);
        updateHealthBar();
        return health.isEmpty();
    }

    /**
     * Get current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return health.getCurrent();
    }

    /**
     * Update bar size and color depending of health percent.
     */
    private void updateHealthBar()
    {
        final int percent = health.getPercent();
        barHealth.setWidthPercent(percent);
        if (percent < BAR_RED_PERCENT)
        {
            barHealth.setColorForeground(ColorRgba.RED);
        }
        else if (percent < BAR_YELLOW_PERCENT)
        {
            barHealth.setColorForeground(ColorRgba.YELLOW);
        }
    }

    @Override
    public void render(Graphic g)
    {
        barHealth.render(g);

        text.draw(g, TEXT_X, TEXT_Y, name);

        if (icon != null)
        {
            icon.render(g);
        }
    }
}
