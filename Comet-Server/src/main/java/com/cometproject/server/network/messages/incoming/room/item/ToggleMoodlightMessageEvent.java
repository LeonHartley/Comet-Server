package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.cometproject.server.game.rooms.types.RoomInstance;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.types.MessageEvent;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.items.MoodlightDao;


public class ToggleMoodlightMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        RoomInstance room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().hasPermission("room_full_control")) {
            client.disconnect();
            return;
        }

        MoodlightWallItem moodlight = room.getItems().getMoodlight();
        if (moodlight == null) {
            return;
        }

        if (!moodlight.getMoodlightData().isEnabled()) {
            moodlight.getMoodlightData().setEnabled(true);
        } else {
            moodlight.getMoodlightData().setEnabled(false);
        }

        // save the data!
        MoodlightDao.updateMoodlight(moodlight);

        // set the mood!
        moodlight.setExtraData(moodlight.generateExtraData());
        moodlight.saveData();
        moodlight.sendUpdate();
    }
}