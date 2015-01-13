package com.cometproject.server.network.clients;

import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

import java.io.IOException;


@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelUpstreamHandler {
    private static Logger log = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler() {
        log.info("Instantiated");
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, MessageEvent ev) throws Exception {
        try {
            Session client = (Session) ctx.getChannel().getAttachment();

            if (client != null && ev.getMessage() instanceof Event) {
                NetworkManager.getInstance().getMessages().handle((Event) ev.getMessage(), client);
            }
        } catch (Exception e) {
            log.error("Error while receiving message", e);
        }
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent ev) throws Exception {
        if (!NetworkManager.getInstance().getSessions().add(ctx.getChannel())) {
            ctx.getChannel().disconnect();
            return;
        }

        super.channelOpen(ctx, ev);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, ChannelStateEvent ev) throws Exception {
        try {
            Session session = (Session) ctx.getChannel().getAttachment();
            session.onDisconnect();
        } catch (Exception e) {
        }

        NetworkManager.getInstance().getSessions().remove(ctx.getChannel());

        super.channelClosed(ctx, ev);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, ExceptionEvent ev) throws Exception {
        if (ctx.getChannel().isConnected()) {
            if (ev.getCause() instanceof IOException) {
                ctx.getChannel().disconnect();
                return;
            }

            log.error("Exception in ClientHandler: " + ev.getCause().getMessage());

            ev.getCause().printStackTrace();
            ctx.getChannel().close();
        }
    }
}