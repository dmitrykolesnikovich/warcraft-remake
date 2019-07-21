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
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.CircuitsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuit;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

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

        map.addFeature(new MapTileViewerModel(services));

        services.get(Handler.class).add(map);
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
    }
}
