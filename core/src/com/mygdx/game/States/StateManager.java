package com.mygdx.game.States;



import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Utility.TouchInfo;

import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class StateManager
{
    private State curState;

    public StateManager()
    {
        curState = null;
        //Define what state we start with.
        SwitchState(State.StateID.EDITOR_STATE);
        curState.RunOnGameOpen();
    }

    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        if(curState != null)
        {
            // Switch the state if a signal has been given from the current state
            if(curState.Switch())
            {
                SwitchState(curState.GetTarget());
            }

            //Run the update loop for the current state
            curState.Update(touches);
        }
    }

    public void Draw(SpriteBatch spriteBatch)
    {
        if(curState != null)
        {
            curState.Draw(spriteBatch);
        }

    }

    //Automatically detects if the state needs to be switched and then handles the switching.
   private void SwitchState(State.StateID stateID)
    {
        // Release previous state, if set
        if(curState != null)
        {
            curState.Dispose();
            curState = null;
        }

        System.out.println("Switched to: ");

        switch(stateID)
        {
            case MENU_STATE:
                System.out.println("MENU");
                curState = new MenuState();
                break;
            case EDITOR_STATE:
                System.out.println("EDITOR");
                curState = new EditorState();
                break;
            case PLAY_STATE:
                System.out.println("PLAY");
                curState = new PlayState();
                break;
        }

        if(curState.Load() == false)
        {
            System.out.println("Failed to load state with ID : ");
        }
    }
}

