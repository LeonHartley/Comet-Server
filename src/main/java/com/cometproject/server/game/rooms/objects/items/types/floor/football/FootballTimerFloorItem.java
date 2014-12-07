package com.cometproject.server.game.rooms.objects.items.types.floor.football;

import com.cometproject.server.game.rooms.objects.entities.GenericEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.types.Room;

public class FootballTimerFloorItem extends RoomItemFloor {
    private int time = 0;

    public FootballTimerFloorItem(int id, int itemId, Room room, int owner, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, x, y, z, rotation, data);
    }


    @Override
    public void onInteract(GenericEntity entity, int requestData, boolean isWiredTriggered) {
        if(requestData == 1) {
            int time = Integer.parseInt(this.getExtraData());

            if (time == 0 || time == 30 || time == 60 || time == 120 || time == 180 || time == 300 || time == 600) {
                switch (time) {
                    default:
                        time = 0;
                        break;
                    case 0:
                        time = 30;
                        break;
                    case 30:
                        time = 60;
                        break;
                    case 60:
                        time = 120;
                        break;
                    case 120:
                        time = 180;
                        break;
                    case 180:
                        time = 300;
                        break;
                    case 300:
                        time = 600;
                        break;
                }
            } else {
                time = 0;
            }

            this.time = time;
            this.setExtraData(this.time + "");
            this.sendUpdate();
        } else {
            // Tell the room we have an active football game.
            this.getRoom().setAttribute("football", true);

            for(RoomItemFloor scoreItem : this.getRoom().getItems().getByInteraction("football_score")) {
                ((FootballScoreFloorItem) scoreItem).reset();
            }

            this.setTicks(RoomItemFactory.getProcessTime(1.0));
        }
    }

    @Override
    public void onTickComplete() {
        if(this.time > 0) {
            this.time--;

            this.setExtraData(this.time + "");
            this.sendUpdate();

            this.setTicks(RoomItemFactory.getProcessTime(1.0));
        } else {
            if(this.getRoom().hasAttribute("football")) {
                // football game has ended.
                this.getRoom().removeAttribute("football");
            }
        }
    }
}