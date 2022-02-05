/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.warcraft.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.State;
import com.b3dgs.warcraft.object.feature.EntitySfx;

/**
 * Die state implementation.
 */
class StateDieBase extends State
{
    private final Collidable collidable = model.getFeature(Collidable.class);
    private final Pathfindable pathfindable = model.getFeature(Pathfindable.class);
    private final Extractor extractor = model.getFeature(Extractor.class);
    private final Producer producer = model.getFeature(Producer.class);
    private final Attacker attacker = model.getFeature(Attacker.class);
    private final Selector selector = model.getServices().get(Selector.class);
    private final Hud hud = model.getServices().get(Hud.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateDieBase(EntityModel model, Animation animation)
    {
        super(model, animation);
    }

    @Override
    public void enter()
    {
        super.enter();

        model.getFeature(EntitySfx.class).onDead();
        final Selectable selectable = model.getFeature(Selectable.class);
        selectable.onSelection(false);
        if (selector.getSelection().remove(selectable))
        {
            hud.clearMenus();
        }
        collidable.setEnabled(false);
        pathfindable.stopMoves();
        extractor.stopExtraction();
        producer.stopProduction();
        attacker.stopAttack();
        pathfindable.clearPath();
    }
}
