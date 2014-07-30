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

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.purview.Fabricable;
import com.b3dgs.lionengine.game.strategy.skill.SetupSkillStrategy;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.RaceType;

/**
 * Setup skill implementation.
 * 
 * @author Pierre-Alexandre
 */
public final class SetupSkill
        extends SetupSkillStrategy
{
    /** Skill icon. */
    public final SpriteTiled icon;
    /** Gold. */
    public final Sprite gold;
    /** Wood. */
    public final Sprite wood;

    /**
     * Constructor.
     * 
     * @param config The config media.
     * @param context The skill context.
     * @param type The skill type.
     */
    public SetupSkill(Media config, ContextSkill context, Class<? extends Fabricable> type)
    {
        super(config, context);

        final RaceType race = RaceType.getRace(type);
        icon = Drawable.loadSpriteTiled(
                Core.MEDIA.create(AppWarcraft.SKILLS_DIR, race.getPath(), configurable.getText("icon")), 27, 19);
        gold = Drawable.loadSprite(Core.MEDIA.create("gold.png"));
        wood = Drawable.loadSprite(Core.MEDIA.create("wood.png"));

        icon.load(false);
        gold.load(false);
        wood.load(false);
    }
}
