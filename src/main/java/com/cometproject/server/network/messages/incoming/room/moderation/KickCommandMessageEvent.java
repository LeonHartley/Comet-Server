package com.cometproject.server.network.messages.incoming.room.moderation;

import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;

public class KickCommandMessageEvent implements IEvent {
    @Override
    public void handle(Session client, Event msg) throws Exception {
        int playerId = msg.readInt();

        if(!client.getPlayer().getPermissions().hasCommand("kick_command")) {
            return;
        }

        if(client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null && client.getPlayer().getEntity().getRoom().getEntities() != null) {
            PlayerEntity playerEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlayerId(playerId);

            if(playerEntity != null) {
                playerEntity.kick();
            }
        }
    }
}
