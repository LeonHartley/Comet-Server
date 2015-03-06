package com.cometproject.server.network.messages.outgoing.room.items;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;


public class FloorItemsMessageComposer extends MessageComposer {
    private final Room room;

    public FloorItemsMessageComposer(final Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.RoomFloorItemsMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        if (room.getItems().getFloorItems().size() > 0) {
            msg.writeInt(1);
            msg.writeInt(room.getData().getOwnerId());
            msg.writeString(room.getData().getOwner());
            msg.writeInt(room.getItems().getFloorItems().size());

            for (RoomItemFloor item : room.getItems().getFloorItems()) {
                item.serialize(msg);
            }
        } else {
            msg.writeInt(0);
            msg.writeInt(0);
        }

    }
}
