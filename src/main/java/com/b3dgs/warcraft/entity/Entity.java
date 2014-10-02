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
package com.b3dgs.warcraft.entity;

import java.util.Collection;
import java.util.Locale;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.strategy.ability.skilled.SkilledModel;
import com.b3dgs.lionengine.game.strategy.ability.skilled.SkilledServices;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.RaceType;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.skill.FactorySkill;
import com.b3dgs.warcraft.skill.Skill;

/**
 * Abstract entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Entity
        extends EntityStrategy
        implements SkilledServices<Skill>
{
    /**
     * Get an entity configuration file.
     * 
     * @param race The race type.
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(RaceType race, Class<? extends Entity> type)
    {
        return Core.MEDIA.create(AppWarcraft.ENTITIES_DIR, race.name().toLowerCase(Locale.ENGLISH),
                type.getSimpleName() + "." + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /** Entity life. */
    private final Alterable life;
    /** Entity name. */
    private final String name;
    /** Entity icon number. */
    private final Sprite icon;
    /** Skilled model. */
    private final SkilledModel<Skill> skilled;
    /** Map reference. */
    protected Map map;
    /** Factory skill. */
    private FactorySkill factorySkill;
    /** Dead flag. */
    private boolean dead;
    /** Player owner (null if none). */
    private Player owner;
    /** Creation progress. */
    private int progress;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Entity(SetupEntity setup)
    {
        super(setup);
        skilled = new SkilledModel<>();
        final Configurer configurer = setup.getConfigurer();
        life = new Alterable(configurer.getInteger("life", "attributes"));
        setFov(configurer.getInteger("fov", "attributes"));
        name = configurer.getString("name");
        final ConfigSurface surfaceData = ConfigSurface.create(configurer);
        icon = Drawable.loadSprite(Core.MEDIA.create(AppWarcraft.ENTITIES_DIR, setup.race.getPath(),
                surfaceData.getIcon()));
        icon.load(false);
        dead = false;
        owner = null;
        life.fill();
        progress = 100;
    }

    /**
     * Add a skill.
     * 
     * @param panel The skill panel.
     * @param media The skill media.
     * @param priority The position number.
     */
    public void addSkill(int panel, Media media, int priority)
    {
        final Skill skill = factorySkill.create(media);
        skill.setOwner(this);
        skill.setPriority(priority);
        skill.prepare();
        addSkill(skill, panel);
    }

    /**
     * Kill the entity.
     */
    public void kill()
    {
        decreaseLife(life.getCurrent());
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }

    /**
     * Get the life percent.
     * 
     * @return The life percent.
     */
    public int getLifePercent()
    {
        return life.getPercent();
    }

    /**
     * Decrease life value.
     * 
     * @param damages The damages.
     */
    public void decreaseLife(int damages)
    {
        decreaseLife(damages, null);
    }

    /**
     * Decrease life value.
     * 
     * @param damages The damages.
     * @param attacker The attacker reference.
     */
    public void decreaseLife(int damages, Attacker attacker)
    {
        life.decrease(damages);
        if (life.isEmpty())
        {
            dead = true;
            setSelectable(false);
            setActive(false);
            setAlive(false);
            stop();
            removeRef();
            final Player player = getPlayer();
            if (player != null)
            {
                player.changePopulation(-1);
            }
        }
    }

    /**
     * Get the entity name.
     * 
     * @return The entity name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the entity icon number.
     * 
     * @return The icon number.
     */
    public Sprite getIcon()
    {
        return icon;
    }

    /**
     * Get the owner reference.
     * 
     * @return The owner reference.
     */
    public Player getPlayer()
    {
        return owner;
    }

    /**
     * Set the owner reference.
     * 
     * @param player The player owner reference.
     */
    public void setPlayer(Player player)
    {
        owner = player;
        setPlayerId(player.id);
    }

    /**
     * Get the dead status.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public boolean isDead()
    {
        return dead;
    }

    /**
     * Get the construction progress in percent.
     * 
     * @return The construction progress in percent.
     */
    public int getProgressPercent()
    {
        return progress;
    }

    /**
     * Set the current progress in percent.
     * 
     * @param progress The progress value in percent.
     */
    void setProgressPercent(int progress)
    {
        this.progress = progress;
    }

    /*
     * EntityStrategy
     */

    @Override
    protected void prepareEntity(ContextGame context)
    {
        map = context.getService(Map.class);
        factorySkill = context.getService(FactorySkill.class);
    }

    /*
     * SkilledServices
     */

    @Override
    public void addSkill(Skill skill, int panel)
    {
        skilled.addSkill(skill, panel);
    }

    @Override
    public <S extends Skill> S getSkill(int panel, Class<S> id)
    {
        return skilled.getSkill(panel, id);
    }

    @Override
    public void removeSkill(int panel, Class<? extends Skill> id)
    {
        skilled.removeSkill(panel, id);
    }

    @Override
    public Collection<Skill> getSkills(int panel)
    {
        return skilled.getSkills(panel);
    }

    @Override
    public Collection<Skill> getSkills()
    {
        return skilled.getSkills();
    }

    @Override
    public void setSkillPanel(int currentSkillPanel)
    {
        skilled.setSkillPanel(currentSkillPanel);
    }

    @Override
    public void setSkillPanelNext(int nextSkillPanel)
    {
        skilled.setSkillPanelNext(nextSkillPanel);
    }

    @Override
    public int getSkillPanel()
    {
        return skilled.getSkillPanel();
    }
}
