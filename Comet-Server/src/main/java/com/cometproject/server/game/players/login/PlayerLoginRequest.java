package com.cometproject.server.game.players.login;

import com.cometproject.api.events.players.OnPlayerLoginEvent;
import com.cometproject.server.config.CometSettings;
import com.cometproject.server.game.achievements.types.AchievementType;
import com.cometproject.server.game.moderation.BanManager;
import com.cometproject.server.game.moderation.types.BanType;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.modules.ModuleManager;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.handshake.SSOTicketMessageEvent;
import com.cometproject.server.network.messages.outgoing.handshake.AuthenticationOKMessageComposer;
import com.cometproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.cometproject.server.network.messages.outgoing.handshake.UniqueIDMessageComposer;
import com.cometproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import com.cometproject.server.network.messages.outgoing.navigator.FavouriteRoomsMessageComposer;
import com.cometproject.server.network.messages.outgoing.notification.MotdNotificationComposer;
import com.cometproject.server.network.messages.outgoing.user.details.EnableNotificationsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.EnableTradingMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.PlayerSettingsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.details.UnreadMinimailsMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.EffectsInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.permissions.FuserightsMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.player.PlayerAccessDao;
import com.cometproject.server.storage.queries.player.PlayerDao;
import com.cometproject.server.tasks.CometTask;

import java.util.concurrent.Callable;

public class PlayerLoginRequest implements CometTask {

    private final Session client;
    private final String ticket;

    public PlayerLoginRequest(Session client, String ticket) {
        this.client = client;
        this.ticket = ticket;
    }

    @Override
    public void run() {

        // TODO: Tell the hotel owners to remove the id:ticket stuff
        Player player = null;
        boolean normalPlayerLoad = false;

        if (ticket.contains(SSOTicketMessageEvent.TICKET_DELIMITER)) {
            String[] ticketData = ticket.split(SSOTicketMessageEvent.TICKET_DELIMITER);

            if (ticketData.length == 2) {
                String authTicket = ticketData[1];

                player = PlayerDao.getPlayer(authTicket);
            } else {
                normalPlayerLoad = true;
            }
        } else {
            normalPlayerLoad = true;
        }

        if (normalPlayerLoad) {
            player = PlayerDao.getPlayer(ticket);
        }

        if (player == null) {
            player = PlayerDao.getPlayerFallback(ticket);

            if (player == null) {
                client.disconnect(false);
                return;
            }
        }

        Session cloneSession = NetworkManager.getInstance().getSessions().getByPlayerId(player.getId());

        if (cloneSession != null) {
            cloneSession.disconnect(true);
        }

        if (BanManager.getInstance().hasBan(Integer.toString(player.getId()), BanType.USER)) {
            client.getLogger().warn("Banned player: " + player.getId() + " tried logging in");
            client.disconnect("banned");
            return;
        }

        player.setSession(client);
        client.setPlayer(player);

        String ipAddress = client.getIpAddress();

        if (ipAddress != null && !ipAddress.isEmpty()) {
            if (BanManager.getInstance().hasBan(ipAddress, BanType.IP)) {
                client.getLogger().warn("Banned player: " + player.getId() + " tried logging in");
                client.disconnect("banned");
                return;
            }

            client.getPlayer().getData().setIpAddress(ipAddress);
        }

        if (CometSettings.storeAccess)
            PlayerAccessDao.saveAccess(player.getId(), client.getUniqueId(), ipAddress);

        RoomManager.getInstance().loadRoomsForUser(player);

        client.getLogger().debug(client.getPlayer().getData().getUsername() + " logged in");

        PlayerDao.updatePlayerStatus(player, true, true);

        client.sendQueue(new UniqueIDMessageComposer(client.getUniqueId())).
                sendQueue(new AuthenticationOKMessageComposer()).
                sendQueue(new FuserightsMessageComposer(client.getPlayer().getSubscription().exists(), client.getPlayer().getData().getRank())).
                sendQueue(new MotdNotificationComposer()).
                sendQueue(new FavouriteRoomsMessageComposer()).
                sendQueue(new UnreadMinimailsMessageComposer()).
                sendQueue(new EnableTradingMessageComposer(true)).
                sendQueue(new EnableNotificationsMessageComposer()).
                sendQueue(new PlayerSettingsMessageComposer(player.getSettings())).
                sendQueue(new HomeRoomMessageComposer(player.getSettings().getHomeRoom())).
                sendQueue(new EffectsInventoryMessageComposer());

        if (client.getPlayer().getPermissions().hasPermission("mod_tool")) {
            client.sendQueue(new ModToolMessageComposer());
        }

        client.flush();

        client.getPlayer().getAchievements().progressAchievement(AchievementType.LOGIN, 1);
        ModuleManager.getInstance().getEventHandler().handleEvent(new OnPlayerLoginEvent(client.getPlayer()));
    }
}