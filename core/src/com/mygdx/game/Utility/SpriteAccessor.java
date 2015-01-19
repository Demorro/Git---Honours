package com.mygdx.game.Utility;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.LogicBlocks.LogicBlock;

/**
 * Created by Elliot Morris on 17/01/2015.
 */

//SpriteAccessor for tweening
public class SpriteAccessor implements TweenAccessor<Sprite> {

    // The following lines define the different possible tween types.
    // It's up to you to define what you need :-)

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int OPACITY = 4;
    public static final int SCALEX = 7;
    public static final int SCALEY = 8;
    public static final int SCALEXY = 9;

    // TweenAccessor implementation

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getX(); return 1;
            case POSITION_Y: returnValues[0] = target.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case OPACITY: returnValues[0] = target.getColor().a; return 1;
            case SCALEX: returnValues[0] = target.getScaleX(); return 1;
            case SCALEY: returnValues[0] = target.getScaleY(); return 1;
            case SCALEXY:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: target.setX(newValues[0]); break;
            case POSITION_Y: target.setY(newValues[0]); break;
            case POSITION_XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            case OPACITY: target.setAlpha(newValues[0]); break;
            case SCALEX: target.setScale(newValues[0], target.getScaleY()); break;
            case SCALEY: target.setScale(target.getScaleX(), newValues[0]); break;
            case SCALEXY:
                target.setScale(newValues[0]);
                break;
            default: assert false; break;
        }
    }
}
