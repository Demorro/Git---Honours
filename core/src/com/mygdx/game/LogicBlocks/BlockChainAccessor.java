package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by Elliot Morris on 22/01/2015.
 */
public class BlockChainAccessor implements TweenAccessor<BlockChain> {

    public static final int OPACITY = 1;

    @Override
    public int getValues(BlockChain target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case OPACITY: returnValues[0] = target.GetOpacity(); return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(BlockChain target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case OPACITY: target.SetOpacity(newValues[0]); break;
            default: assert false; break;
        }
    }
}
