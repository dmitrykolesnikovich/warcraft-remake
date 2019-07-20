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

import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectionListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Handle the selected entities information on Hud.
 */
@FeatureInterface
public class EntityInfo extends FeatureModel implements Renderable, SelectionListener
{
    private static final int COUNT_X = 5;
    private static final int COUNT_Y = 88;

    private final Image stats = Drawable.loadImage(Medias.create("entity_stats.png"));
    private List<Selectable> selection = Collections.emptyList();
    private final Text text;

    /**
     * Create the entity information.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityInfo(Services services, Setup setup)
    {
        super();

        text = services.get(Text.class);

        stats.load();
        stats.prepare();
        stats.setLocation(Constant.ENTITY_INFO_X, Constant.ENTITY_INFO_Y);
    }

    @Override
    public void render(Graphic g)
    {
        final int count = selection.size();
        if (count == 1)
        {
            stats.render(g);
            for (final Selectable selectable : selection)
            {
                selectable.getFeature(Routines.class).render(g);
            }
        }
        else if (count > 1)
        {
            text.draw(g, COUNT_X, COUNT_Y, "Army: " + count);
        }
    }

    @Override
    public void notifySelected(List<Selectable> selection)
    {
        this.selection = selection;
    }
}
