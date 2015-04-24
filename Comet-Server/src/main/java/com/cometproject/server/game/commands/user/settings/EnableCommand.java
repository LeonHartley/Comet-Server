package com.cometproject.server.game.commands.user.settings;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.RoomInstance;
import com.cometproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.cometproject.server.network.sessions.Session;

public class EnableCommand extends ChatCommand{
    @Override
    public void execute(Session client, String[] params) {
        RoomInstance room = client.getPlayer().getEntity().getRoom();

        if ((room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().hasPermission("room_full_control"))) {
            return;
        }

        if(params.length != 1) {
            return;
        }

        String disabledCommand = params[0];

        if(room.getData().getDisabledCommands().contains(disabledCommand)) {
            room.getData().getDisabledCommands().remove(disabledCommand);
            room.getData().save();

            sendNotif(Locale.get("command.enablecommand.success"), client);
        } else {
            client.send(new AdvancedAlertMessageComposer(Locale.get("command.enablecommand.error")));
        }
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
