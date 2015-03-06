package com.cometproject.server.network.messages.outgoing.room.avatar;

import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;


public class WisperMessageComposer extends MessageComposer {
    private final int playerId;
    private final String message;
    private final int bubbleId;

    public WisperMessageComposer(int playerId, String message, int bubbleId) {
        this.playerId = playerId;
        this.message = message;
        this.bubbleId = bubbleId;
    }

    public WisperMessageComposer(int playerId, String message) {
        this(playerId, message, 0);
    }

    @Override
    public short getId() {
        return Composers.WhisperMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        msg.writeInt(playerId);
        msg.writeString(message);
        msg.writeInt(0);
        msg.writeInt(bubbleId);
        msg.writeInt(0);
        msg.writeInt(-1);
    }
}
