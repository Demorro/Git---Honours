package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.GameObjects.Weapons.Target;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.Utility.ScriptSaver;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 28/01/2015.
 */
public class PlayerShip extends Ship{


    private Texture gameObjectTexSheet; //Reference to game object texture sheet

    private ArrayList<ArrayList<LogicGroups.LogicBlockType>> playerAI = new ArrayList<ArrayList<LogicGroups.LogicBlockType>>();

    private ArrayList<EnemyCapitalShip> caps;
    private ArrayList<EnemyFrigateShip> frigs;
    private ArrayList<EnemyFighterShip> fighters;

    //Choosing what to attack with each gun is done on a random chance sort of basis, and the speed determines the weighting of this
    private int QuickAttackChanceWeight = 3;
    private int ModerateAttackChanceWeight = 2;
    private int SlowAttackChanceWeight = 1;

    public PlayerShip(Texture gameObjectTexSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters)
    {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet,0,0,100,76), 100);
        this.gameObjectTexSheet = gameObjectTexSheet;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1000, 1, new Rectangle(0,750,18,50), GetCenterPosition(), 12);

        this.caps = caps;
        this.frigs = frigs;
        this.fighters = fighters;

        ParseLogicScript();

        autoCannon.SetIsAutoFiring(true);
    }

    public void Update(float elapsed, OrthographicCamera camera)
    {
        super.Update(elapsed);
        ResolveAutoCannonAttackTargets(caps, frigs, fighters, camera);
    }

    //Takes the list of targets that the logic script has loaded in, and resolves what should be targetted and how much
    public void ResolveAutoCannonAttackTargets(ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, OrthographicCamera camera)
    {
        if(attackTargets.size() > 0)
        {
            GameObject closestCap = GetClosestObject(caps, camera);
            GameObject closestFrig = GetClosestObject(frigs, camera);
            GameObject closestFighter = GetClosestObject(fighters, camera);

            int autoCannonCapAttackChance = 0; int autoCannonFrigAttackChance = 0; int autoCannonFighterAttackChance = 0;

            //Get chance to attack specific target
            for(Target target : attackTargets){
                if(target.target == Utility.Target.CAPITAL){
                    if(target.firingWeapon == Utility.Weapon.AUTOCANNON){
                        if(target.firingSpeed == Utility.Speed.QUICK){autoCannonCapAttackChance += QuickAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.QUICK);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){autoCannonCapAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){autoCannonCapAttackChance += SlowAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.SLOW);}
                        else{ autoCannonCapAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                    }
                }
                else if(target.target == Utility.Target.FRIGATE){
                    if(target.firingWeapon == Utility.Weapon.AUTOCANNON){
                        if(target.firingSpeed == Utility.Speed.QUICK){autoCannonFrigAttackChance += QuickAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.QUICK);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){autoCannonFrigAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){autoCannonFrigAttackChance += SlowAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.SLOW);}
                        else{ autoCannonCapAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                    }
                }
                else if(target.target == Utility.Target.FIGHTER){
                    if(target.firingWeapon == Utility.Weapon.AUTOCANNON){
                        if(target.firingSpeed == Utility.Speed.QUICK){autoCannonFighterAttackChance += QuickAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.QUICK);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){autoCannonFighterAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){autoCannonFighterAttackChance += SlowAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.SLOW);}
                        else{ autoCannonCapAttackChance += ModerateAttackChanceWeight; autoCannon.SetFireRate(Utility.Speed.MODERATE);}
                    }
                }
            }

            if(autoCannonCapAttackChance + autoCannonFrigAttackChance + autoCannonFighterAttackChance <= 0){ autoCannon.SetTarget(null); return;}

            int chooseTarget = MathUtils.random(1,autoCannonCapAttackChance + autoCannonFrigAttackChance + autoCannonFighterAttackChance);
            if(chooseTarget <= autoCannonCapAttackChance){autoCannon.SetTarget(closestCap);}
            else if(chooseTarget <= autoCannonCapAttackChance + autoCannonFrigAttackChance){autoCannon.SetTarget(closestFrig);}
            else if(chooseTarget <= autoCannonCapAttackChance + autoCannonFrigAttackChance + autoCannonFighterAttackChance){autoCannon.SetTarget(closestFighter);}
            else{autoCannon.SetTarget(null);}
        }
    }

    private void ParseLogicScript()
    {
        playerAI = ScriptSaver.LoadScriptIntoArray(ScriptSaver.workingScriptPath);
        System.out.println(playerAI.size());
        //Loop over the blocks
        for(ArrayList<LogicGroups.LogicBlockType> blockChain : playerAI)
        {
            if(blockChain.size() > 0){
                //Get the first block, and then go to the relevent function to parse the line.
                if(blockChain.get(0) == LogicGroups.LogicBlockType.ATTACK){ ParseAttackLine(blockChain);}
            }
        }
    }

    private void ParseAttackLine(ArrayList<LogicGroups.LogicBlockType> logicLine)
    {

        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(logicLine);
        workingLine.remove(0); //Remove the first element of the line, since we know it's attack

        LogicGroups.LogicBlockType target = LogicGroups.LogicBlockType.NULL;
        LogicGroups.LogicBlockType weapon = LogicGroups.LogicBlockType.NULL;
        LogicGroups.LogicBlockType speed = LogicGroups.LogicBlockType.NULL;
        //The correct order for an attackline should be Attack -> Object -> Weapon -> Speed
        if(workingLine.size() > 0){
            target = workingLine.get(0);
            workingLine.remove(0);
        }
        if(workingLine.size() > 0){
            weapon = workingLine.get(0);
            workingLine.remove(0);
        }
        if(workingLine.size() > 0){
            speed = workingLine.get(0);
            workingLine.remove(0);
        }

        attackTargets.add(new Target(target,weapon,speed));
    }




}
