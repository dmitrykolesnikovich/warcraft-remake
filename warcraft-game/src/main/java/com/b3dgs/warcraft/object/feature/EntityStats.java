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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableConfig;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.StatsConfig;

/**
 * Icon renderer feature.
 */
@FeatureInterface
public class EntityStats extends FeatureModel implements Routine
{
    private static final String ATT_NAME = "name";

    private static final ColorRgba COLOR_LIFE = new ColorRgba(0, 200, 0);
    private static final int ENTITY_INFO_MARGIN = 4;
    private static final int TEXT_X = 5;
    private static final int TEXT_Y = 98;
    private static final int BAR_LIFE_X = 31;
    private static final int BAR_LIFE_Y = 16;
    private static final int BAR_RED_PERCENT = 25;
    private static final int BAR_YELLOW_PERCENT = 50;

    private final Image stats = Drawable.loadImage(Medias.create("entity_stats.png"));
    private final Alterable health;
    private final Bar barHealth = new Bar(27, 3);
    private final String name;
    private final Race race;
    private final boolean mover;
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

        final String path = setup.getMedia().getParentPath();
        if (path.contains(Race.ORC.name().toLowerCase(Locale.ENGLISH)))
        {
            race = Race.ORC;
        }
        else if (path.contains(Race.HUMAN.name().toLowerCase(Locale.ENGLISH)))
        {
            race = Race.HUMAN;
        }
        else
        {
            race = Race.NEUTRAL;
        }
        mover = setup.hasNode(PathfindableConfig.NODE_PATHFINDABLE);

        final StatsConfig config = StatsConfig.imports(setup);
        health = new Alterable(config.getHealth());

        text = services.get(Text.class);

        stats.load();
        stats.prepare();
        stats.setLocation(Constant.ENTITY_INFO_X, Constant.ENTITY_INFO_Y);

        name = setup.getString(ATT_NAME);
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
     * Get the race.
     * 
     * @return The race.
     */
    public Race getRace()
    {
        return race;
    }

    /**
     * Check if is mover.
     * 
     * @return <code>true</code> if mover, <code>false</code> else.
     */
    public boolean isMover()
    {
        return mover;
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
        stats.render(g);
        barHealth.render(g);

        text.draw(g, TEXT_X, TEXT_Y, name);

        if (icon != null)
        {
            icon.render(g);
        }
    }
}
