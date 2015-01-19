package com.mygdx.game.States;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Utility.TouchInfo;

import java.util.HashMap;


/**
 * Created by Elliot on 19/01/14.
 */
public abstract class State
{
    public State(StateID ID)
    {
        this.ID = targetID = ID;
    }

    public enum StateID
    {
        RESTART_STATE, //Neccesary to make the state machine do restarts
        MENU_STATE,
        EDITOR_STATE
    }

    // The unique ID of the state.
    protected StateID ID;
    // The unique ID of the desired state to switch to.
    private StateID targetID;

    // Abstract method intended to act as the main loop of the state.
    public abstract void Update(HashMap<Integer,TouchInfo> touches);
    // Abstract method intended to render all objects of the state.
    public abstract void Draw(SpriteBatch spriteBatch);
    //Abstract method that runs on state initialisation, for loading resources
    public abstract boolean Load();
    //Put dispose logic of anything in the state here to prevent memory leaks
    public abstract void Dispose();

    // Returns the targetID variable.
    public StateID GetTarget()
    {
        return targetID;
    }
    // Returns true if targetID differs from ID, otherwise returns false.
    public boolean Switch()
    {
        if(ID != targetID)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    // Sets the state targetID value to the param.
    protected void SwitchState(StateID state)
    {
        targetID = state;

        //This sets this ID to the restart state if you try and switch to this state, meaning that if you try and switch to this state, the state will reload.
        if(targetID == state)
        {
            this.ID = StateID.RESTART_STATE;
        }
    }

}
