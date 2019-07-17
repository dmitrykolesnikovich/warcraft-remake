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
package com.b3dgs.warcraft.object;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends FeatureModel
{
    private final SpriteAnimated surface;
    private final Hud hud;
    private final Selector selector;
    private final Services services;

    @FeatureGet private Collidable collidable;
    @FeatureGet private Selectable selectable;

    private boolean visible = true;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityModel(Services services, Setup setup)
    {
        super();

        this.services = services;

        final FramesConfig config = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.setFrameOffsets(config.getOffsetX(), config.getOffsetY());

        hud = services.get(Hud.class);
        selector = services.get(Selector.class);
    }

    /**
     * Set the visible flag.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        collidable.setEnabled(visible);
        if (!visible && selector.getSelection().remove(selectable))
        {
            selectable.onSelection(false);
            hud.clearMenus();
        }
    }

    /**
     * Check visible flag.
     * 
     * @return <code>true</code> if visible, <code>false</code> else.
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Get the surface reference.
     * 
     * @return The surface reference.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }

    /**
     * Get the services reference.
     * 
     * @return The services reference.
     */
    public Services getServices()
    {
        return services;
    }
}
