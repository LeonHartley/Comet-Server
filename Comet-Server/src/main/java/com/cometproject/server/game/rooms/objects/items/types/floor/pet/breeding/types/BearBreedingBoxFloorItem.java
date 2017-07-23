package com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.types;

import com.cometproject.server.game.pets.races.PetType;
import com.cometproject.server.game.rooms.objects.items.types.floor.pet.breeding.BreedingBoxFloorItem;
import com.cometproject.server.game.rooms.types.Room;

public class BearBreedingBoxFloorItem extends BreedingBoxFloorItem {
    public BearBreedingBoxFloorItem(long id, int itemId, Room room, int owner, String ownerName, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, ownerName, x, y, z, rotation, data);
    }

    @Override
    protected int getBabyType() {
        return PetType.BABYBEAR;
    }

    @Override
    protected int getPetType() {
        return PetType.BEAR;
    }
}