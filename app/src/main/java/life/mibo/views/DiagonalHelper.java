/*
 *  Created by Sumeet Kumar on 2/15/20 10:30 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/15/20 10:30 AM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.graphics.Path;

import static life.mibo.views.DiagonalHelper.Gravity.LEFT;
import static life.mibo.views.DiagonalHelper.Gravity.RIGHT;
import static life.mibo.views.DiagonalHelper.Gravity.TOP;
import static life.mibo.views.DiagonalHelper.Gravity.BOTTOM;

public class DiagonalHelper {

    public enum Gravity {
        LEFT(0),
        RIGHT(1),
        TOP(2),
        BOTTOM(3);
        final int value;

        Gravity(int value) {
            this.value = value;
        }
    }

    static final Gravity[] GRAVITIES = {Gravity.LEFT, RIGHT, Gravity.TOP, Gravity.BOTTOM};

    public enum Direction {
        LEFT_TO_RIGHT(0),
        RIGHT_TO_LEFT(1),
        TOP_TO_BOTTOM(2),
        BOTTOM_TO_TOP(3);
        final int value;

        Direction(int value) {
            this.value = value;
        }
    }

    static final Direction[] DIRECTIONS = {Direction.LEFT_TO_RIGHT, Direction.RIGHT_TO_LEFT, Direction.TOP_TO_BOTTOM, Direction.BOTTOM_TO_TOP};

    public static Path getPathFor(float width, float height, double angle, Direction direction, Gravity gravity) {
        switch (direction) {
            case LEFT_TO_RIGHT:
                return leftToRight(width, height, angle, gravity);
            case RIGHT_TO_LEFT:
                return rightToLeft(width, height, angle, gravity);
            case TOP_TO_BOTTOM:
                return topToBottom(width, height, angle, gravity);
            case BOTTOM_TO_TOP:
                return bottomToTop(width, height, angle, gravity);
        }
        return null;
    }


    private static Path leftToRight(float width, float height, double angle, Gravity gravity) {
        if (gravity == Gravity.LEFT || gravity == RIGHT)
            gravity = Gravity.BOTTOM;
        Path path = new Path();
        float attitude = (float) Math.abs(Math.tan(Math.toRadians(angle)) * width);
        if (gravity == Gravity.BOTTOM) {
            path.moveTo(0, 0);
            path.lineTo(width, 0);
            path.lineTo(width, height - attitude);
            path.lineTo(0, height);
            path.close();
        } else {
            path.moveTo(0, 0);
            path.lineTo(width, attitude);
            path.lineTo(width, height);
            path.lineTo(0, height);
            path.close();
        }
        return path;
    }

    private static Path rightToLeft(float width, float height, double angle, Gravity gravity) {
        if (gravity == Gravity.LEFT || gravity == RIGHT)
            gravity = BOTTOM;
        Path path = new Path();
        float attitude = (float) Math.abs(Math.tan(Math.toRadians(angle)) * width);
        if (gravity == BOTTOM) {
            path.moveTo(0, 0);
            path.lineTo(width, 0);
            path.lineTo(width, height);
            path.lineTo(0, height - attitude);
            path.close();
        } else {
            path.moveTo(0, attitude);
            path.lineTo(width, 0);
            path.lineTo(width, height);
            path.lineTo(0, height);
            path.close();
        }
        return path;
    }

    private static Path topToBottom(float width, float height, double angle, Gravity gravity) {
        if (gravity == TOP || gravity == BOTTOM)
            gravity = LEFT;
        float attitude = (float) Math.abs(Math.tan(Math.toRadians(angle)) * height);
        Path path = new Path();
        if (gravity == LEFT) {
            path.moveTo(0, 0);
            path.lineTo(0, height);
            path.lineTo(width - attitude, height);
            path.lineTo(width, 0);
            path.close();
        } else {
            path.moveTo(0, 0);
            path.lineTo(attitude, height);
            path.lineTo(width, height);
            path.lineTo(width, 0);
            path.close();
        }
        return path;
    }

    private static Path bottomToTop(float width, float height, double angle, Gravity gravity) {
        if (gravity == TOP || gravity == BOTTOM)
            gravity = LEFT;

        float attitude = (float) Math.abs(Math.tan(Math.toRadians(angle)) * height);
        Path path = new Path();
        if (gravity == LEFT) {
            path.moveTo(0, 0);
            path.lineTo(0, height);
            path.lineTo(width, height);
            path.lineTo(width - attitude, 0);
            path.close();
        } else {
            path.moveTo(attitude, 0);
            path.lineTo(width, 0);
            path.lineTo(width, height);
            path.lineTo(0, height);
            path.close();
        }
        return path;
    }
}