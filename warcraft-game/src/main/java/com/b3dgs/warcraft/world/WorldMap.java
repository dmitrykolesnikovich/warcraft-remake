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
package com.b3dgs.warcraft.world;

import java.io.IOException;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Persistable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListener;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListenerVoid;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.CircuitsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuit;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FogOfWar;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.Fovable;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.warcraft.Player;
import com.b3dgs.warcraft.constant.Folder;
import com.b3dgs.warcraft.constant.Gfx;

/**
 * Handle world map data.
 */
public class WorldMap implements Persistable
{
    private final MapTile map;
    private final MapTileGroup mapGroup;
    private final MapTilePersister mapPersister;
    private final MapTilePath mapPath;
    private final MapTileTransition mapTransition;
    private final MapTileCircuit mapCircuit;
    private final FogOfWar fogOfWar;
    private final Handler handler;
    private final Player player;
    private final PathfindableListener listener = new PathfindableListenerVoid()
    {
        @Override
        public void notifyMoving(Pathfindable pathfindable)
        {
            fogOfWar.update(pathfindable.getFeature(Fovable.class));
            // final Layerable layerable = pathfindable.getFeature(Layerable.class);
            // layerable.setLayer(layerable.getLayerRefresh(), Integer.valueOf(pathfindable.getInTileY()));
        }
    };

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public WorldMap(Services services)
    {
        super();

        map = services.create(MapTileGame.class);
        mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));
        mapPath = services.add(map.addFeatureAndGet(new MapTilePathModel(services)));
        mapTransition = map.addFeatureAndGet(new MapTileTransitionModel(services));
        mapCircuit = map.addFeatureAndGet(new MapTileCircuitModel(services));
        fogOfWar = services.add(map.addFeatureAndGet(new FogOfWar()));
        player = services.get(Player.class);

        final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
        mapViewer.addRenderer(fogOfWar);

        final SpriteTiled hide = Drawable.loadSpriteTiled(Gfx.FOG_HIDDEN.getSurface(), 16, 16);
        final SpriteTiled fog = Drawable.loadSpriteTiled(Gfx.FOG_FOGGED.getSurface(), 16, 16);
        fogOfWar.setTilesheet(hide, fog);
        fogOfWar.setEnabled(true, false);

        handler = services.get(Handler.class);
        handler.add(map);
        handler.addListener(new HandlerListener()
        {
            @Override
            public void notifyHandlableAdded(Featurable featurable)
            {
                handleAdded(featurable);
            }

            @Override
            public void notifyHandlableRemoved(Featurable featurable)
            {
                handleRemoved(featurable);
            }
        });
    }

    /**
     * Handle added featurable.
     * 
     * @param featurable The added featurable.
     */
    private void handleAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Fovable.class) && player.owns(featurable))
        {
            featurable.getFeature(Pathfindable.class).addListener(listener);
            fogOfWar.update(featurable.getFeature(Fovable.class));
        }
    }

    /**
     * Handle removed featurable.
     * 
     * @param featurable The removed featurable.
     */
    private void handleRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Fovable.class) && player.owns(featurable))
        {
            featurable.getFeature(Pathfindable.class).removeListener(listener);
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        map.getFeature(MapTilePersister.class).save(file);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        mapPersister.load(file);

        final String parent = map.getMedia().getParentPath();
        mapGroup.loadGroups(Medias.create(parent, TileGroupsConfig.FILENAME));
        mapPath.loadPathfinding(Medias.create(parent, PathfindingConfig.FILENAME));
        mapTransition.loadTransitions(Medias.create(parent, TransitionsConfig.FILENAME));
        mapCircuit.loadCircuits(Medias.create(parent, CircuitsConfig.FILENAME));
        fogOfWar.create(map, Medias.create(Folder.FOG, "fog.xml"));
    }
}
