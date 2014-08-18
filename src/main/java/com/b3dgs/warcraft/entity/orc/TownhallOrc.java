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
package com.b3dgs.warcraft.entity.orc;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.warcraft.RaceType;
import com.b3dgs.warcraft.entity.BuildingProducer;
import com.b3dgs.warcraft.entity.Entity;
import com.b3dgs.warcraft.entity.SetupEntity;
import com.b3dgs.warcraft.entity.Warehouse;
import com.b3dgs.warcraft.skill.orc.ProducePeon;

/**
 * TownHall building implementation. This building allows to create new peon and store resources.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TownhallOrc
        extends BuildingProducer
        implements Warehouse
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(RaceType.ORC, TownhallOrc.class);

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public TownhallOrc(SetupEntity setup)
    {
        super(setup);
    }

    @Override
    public void prepareEntity(ContextGame context)
    {
        super.prepareEntity(context);
        addSkill(0, ProducePeon.MEDIA, 0);
    }
}
