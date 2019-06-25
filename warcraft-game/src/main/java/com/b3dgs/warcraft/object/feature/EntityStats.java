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
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Icon renderer feature.
 */
@FeatureInterface
public class EntityStats extends FeatureModel implements Renderable
{
    private static final ColorRgba COLOR_LIFE = new ColorRgba(0, 200, 0);
    private static final int ENTITY_INFO_MARGIN = 4;
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 98;
    private static final int BAR_LIFE_X = 31;
    private static final int BAR_LIFE_Y = 16;

    private final Alterable life = new Alterable(60);
    private final Bar barLife = new Bar(27, 3);
    private final String name;
    private final Image icon;
    private boolean visible = true;

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
        life.fill();
        barLife.setColorForeground(COLOR_LIFE);
        barLife.setLocation((int) (icon.getX() + BAR_LIFE_X), (int) (icon.getY() + BAR_LIFE_Y));
    }

    /**
     * Set the visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Check visibility flag.
     * 
     * @return <code>true</code> if visible, <code>false</code> else.
     */
    public boolean isVisible()
    {
        return visible;
    }

    @Override
    public void render(Graphic g)
    {
        barLife.setWidthPercent(life.getPercent());
        barLife.render(g);

        text.draw(g, TEXT_X, TEXT_Y, name);

        if (icon != null)
        {
            icon.render(g);
        }
    }
}
