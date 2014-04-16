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

import com.b3dgs.warcraft.RaceOrc;
import com.b3dgs.warcraft.entity.SetupEntity;
import com.b3dgs.warcraft.entity.UnitWorker;
import com.b3dgs.warcraft.skill.orc.BuildBarracksOrc;
import com.b3dgs.warcraft.skill.orc.BuildFarmOrc;
import com.b3dgs.warcraft.skill.orc.BuildingStandardOrc;
import com.b3dgs.warcraft.skill.orc.CancelOrc;
import com.b3dgs.warcraft.skill.orc.MoveOrc;
import com.b3dgs.warcraft.skill.orc.StopOrc;

/**
 * Peon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Peon
        extends UnitWorker
        implements RaceOrc
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupEntity setup)
    {
        super(setup);
        addSkill(0, MoveOrc.class, 0);
        addSkill(0, StopOrc.class, 1);
        addSkill(0, BuildingStandardOrc.class, 2);
        addSkill(1, BuildFarmOrc.class, 0);
        addSkill(1, BuildBarracksOrc.class, 1);
        addSkill(1, CancelOrc.class, 2);
    }
}
