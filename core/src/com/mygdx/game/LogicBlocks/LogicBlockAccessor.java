package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Elliot Morris on 17/01/2015.
 */
public class LogicBlockAccessor implements TweenAccessor<LogicBlock> {

    // The following lines define the different possible tween types.
    // It's up to you to define what you need :-)

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;
    public static final int OPACITY = 4;
    public static final int OPACITYNOTEXT = 5;
    public static final int OPACITYANDTEXT = 6;
    public static final int SCALEX = 7;
    public static final int SCALEY = 8;
    public static final int SCALEXY = 9;

    // TweenAccessor implementation

    @Override
    public int getValues(LogicBlock target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.GetX(); return 1;
            case POSITION_Y: returnValues[0] = target.GetY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.GetX();
                returnValues[1] = target.GetY();
                return 2;
            case OPACITY: returnValues[0] = target.GetOpacity(); return 1;
            case OPACITYNOTEXT: returnValues[0] = target.GetOpacity(); return 1;
            case OPACITYANDTEXT:
                returnValues[0] = target.GetOpacity();
                returnValues[1] = target.GetTextOpacity();
                return 2;
            case SCALEX: returnValues[0] = target.GetScaleX(); return 1;
            case SCALEY: returnValues[0] = target.GetScaleY(); return 1;
            case SCALEXY:
                returnValues[0] = target.GetScaleX();
                returnValues[1] = target.GetScaleY();
                return 2;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(LogicBlock target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: target.SetX(newValues[0]); break;
            case POSITION_Y: target.SetY(newValues[0]); break;
            case POSITION_XY:
                target.SetX(newValues[0]);
                target.SetY(newValues[1]);
                break;
            case OPACITY: target.SetOpacity(newValues[0]); break;
            case OPACITYNOTEXT: target.SetOpacityWithoutChangingText(newValues[0]); break;
            case OPACITYANDTEXT:
                target.SetOpacity(newValues[0]);
                target.SetTextOpacity(newValues[1]);
                break;
            case SCALEX: target.SetScaleX(newValues[0]); break;
            case SCALEY: target.SetScaleY(newValues[0]); break;
            case SCALEXY:
                target.SetScaleXY(newValues[0]);
                break;
            default: assert false; break;
        }
    }
}
