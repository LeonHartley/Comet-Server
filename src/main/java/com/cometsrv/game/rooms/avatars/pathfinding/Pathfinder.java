package com.cometsrv.game.rooms.avatars.pathfinding;

import com.cometsrv.game.rooms.avatars.misc.Position3D;
import com.cometsrv.game.rooms.entities.AvatarEntity;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public class Pathfinder
{
    /*
        @author Unknown
     */

    private Point[] points;

    public int goalX;
    public int goalY;
    private int mapX = 0;
    private int mapY = 0;

    protected AvatarEntity avatar;

    public Pathfinder(AvatarEntity avatar)
    {
        points = new Point[] {
                new Point(0, -1),
                new Point(0, 1),
                new Point(1, 0),
                new Point(-1, 0),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(1, 1),
                new Point(-1, -1)
        };

        mapX = avatar.getRoom().getModel().getSizeX();
        mapY = avatar.getRoom().getModel().getSizeY();

        this.avatar = avatar;
    }

    public LinkedList<Square> makePath()
    {
        LinkedList<Square> coordSquares = new LinkedList<>();

        int userX = avatar.getPosition().getX();
        int userY = avatar.getPosition().getY();

        goalX = avatar.getWalkingGoal().getX();
        goalY = avatar.getWalkingGoal().getY();

        Square lastCoord = new Square(-200, -200);
        int trys = 0;

        while(true)
        {
            trys++;
            int stepsToWalk = 10000;

            for (Point movePoint : points)
            {
                int newX = movePoint.x + userX;
                int newY = movePoint.y + userY;

                /*boolean lastStep = false;

                if(newX == goalX && newY == goalY)
                    lastStep = true;*/
                Position3D currentPos = new Position3D(userX, userY, 0);
                Position3D newPos = new Position3D(newX, newY, 0);

                if (avatar.getRoom().getMapping().isValidStep(currentPos, newPos)) //, true))
                {
                    Square pCoord = new Square(newX, newY);
                    pCoord.positionDistance = DistanceBetween(newX, newY, goalX, goalY);
                    pCoord.reversedPositionDistance = DistanceBetween(goalX, goalY, newX, newY);

                    if (stepsToWalk > pCoord.positionDistance)
                    {
                        stepsToWalk = pCoord.positionDistance;
                        lastCoord = pCoord;
                    }
                }
            }
            if (trys >= 200) {
                return null;
            } else if (lastCoord.x == -200 && lastCoord.y == -200) {
                return null;
            }

            userX = lastCoord.x;
            userY = lastCoord.y;
            coordSquares.add(lastCoord);

            if(userX == goalX && userY == goalY) {
                break;
            }
        }
        return coordSquares;
    }

    public void dispose() {
        Arrays.fill(points, null);
        avatar = null;
    }

    private int DistanceBetween(int currentX, int currentY, int goX, int goY)
    {
        return Math.abs(currentX - goX) + Math.abs(currentY - goY);
    }
}