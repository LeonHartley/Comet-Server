package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.items.types.floor.football.FootballGateFloorItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;

public class SaveFootballGateMessageEvent implements IEvent {

    @Override
    public void handle(Session client, Event msg) throws Exception {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().hasPermission("room_full_control")) {
            client.disconnect();
            return;
        }

        int itemId = msg.readInt();

        if(room.getItems().getFloorItem(itemId) == null || !(room.getItems().getFloorItem(itemId) instanceof FootballGateFloorItem)) return;

        FootballGateFloorItem floorItem = ((FootballGateFloorItem) room.getItems().getFloorItem(itemId));

        String gender = msg.readString().toUpperCase();
        String figure = msg.readString();

        floorItem.setFigure(gender.toUpperCase(), figure.split(";")[gender.equals("M") ? 0 : 1]);
        floorItem.saveFigures();
    }
}