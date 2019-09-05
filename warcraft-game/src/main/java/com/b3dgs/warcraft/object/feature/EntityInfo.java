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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectionListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;

/**
 * Handle the selected entities information on Hud.
 */
@FeatureInterface
public class EntityInfo extends FeatureModel implements Renderable, SelectionListener, Recyclable
{
    private static final int COUNT_X = 5;
    private static final int COUNT_Y = 88;
    private static final String COUNT_TEXT = "ARMY: ";

    private final Renderable infoSingle;
    private final Renderable infoArmy;

    private Renderable info;
    private List<Selectable> selection;
    private int selectionCount;

    /**
     * Create the entity information.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityInfo(Services services, Setup setup)
    {
        super(services, setup);

        final SpriteFont text = services.get(SpriteFont.class);

        infoSingle = g ->
        {
            for (final Selectable selectable : selection)
            {
                selectable.getFeature(Routines.class).render(g);
            }
        };
        infoArmy = g -> text.draw(g, COUNT_X, COUNT_Y, Align.LEFT, COUNT_TEXT + selectionCount);
    }

    @Override
    public void render(Graphic g)
    {
        info.render(g);
    }

    @Override
    public void notifySelectionStarted()
    {
        info = RenderableVoid.getInstance();
    }

    @Override
    public void notifySelected(List<Selectable> selection)
    {
        this.selection = selection;
        selectionCount = selection.size();
        if (selectionCount == 1)
        {
            info = infoSingle;
            selection.get(0).getFeature(EntitySfx.class).onSelected();
        }
        else if (selectionCount > 1)
        {
            info = infoArmy;
        }
        else
        {
            info = RenderableVoid.getInstance();
        }
    }

    @Override
    public void recycle()
    {
        info = RenderableVoid.getInstance();
        selection = Collections.emptyList();
        selectionCount = 0;
    }
}
