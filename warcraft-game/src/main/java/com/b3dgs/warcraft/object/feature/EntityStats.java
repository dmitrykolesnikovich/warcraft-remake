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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableConfig;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableConfig;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.warcraft.Race;
import com.b3dgs.warcraft.Util;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.StatsConfig;

/**
 * Icon renderer feature.
 */
@FeatureInterface
public class EntityStats extends FeatureModel implements Routine, Recyclable
{
    private static final String ATT_NAME = "name";

    private static final int ENTITY_INFO_MARGIN = 4;
    private static final int TEXT_X = 6;
    private static final int TEXT_Y = 98;
    private static final int BAR_LIFE_WIDTH = 27;
    private static final int BAR_LIFE_HEIGHT = 3;
    private static final int BAR_LIFE_X = 31;
    private static final int BAR_LIFE_Y = 16;

    private final Image stats = Util.getImage("entity_stats.png", Constant.ENTITY_INFO_X, Constant.ENTITY_INFO_Y);
    private final Bar barHealth = new Bar(BAR_LIFE_WIDTH, BAR_LIFE_HEIGHT);
    private final Alterable health;
    private final String name;
    private final Race race;
    private final boolean mover;
    private final Image icon;
    private final Integer layerRefresh;
    private final Integer layerDisplay;

    private final SpriteFont text;

    @FeatureGet private Layerable layerable;

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

        final LayerableConfig layerableConfig = LayerableConfig.imports(setup);
        layerRefresh = Integer.valueOf(layerableConfig.getLayerRefresh());
        layerDisplay = Integer.valueOf(layerableConfig.getLayerDisplay());

        final StatsConfig config = StatsConfig.imports(setup);
        health = new Alterable(config.getHealth());
        if (Constant.DEBUG)
        {
            health.setMax(10);
        }

        text = services.get(SpriteFont.class);

        final Media media = setup.getIconFile();
        icon = Drawable.loadImage(media);
        icon.load();
        icon.prepare();
        icon.setLocation(Constant.ENTITY_INFO_X + ENTITY_INFO_MARGIN, Constant.ENTITY_INFO_Y + ENTITY_INFO_MARGIN);

        name = setup.getString(ATT_NAME).toUpperCase(Locale.ENGLISH);

        barHealth.setColorForeground(Constant.COLOR_HEALTH_GOOD);
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
     * Restore health.
     * 
     * @param value The health to restore.
     * @return <code>true</code> if full health, <code>false</code> else.
     */
    public boolean heal(int value)
    {
        health.increase(value);
        updateHealthBar();
        return health.isFull();
    }

    /**
     * Get current life percent.
     * 
     * @return The current life percent.
     */
    public int getLife()
    {
        return health.getPercent();
    }

    /**
     * Check if full health.
     * 
     * @return <code>true</code> if full health, <code>false</code> else.
     */
    public boolean isFullHealth()
    {
        return health.isFull();
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
        if (percent < Constant.HEALTH_PERCENT_ALERT)
        {
            barHealth.setColorForeground(Constant.COLOR_HEALTH_ALERT);
        }
        else if (percent < Constant.HEALTH_PERCENT_WARN)
        {
            barHealth.setColorForeground(Constant.COLOR_HEALTH_WARN);
        }
        else
        {
            barHealth.setColorForeground(Constant.COLOR_HEALTH_GOOD);
        }
    }

    @Override
    public void render(Graphic g)
    {
        stats.render(g);
        barHealth.render(g);
        icon.render(g);

        text.draw(g, TEXT_X, TEXT_Y, Align.LEFT, name);
    }

    @Override
    public void recycle()
    {
        health.fill();
        updateHealthBar();
        layerable.setLayer(layerRefresh, layerDisplay);
    }
}
