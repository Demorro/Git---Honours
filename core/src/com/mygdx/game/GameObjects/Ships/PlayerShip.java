package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.utils.Collision;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.GameObjects.Weapons.Target;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.Utility.ScriptSaver;
import com.mygdx.game.Utility.Utility;

import java.lang.reflect.Array;
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

    private static float shipRadius = 15;
    private static float maxLinearVelocity = 200;
    private static float maxLinearVelocityAccel = 500;
    private static float maxAngularVelocity = 5;
    private static float maxAngularVelocityAccel = 2;

    private boolean hasSetupLogic = false; //Cause we cant setup everything in constructor cause things havnt been initialised.


    public PlayerShip(Texture gameObjectTexSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters)
    {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet,0,0,100,76), 100, shipRadius, maxLinearVelocity,maxLinearVelocityAccel,maxAngularVelocity,maxAngularVelocityAccel);
        this.gameObjectTexSheet = gameObjectTexSheet;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1000, 1, new Rectangle(0,750,18,50), GetCenterPosition(), 25, Utility.Weapon.AUTOCANNON);
        autoCannon.SetFastMedSlowFireRate(0.15f, 0.35f, 0.6f);
        laser = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1600, 10, new Rectangle(30,731,12,69), GetCenterPosition(), 5, Utility.Weapon.LASER);
        laser.SetFastMedSlowFireRate(0.4f, 1.0f, 1.8f);
        torpedo = new Gun(bulletPool, bulletList, gameObjectTexSheet, 400, 50, new Rectangle(78,768,30,30), GetCenterPosition(), 2, Utility.Weapon.MISSILE);
        torpedo.SetFastMedSlowFireRate(3.0f, 5.0f, 7.0f);

        this.caps = caps;
        this.frigs = frigs;
        this.fighters = fighters;

        autoCannon.SetIsAutoFiring(true);
        laser.SetIsAutoFiring(true);
        torpedo.SetIsAutoFiring(true);


    }

    public void Update(float elapsed, OrthographicCamera camera)
    {
        if(hasSetupLogic == false){
            ParseLogicScript();
            hasSetupLogic = true;
        }

        super.Update(elapsed);
        ResolveWeaponAttackTarget(caps, frigs, fighters, autoCannon, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, laser, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, torpedo, camera);
        ResolvePersueTargets(caps, frigs, fighters, camera);
        ResolveEvadeTargets(caps, frigs, fighters, camera);

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            translate(2000 * elapsed, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            translate(-2000 * elapsed, 0);
        }

    }

    //Takes the list of targets that the logic script has loaded in, and resolves what should be targetted and how much
    private void ResolveWeaponAttackTarget(ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, Gun weapon, OrthographicCamera camera)
    {
        if(attackTargets.size() > 0)
        {
            SteerableObject closestCap = null;
            SteerableObject closestFrig = null;
            SteerableObject closestFighter = null;
            if(caps.size() > 0){
                closestCap = GetClosestObject(caps, camera);
            }
            if(frigs.size() > 0) {
                closestFrig = GetClosestObject(frigs, camera);
            }
            if(fighters.size() > 0) {
                closestFighter = GetClosestObject(fighters, camera);
            }

            int capAttackChance = 0; int frigAttackChance = 0; int fighterAttackChance = 0;

            //Get chance to attack specific target
            for(Target target : attackTargets){
                if(target.target == Utility.Target.CAPITAL){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.speed == Utility.Speed.QUICK){capAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.speed == Utility.Speed.MODERATE){capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.speed == Utility.Speed.SLOW){capAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        capAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
                }
                else if(target.target == Utility.Target.FRIGATE){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.speed == Utility.Speed.QUICK){frigAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.speed == Utility.Speed.MODERATE){frigAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.speed == Utility.Speed.SLOW){frigAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        frigAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
                }
                else if(target.target == Utility.Target.FIGHTER){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.speed == Utility.Speed.QUICK){fighterAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.speed == Utility.Speed.MODERATE){fighterAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.speed == Utility.Speed.SLOW){fighterAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        fighterAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
                }
                else if(target.target == Utility.Target.ASTEROID){
                    //Dont do anything yet, asteroids arnt in
                }
                else if(target.target == Utility.Target.ALL){
                    fighterAttackChance += ModerateAttackChanceWeight;
                    frigAttackChance += ModerateAttackChanceWeight;
                    capAttackChance += ModerateAttackChanceWeight;
                    weapon.SetFireRate(Utility.Speed.MODERATE, true);
                }
            }

            if(capAttackChance + frigAttackChance + fighterAttackChance <= 0){ weapon.SetTarget(null); return;}

            int chooseTarget = MathUtils.random(1,capAttackChance + frigAttackChance + fighterAttackChance);
            if(chooseTarget <= capAttackChance){weapon.SetTarget(closestCap);}
            else if(chooseTarget <= capAttackChance + frigAttackChance){weapon.SetTarget(closestFrig);}
            else if(chooseTarget <= capAttackChance + frigAttackChance + fighterAttackChance){weapon.SetTarget(closestFighter);}
            else{weapon.SetTarget(null);}
        }
    }

    private void ResolvePersueTargets(ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, OrthographicCamera camera)
    {
        if(pursueTargets.size() > 0) {
            SteerableObject closestCap = null;
            SteerableObject closestFrig = null;
            SteerableObject closestFighter = null;
            if(caps.size() > 0){
                closestCap = GetClosestObject(caps, camera);
            }
            if(frigs.size() > 0) {
                closestFrig = GetClosestObject(frigs, camera);
            }
            if(fighters.size() > 0) {
                closestFighter = GetClosestObject(fighters, camera);
            }

            SteerableObject closestCandidateObj = null;
            Utility.Speed closestCandidateObjPursueSpeed = null;

            for(Target target : pursueTargets){
                if(target.target == Utility.Target.CAPITAL){
                    if(closestCap != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestCap;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestCap.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestCap;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
                if(target.target == Utility.Target.FRIGATE){
                    if(closestFrig != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestFrig;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestFrig.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestFrig;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
                if(target.target == Utility.Target.FIGHTER){
                    if(closestFighter != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestFighter;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestFighter.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestFighter;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
            }

            if(closestCandidateObj != null){
                Vector3 screenPos = new Vector3(closestCandidateObj.getX(), closestCandidateObj.getY(), 0);
                if(camera.frustum.boundsInFrustum(screenPos.x,screenPos.y,screenPos.z,getWidth()/2, getHeight()/2,1))
                {
                    SetPursueTarget(closestCandidateObj, closestCandidateObjPursueSpeed);
                }
            }
            else
            {
                SetBehaviorActive(pursueBehavior, false);
            }

        }

    }

    private void ResolveEvadeTargets(ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, OrthographicCamera camera)
    {
        if(evadeTargets.size() > 0) {
            SteerableObject closestCap = null;
            SteerableObject closestFrig = null;
            SteerableObject closestFighter = null;
            if(caps.size() > 0){
                closestCap = GetClosestObject(caps, camera);
            }
            if(frigs.size() > 0) {
                closestFrig = GetClosestObject(frigs, camera);
            }
            if(fighters.size() > 0) {
                closestFighter = GetClosestObject(fighters, camera);
            }

            SteerableObject closestCandidateObj = null;
            Utility.Speed closestCandidateObjPursueSpeed = null;

            for(Target target : evadeTargets){
                if(target.target == Utility.Target.CAPITAL){
                    if(closestCap != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestCap;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestCap.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestCap;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
                if(target.target == Utility.Target.FRIGATE){
                    if(closestFrig != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestFrig;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestFrig.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestFrig;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
                if(target.target == Utility.Target.FIGHTER){
                    if(closestFighter != null) {
                        if (closestCandidateObj == null) {
                            closestCandidateObj = closestFighter;
                            closestCandidateObjPursueSpeed = target.speed;
                        } else {
                            if ((closestFighter.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                                closestCandidateObj = closestFighter;
                                closestCandidateObjPursueSpeed = target.speed;
                            }
                        }
                    }
                }
            }

            if(closestCandidateObj != null){
                Vector3 screenPos = new Vector3(closestCandidateObj.getX(), closestCandidateObj.getY(), 0);
                if(camera.frustum.boundsInFrustum(screenPos.x,screenPos.y,screenPos.z,getWidth()/2, getHeight()/2,1))
                {
                    SetEvadeTarget(closestCandidateObj, closestCandidateObjPursueSpeed);
                }
            }
            else
            {
                SetBehaviorActive(evadeBehavior, false);
            }

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
                else if(blockChain.get(0) == LogicGroups.LogicBlockType.PURSUE){ ParsePersueLine(blockChain);}
                else if(blockChain.get(0) == LogicGroups.LogicBlockType.EVADE){ ParseEvadeLine(blockChain);}
            }
        }
    }

    private void ParseAttackLine(ArrayList<LogicGroups.LogicBlockType> logicLine)
    {
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(logicLine);
        workingLine.remove(0); //Remove the first element of the line, since we know it's attack

        LogicGroups.LogicBlockType target = null;
        LogicGroups.LogicBlockType weapon = null;
        LogicGroups.LogicBlockType speed = null;
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

        attackTargets.add(new Target(target, weapon, speed));
    }

    private void ParsePersueLine(ArrayList<LogicGroups.LogicBlockType> logicLine)
    {
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(logicLine);
        workingLine.remove(0); //Remove the first element of the line, since we know it's persue

        LogicGroups.LogicBlockType target = null;
        LogicGroups.LogicBlockType weapon = null;
        LogicGroups.LogicBlockType speed = null;
        //The correct order for a persueLine should be Speed -> Object -> Speed
        if(workingLine.size() > 0){
            target = workingLine.get(0);
            workingLine.remove(0);
        }
        if(workingLine.size() > 0){
            speed = workingLine.get(0);
            workingLine.remove(0);
        }

        pursueTargets.add(new Target(target, weapon, speed));
    }

    private void ParseEvadeLine(ArrayList<LogicGroups.LogicBlockType> logicLine)
    {
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(logicLine);
        workingLine.remove(0); //Remove the first element of the line, since we know it's evade

        LogicGroups.LogicBlockType target = null;
        LogicGroups.LogicBlockType weapon = null;
        LogicGroups.LogicBlockType speed = null;
        //The correct order for a evadeLine should be Speed -> Object -> Speed
        if(workingLine.size() > 0){
            target = workingLine.get(0);
            workingLine.remove(0);
        }
        if(workingLine.size() > 0){
            speed = workingLine.get(0);
            workingLine.remove(0);
        }

        evadeTargets.add(new Target(target, weapon, speed));
    }




}
