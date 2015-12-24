package com.cometproject.server.game.rooms.objects.entities.types.ai;

import com.cometproject.server.game.rooms.objects.entities.GenericEntity;
import com.cometproject.server.game.rooms.objects.entities.types.BotEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;

public abstract class AbstractBotAI implements BotAI {
    private GenericEntity entity;

    public AbstractBotAI(GenericEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean onTalk(PlayerEntity entity, String message) {
        return false;
    }

    @Override
    public boolean onPlayerLeave(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onPlayerEnter(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onAddedToRoom() {
        return false;
    }

    @Override
    public boolean onRemovedFromRoom() {
        return false;
    }

    public GenericEntity getEntity() {
        return entity;
    }

    public BotEntity getBotEntity() {
        return (BotEntity) entity;
    }
}
