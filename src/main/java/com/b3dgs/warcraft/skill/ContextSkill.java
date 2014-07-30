/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.warcraft.Cursor;
import com.b3dgs.warcraft.entity.FactoryProduction;
import com.b3dgs.warcraft.entity.HandlerEntity;
import com.b3dgs.warcraft.map.Map;

/**
 * Represents the context related to skills.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ContextSkill
        implements ContextGame
{
    /** Skill background. */
    public final SpriteTiled background;
    /** Map. */
    final Map map;
    /** Cursor. */
    final Cursor cursor;
    /** Handler entity. */
    final HandlerEntity handlerEntity;
    /** Production factory. */
    final FactoryProduction factoryProduction;
    /** The timed message reference. */
    final TimedMessage message;

    /**
     * Constructor.
     * 
     * @param background The skill background.
     * @param map The map reference.
     * @param cursor The cursor reference.
     * @param handlerEntity The handler entity reference.
     * @param factoryProduction The production factory.
     * @param message The timed message reference.
     */
    public ContextSkill(SpriteTiled background, Map map, Cursor cursor, HandlerEntity handlerEntity,
            FactoryProduction factoryProduction, TimedMessage message)
    {
        this.background = background;
        this.map = map;
        this.cursor = cursor;
        this.handlerEntity = handlerEntity;
        this.factoryProduction = factoryProduction;
        this.message = message;
    }
}
