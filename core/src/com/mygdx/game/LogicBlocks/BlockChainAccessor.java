package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Elliot Morris on 22/01/2015.
 */
public class BlockChainAccessor implements TweenAccessor<BlockChain> {

    public static final int OPACITY = 1;
    public static final int POSITION_X = 2;
    public static final int POSITION_Y = 3;
    public static final int POSITION_XY = 4;

    @Override
    public int getValues(BlockChain target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case OPACITY: returnValues[0] = target.GetOpacity(); return 1;
            case POSITION_X: returnValues[0] = target.GetX(); return 1;
            case POSITION_Y: returnValues[0] = target.GetY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.GetX();
                returnValues[1] = target.GetY();
                return 2;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(BlockChain target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case OPACITY: target.SetOpacity(newValues[0]); break;
            case POSITION_X: target.SetPosition(newValues[0], target.GetY()); break;
            case POSITION_Y: target.SetPosition(target.GetX(), newValues[0]); break;
            case POSITION_XY:
                target.SetPosition(newValues[0], newValues[1]);
                break;
            default: assert false; break;
        }
    }
}
