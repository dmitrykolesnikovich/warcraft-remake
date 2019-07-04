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
package com.b3dgs.warcraft.action;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Action model implementation..
 */
public class ActionModel extends FeaturableModel implements Updatable, Renderable
{
    private static final int CURSOR_OFFSET = -5;

    /** Actionable reference. */
    protected final Actionable actionable;
    /** Assignable reference. */
    protected final Assignable assignable;
    /** Current state reference. */
    protected final AtomicReference<Updatable> state;

    /** Map reference. */
    protected final MapTile map;
    /** Map path reference. */
    protected final MapTilePath mapPath;
    /** Cursor reference. */
    protected final Cursor cursor;
    /** Selector reference. */
    protected final Selector selector;
    /** Handler reference. */
    protected final Handler handler;
    /** Text reference. */
    protected final Text text;

    /**
     * Create move action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ActionModel(Services services, Setup setup)
    {
        super();

        map = services.get(MapTile.class);
        mapPath = services.get(MapTilePath.class);
        cursor = services.get(Cursor.class);
        selector = services.get(Selector.class);
        handler = services.get(Handler.class);
        text = services.get(Text.class);

        addFeature(new LayerableModel(Constant.LAYER_SELECTION, Constant.LAYER_MENUS_RENDER));

        final SpriteAnimated background = Drawable.loadSpriteAnimated(Medias.create("action_background.png"), 2, 1);
        background.load();
        background.prepare();

        actionable = addFeatureAndGet(new ActionableModel(services, setup));
        state = new AtomicReference<>(actionable);
        assignable = addFeatureAndGet(new AssignableModel(services));

        actionable.setClickAction(1);
        actionable.setAction(() ->
        {
            cursor.setSurfaceId(1);
            cursor.setRenderingOffset(CURSOR_OFFSET, CURSOR_OFFSET);
            selector.setEnabled(false);
            state.set(assignable);
            ActionModel.this.action();
        });

        assignable.setClickAssign(1);
        assignable.setAssign(() ->
        {
            ActionModel.this.assign();
            cursor.setSurfaceId(0);
            cursor.setRenderingOffset(0, 0);
            selector.setEnabled(true);
            state.set(actionable);
        });

        final SpriteTiled surface = Drawable.loadSpriteTiled(setup.getSurface(), 27, 19);
        surface.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
        background.setLocation(actionable.getButton().getX() - 2, actionable.getButton().getY() - 2);

        addFeature(new RefreshableModel(extrp ->
        {
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
            state.get().update(extrp);
            ActionModel.this.update(extrp);
        }));

        addFeature(new DisplayableModel(g ->
        {
            if (actionable.isEnabled())
            {
                background.render(g);
                surface.render(g);
                ActionModel.this.render(g);
            }
        }));
    }

    /**
     * Executed action. Does nothing by default.
     */
    protected void action()
    {
        // Nothing by default
    }

    /**
     * Executed assign. Does nothing by default.
     */
    protected void assign()
    {
        // Nothing by default
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void update(double extrp)
    {
        // Nothing by default
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void render(Graphic g)
    {
        // Nothing by default
    }
}
