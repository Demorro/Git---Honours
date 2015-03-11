package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.utils.Collision;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.mygdx.game.LogicBlocks.BlockChain;
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

    //private ArrayList<ArrayList<LogicGroups.LogicBlockType>> playerAI = new ArrayList<ArrayList<LogicGroups.LogicBlockType>>();
    private FullBlockScript playerAI;


    private ArrayList<EnemyCapitalShip> caps;
    private ArrayList<EnemyFrigateShip> frigs;
    private ArrayList<EnemyFighterShip> fighters;

    //Choosing what to attack with each gun is done on a random chance sort of basis, and the speed determines the weighting of this
    private int QuickAttackChanceWeight = 3;
    private int ModerateAttackChanceWeight = 2;
    private int SlowAttackChanceWeight = 1;

    private static float shipRadius = 15;
    private static float maxLinearVelocity = 300;
    private static float maxLinearVelocityAccel = 220;
    private static float maxAngularVelocity = 5;
    private static float maxAngularVelocityAccel = 2;

    private boolean hasSetupLogic = false; //Cause we cant setup everything in constructor cause things havnt been initialised.

    private SteerableObject pursueTarget;
    private float pursueDisableDistance = 100; //Stupid hack to stop really close collisions
    private SteerableObject evadeTarget;
    private float evadeDisableDistance = 450; //Stupid hack again
    private SteerableObject attackTarget;

    private static Vector2 collisionBoxNegativeOffset = new Vector2(20,0);

    public PlayerShip(Texture gameObjectTexSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, TextureAtlas destructionExplosionAtlas)
    {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet,0,0,100,76), 100, shipRadius, maxLinearVelocity,maxLinearVelocityAccel,maxAngularVelocity,maxAngularVelocityAccel, collisionBoxNegativeOffset, destructionExplosionAtlas);
        this.gameObjectTexSheet = gameObjectTexSheet;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1000, 0.5f, new Rectangle(0,750,18,50), GetCenterPosition(),25, Utility.Weapon.AUTOCANNON);
        autoCannon.SetFastMedSlowFireRate(0.12f, 0.22f, 0.32f);
        laser = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1600, 2.5f, new Rectangle(30,731,12,69), GetCenterPosition(), 5, Utility.Weapon.LASER);
        laser.SetFastMedSlowFireRate(0.4f, 1.0f, 1.8f);
        torpedo = new Gun(bulletPool, bulletList, gameObjectTexSheet, 400, 20, new Rectangle(78,768,30,30), GetCenterPosition(), 2, Utility.Weapon.MISSILE);
        torpedo.SetFastMedSlowFireRate(2.5f, 4.0f, 6.5f);

        this.caps = caps;
        this.frigs = frigs;
        this.fighters = fighters;

        autoCannon.SetIsAutoFiring(true);
        laser.SetIsAutoFiring(true);
        torpedo.SetIsAutoFiring(true);


        //This is a dumb hack but it works. We're repurposing the script structure from the editor state to store logic in the game, (mainly due to the tech debt we racked up using it to load in xml, but either way, it needs a texture, so just give it any old texture, its never displayed
        playerAI = new FullBlockScript(gameObjectTexSheet,null,null);
        ScriptSaver.LoadScript(playerAI, ScriptSaver.workingScriptPath);
    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
    {
        if(hasSetupLogic == false){
            ParseLogicScript();
            hasSetupLogic = true;
        }

        autoCannon.ClearTarget();

        pursueTargets.clear();
        evadeTargets.clear();
        attackTargets.clear();
        ParseLogicScript();

        ResolveWeaponAttackTarget(caps, frigs, fighters, autoCannon, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, laser, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, torpedo, camera);
        ResolvePersueTargets(caps, frigs, fighters, camera);
        ResolveEvadeTargets(caps, frigs, fighters, camera);

        CustomLogic();

        super.Update(elapsed,camera, bullets);


    }

    private void ParseLogicScript()
    {
        //Loop over the blocks
        BlockChain nextChain = playerAI.GetBlockChains().get(0);
        while(nextChain != null)
        {
            if(nextChain.GetBlockList().size() > 0){

                //When blocks
                if(nextChain.GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.WHEN){
                    if(IsWhenStatementTrue(ScriptSaver.ConvertBlockChainToTypeLine(nextChain)) == false)
                    {
                        //if the when statement isnt true, skip past this if statement
                        nextChain = nextChain.GetNextBlockAfterIf();
                        continue;
                    }
                }

                //Get the first block, and then go to the relevent function to parse the line.
                if(nextChain.GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.ATTACK){ ParseAttackLine(ScriptSaver.ConvertBlockChainToTypeLine(nextChain));}
                else if(nextChain.GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.PURSUE){ ParsePersueLine(ScriptSaver.ConvertBlockChainToTypeLine(nextChain));}
                else if(nextChain.GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.EVADE){ ParseEvadeLine(ScriptSaver.ConvertBlockChainToTypeLine(nextChain));}
            }

            nextChain = nextChain.GetBelowBlockChain();
        }
    }

    private boolean IsWhenStatementTrue(ArrayList<LogicGroups.LogicBlockType> logicLine)
    {
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(logicLine);
        workingLine.remove(0); //Remove the first element of the line, since we know it's when

        //If the when statement is about weapons
        if((workingLine.get(0) == LogicGroups.LogicBlockType.AUTOCANNON) || (workingLine.get(0) == LogicGroups.LogicBlockType.LASER) || (workingLine.get(0) == LogicGroups.LogicBlockType.MISSILE)){
            return DoWhenWeapon(workingLine);
        }
        //If the when statment is about ships
        else if((workingLine.get(0) == LogicGroups.LogicBlockType.CAPITALSHIP) || (workingLine.get(0) == LogicGroups.LogicBlockType.FRIGATESHIP) || (workingLine.get(0) == LogicGroups.LogicBlockType.FIGHTERSHIP)){
            return DoWhenEnemyShip(workingLine);
        }
        //If the when statement is about ship health
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.SHIPHP){
            return DoWhenShipHP(workingLine);
        }
        return false;
    }
    //Functions called inside IsWhenStatement true for the different types on when statement, takes an array with the when statement removed
    private boolean DoWhenWeapon(ArrayList<LogicGroups.LogicBlockType> whenWeaponLine){
        //Get the weapon
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(whenWeaponLine);
        Gun weapon = null;
        if(workingLine.get(0) == LogicGroups.LogicBlockType.AUTOCANNON){weapon = autoCannon;}
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.LASER){weapon = laser;}
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.MISSILE){weapon = torpedo;}
        workingLine.remove(0);
        if(weapon == null){Gdx.app.log("Error","Weapon must not be null, DoWhenWeapon, PlayerShip.cpp");}

        //The final block should be is/isnt firing
        if(workingLine.get(0) == LogicGroups.LogicBlockType.ISFIRING){
            if(weapon.IsFiringRightNow()){
                return true;
            }else{
                return false;
            }
        }
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.ISNTFIRING){
            if(!weapon.IsFiringRightNow()){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    private boolean DoWhenEnemyShip(ArrayList<LogicGroups.LogicBlockType> whenEnemiesLine) {
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(whenEnemiesLine);

        //Distances for close and far
        float closeDistance = 180.0f;
        float farDistance = 500.0f;

        boolean enemyIsFighters = false;
        boolean enemyIsFrigates = false;
        boolean enemyIsCaps = false;

        //this block is a ship type
        if(workingLine.get(0) == LogicGroups.LogicBlockType.FIGHTERSHIP){enemyIsFighters = true;}
        if(workingLine.get(0) == LogicGroups.LogicBlockType.FRIGATESHIP){enemyIsFrigates = true;}
        if(workingLine.get(0) == LogicGroups.LogicBlockType.CAPITALSHIP){enemyIsCaps = true;}

        workingLine.remove(0);

        //The next block is close/far
        if(workingLine.get(0) == LogicGroups.LogicBlockType.CLOSE){
            if(enemyIsFighters){
                for(EnemyFighterShip ship : fighters){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) < closeDistance){
                        return true;
                    }
                }
            }
            else if(enemyIsFrigates){
                for(EnemyFrigateShip ship : frigs){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) < closeDistance){
                        return true;
                    }
                }
            }
            if(enemyIsCaps){
                for(EnemyCapitalShip ship : caps){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) < closeDistance){
                        return true;
                    }
                }
            }
        }
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.FAR){
            if(enemyIsFighters){
                for(EnemyFighterShip ship : fighters){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) > farDistance){
                        return true;
                    }
                }
            }
            else if(enemyIsFrigates){
                for(EnemyFrigateShip ship : frigs){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) > farDistance){
                        return true;
                    }
                }
            }
            if(enemyIsCaps){
                for(EnemyCapitalShip ship : caps){
                    if(ship.GetCenterPosition().dst(GetCenterPosition()) > farDistance){
                        return true;
                    }
                }
            }
        }

        return false;
    }
    private boolean DoWhenShipHP(ArrayList<LogicGroups.LogicBlockType> shipHPLine){
        ArrayList<LogicGroups.LogicBlockType> workingLine = new ArrayList<LogicGroups.LogicBlockType>(shipHPLine);

        //First block is ship hp, so we can just pop it
        workingLine.remove(0);

        //Less than/More than/Full
        if(workingLine.get(0) == LogicGroups.LogicBlockType.LESSTHAN){
            workingLine.remove(0);
            if(GetHealth() < LogicGroups.NumberBlockToInt(workingLine.get(0))){
                return true;
            }
        }
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.MORETHAN){
            workingLine.remove(0);
            if(GetHealth() > LogicGroups.NumberBlockToInt(workingLine.get(0))){
                return true;
            }
        }
        else if(workingLine.get(0) == LogicGroups.LogicBlockType.FULL){
            return (GetHealth() >= GetMaxHealth());
        }

        return false;
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
            if(chooseTarget <= capAttackChance){weapon.SetTarget(closestCap); attackTarget = closestCap;}
            else if(chooseTarget <= capAttackChance + frigAttackChance){weapon.SetTarget(closestFrig); attackTarget = closestFrig;}
            else if(chooseTarget <= capAttackChance + frigAttackChance + fighterAttackChance){weapon.SetTarget(closestFighter); attackTarget = closestFighter;}
            else{weapon.SetTarget(null); attackTarget = null;}
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
                    System.out.println("Frigs");
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
                if(target.target == Utility.Target.ALL){
                    ArrayList<SteerableObject> closestShips = new ArrayList<SteerableObject>();
                    closestShips.add(closestCap);
                    closestShips.add(closestFrig);
                    closestShips.add(closestFighter);
                    SteerableObject closestShip = GetClosestObject(closestShips, camera);
                    if (closestCandidateObj == null) {
                        closestCandidateObj = closestShip;
                        closestCandidateObjPursueSpeed = target.speed;
                    } else {
                        if ((closestShip.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                            closestCandidateObj = closestShip;
                            closestCandidateObjPursueSpeed = target.speed;
                        }
                    }
                }
            }

            if(closestCandidateObj != null){
                Vector3 screenPos = new Vector3(closestCandidateObj.getPosition().x, closestCandidateObj.getPosition().y, 0);
                if(camera.frustum.pointInFrustum(screenPos.x, screenPos.y, screenPos.z))
                {
                    SetPursueTarget(closestCandidateObj, closestCandidateObjPursueSpeed);
                    pursueTarget = closestCandidateObj;
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
                if(target.target == Utility.Target.ALL){
                    ArrayList<SteerableObject> closestShips = new ArrayList<SteerableObject>();
                    closestShips.add(closestCap);
                    closestShips.add(closestFrig);
                    closestShips.add(closestFighter);
                    SteerableObject closestShip = GetClosestObject(closestShips, camera);
                    if (closestCandidateObj == null) {
                        closestCandidateObj = closestShip;
                        closestCandidateObjPursueSpeed = target.speed;
                    } else {
                        if ((closestShip.GetCenterPosition().dst(this.GetCenterPosition())) < (closestCandidateObj.GetCenterPosition().dst(this.GetCenterPosition()))) {
                            closestCandidateObj = closestShip;
                            closestCandidateObjPursueSpeed = target.speed;
                        }
                    }
                }
            }

            if(closestCandidateObj != null){
                Vector3 screenPos = new Vector3(closestCandidateObj.getPosition().x, closestCandidateObj.getPosition().y, 0);
                if(camera.frustum.pointInFrustum(screenPos.x,screenPos.y,screenPos.z))
                {
                    SetEvadeTarget(closestCandidateObj, closestCandidateObjPursueSpeed);
                    evadeTarget = closestCandidateObj;
                }
            }
            else
            {
                SetBehaviorActive(evadeBehavior, false);
            }

        }

    }

    private void CustomLogic()
    {
        if(pursueTarget != null){
            if(pursueTarget.getPosition().dst(getPosition()) < pursueDisableDistance){
                SetBehaviorActive(pursueBehavior, false);
            }else{
                SetBehaviorActive(pursueBehavior, true);
            }
        }

        if(evadeTarget != null)
        {
            if(evadeTarget.getPosition().dst(getPosition()) > evadeDisableDistance){
                SetBehaviorActive(evadeBehavior, false);
            }else{
                SetBehaviorActive(evadeBehavior, true);
            }
        }
    }




}
