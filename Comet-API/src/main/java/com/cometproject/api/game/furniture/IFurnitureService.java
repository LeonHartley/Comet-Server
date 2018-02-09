package com.cometproject.api.game.furniture;

import com.cometproject.api.game.furniture.types.FurnitureDefinition;
import com.cometproject.api.game.furniture.types.IMusicData;
import com.cometproject.api.utilities.Initialisable;
import org.apache.log4j.Logger;

import java.util.Map;

public interface IFurnitureService extends Initialisable {
    void loadItemDefinitions();

    void loadMusicData();

    int getItemVirtualId(long itemId);

    void disposeItemVirtualId(long itemId);

    Long getItemIdByVirtualId(int virtualId);

    long getTeleportPartner(long itemId);

    int roomIdByItemId(long itemId);

    FurnitureDefinition getDefinition(int itemId);

    IMusicData getMusicData(int songId);

    IMusicData getMusicDataByName(String name);

    Map<Long, Integer> getItemIdToVirtualIds();

    FurnitureDefinition getBySpriteId(int spriteId);

    Logger getLogger();

    Map<Integer, FurnitureDefinition> getItemDefinitions();

    Integer getSaddleId();
}
