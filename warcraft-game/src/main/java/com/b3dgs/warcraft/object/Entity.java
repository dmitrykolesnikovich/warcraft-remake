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

import java.util.Locale;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.ActionerModel;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerConfig;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListenerVoid;
import com.b3dgs.lionengine.game.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListenerVoid;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorChecker;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.warcraft.constant.Constant;
import com.b3dgs.warcraft.object.feature.EntitySfx;
import com.b3dgs.warcraft.object.feature.EntityStats;
import com.b3dgs.warcraft.object.feature.Warehouse;
import com.b3dgs.warcraft.object.state.StateIdle;
import com.b3dgs.warcraft.object.state.StateProducing;

/**
 * Entity representation base.
 */
public class Entity extends FeaturableModel
{
    private static final int PREFIX = State.class.getSimpleName().length();

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    public static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Entity(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(services, setup));
        addFeature(new MirrorableModel());
        addFeature(new TransformableModel(setup));
        addFeature(new SelectableModel());
        addFeature(new AnimatableModel());
        addFeature(new EntityStats(services, setup));
        addFeature(new EntitySfx(services, setup));
        addFeature(new ProducibleModel(setup));
        addFeature(new ActionerModel(setup));
        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));
        final Attacker attacker = addFeatureAndGet(new AttackerModel(setup));

        final MapTile map = services.get(MapTile.class);
        if (setup.hasNode(AttackerConfig.NODE_ATTACKER))
        {
            final Range range = AttackerConfig.imports(setup).getDistance();
            attacker.setAttackDistance(new Range(range.getMin() * map.getTileWidth(),
                                                 range.getMax() * map.getTileHeight()));
            attacker.addListener(new AttackerListenerVoid()
            {
                @Override
                public void notifyReachingTarget(Transformable target)
                {
                    if (!pathfindable.isMoving())
                    {
                        pathfindable.setDestination(target);
                    }
                }

                @Override
                public void notifyAttackStarted(Transformable target)
                {
                    pathfindable.stopMoves();
                }
            });
        }

        final Producer producer = addFeatureAndGet(new ProducerModel(services));
        producer.setStepsSpeed(1.0);
        producer.addListener(new ProducerListenerVoid()
        {
            @Override
            public void notifyStartProduction(Featurable featurable)
            {
                featurable.getFeature(StateHandler.class).changeState(StateProducing.class);
            }
        });

        final Handler handler = services.get(Handler.class);
        final ExtractorModel extractor = addFeatureAndGet(new ExtractorModel(services, setup));
        extractor.setChecker(new ExtractorChecker()
        {
            @Override
            public boolean canExtract()
            {
                return UtilMath.getDistance(pathfindable.getInTileX(),
                                            pathfindable.getInTileY(),
                                            extractor.getResourceLocation().getInTileX(),
                                            extractor.getResourceLocation().getInTileY()) < 2
                       && !pathfindable.isMoving();
            }

            @Override
            public boolean canCarry()
            {
                final Tiled warehouse = handler.get(Warehouse.class).iterator().next();
                return UtilMath.getDistance(pathfindable.getInTileX(),
                                            pathfindable.getInTileY(),
                                            warehouse.getInTileX(),
                                            warehouse.getInTileY()) < 2;
            }
        });

        final StateHandler stateHandler = addFeatureAndGet(new StateHandler(setup, Entity::getAnimationName));
        stateHandler.changeState(StateIdle.class);

        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setGroup(Integer.valueOf(Constant.LAYER_ENTITY));
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setCollisionVisibility(false);
        collidable.setOrigin(Origin.BOTTOM_LEFT);

        addFeature(new EntityModel(services, setup));
    }

    @Override
    public void addAfter(Services services, Setup setup)
    {
        addFeature(new Routines());

        final EntityModel model = getFeature(EntityModel.class);
        addFeature(new EntityUpdater(services));
        addFeature(new EntityRenderer(services, model));
    }
}
