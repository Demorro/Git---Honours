package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    private static float shipRadius = 10;
    private static float maxLinearVelocity = 500;
    private static float maxLinearVelocityAccel = 500;
    private static float maxAngularVelocity = 300;
    private static float maxAngularVelocityAccel = 100;

    private static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<Vector2>(new Vector2());
    private Seek<Vector2> seek;
    private CollisionAvoidance<Vector2> avoid;

    public PlayerShip(Texture gameObjectTexSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters)
    {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet,0,0,100,76), 100, shipRadius, maxLinearVelocity,maxLinearVelocityAccel,maxAngularVelocity,maxAngularVelocityAccel);
        this.gameObjectTexSheet = gameObjectTexSheet;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1000, 1, new Rectangle(0,750,18,50), GetCenterPosition(), 12, Utility.Weapon.AUTOCANNON);
        autoCannon.SetFastMedSlowFireRate(0.15f, 0.35f, 0.6f);
        laser = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1600, 10, new Rectangle(30,731,12,69), GetCenterPosition(), 5, Utility.Weapon.LASER);
        laser.SetFastMedSlowFireRate(0.4f, 1.0f, 1.8f);
        torpedo = new Gun(bulletPool, bulletList, gameObjectTexSheet, 400, 50, new Rectangle(78,768,30,30), GetCenterPosition(), 2, Utility.Weapon.MISSILE);
        torpedo.SetFastMedSlowFireRate(3.0f, 5.0f, 7.0f);

        this.caps = caps;
        this.frigs = frigs;
        this.fighters = fighters;

        ParseLogicScript();

        autoCannon.SetIsAutoFiring(true);
        laser.SetIsAutoFiring(true);
        torpedo.SetIsAutoFiring(true);

        seek = new Seek<Vector2>(this, caps.get(0));

    }

    public void Update(float elapsed, OrthographicCamera camera)
    {
        super.Update(elapsed);
        ResolveWeaponAttackTarget(caps, frigs, fighters, autoCannon, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, laser, camera);
        ResolveWeaponAttackTarget(caps, frigs, fighters, torpedo, camera);

        seek.calculateSteering(steeringOutput);
        this.translate(linearVelocity.x * elapsed, linearVelocity.y * elapsed);
        linearVelocity.mulAdd(steeringOutput.linear, elapsed);
    }

    //Takes the list of targets that the logic script has loaded in, and resolves what should be targetted and how much
    public void ResolveWeaponAttackTarget(ArrayList<EnemyCapitalShip> caps, ArrayList<EnemyFrigateShip> frigs, ArrayList<EnemyFighterShip> fighters, Gun weapon, OrthographicCamera camera)
    {
        if(attackTargets.size() > 0)
        {
            GameObject closestCap = GetClosestObject(caps, camera);
            GameObject closestFrig = GetClosestObject(frigs, camera);
            GameObject closestFighter = GetClosestObject(fighters, camera);

            int capAttackChance = 0; int frigAttackChance = 0; int fighterAttackChance = 0;

            //Get chance to attack specific target
            for(Target target : attackTargets){
                if(target.target == Utility.Target.CAPITAL){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.firingSpeed == Utility.Speed.QUICK){capAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){capAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        capAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
                }
                else if(target.target == Utility.Target.FRIGATE){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.firingSpeed == Utility.Speed.QUICK){frigAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){frigAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){frigAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        frigAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
                }
                else if(target.target == Utility.Target.FIGHTER){
                    if(target.firingWeapon == weapon.GetWeaponType()){
                        if(target.firingSpeed == Utility.Speed.QUICK){fighterAttackChance += QuickAttackChanceWeight; weapon.SetFireRate(Utility.Speed.QUICK, true);}
                        else if(target.firingSpeed == Utility.Speed.MODERATE){fighterAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                        else if(target.firingSpeed == Utility.Speed.SLOW){fighterAttackChance += SlowAttackChanceWeight; weapon.SetFireRate(Utility.Speed.SLOW, true);}
                        else{ capAttackChance += ModerateAttackChanceWeight; weapon.SetFireRate(Utility.Speed.MODERATE, true);}
                    }
                    else if(target.firingWeapon == Utility.Weapon.ALL){
                        fighterAttackChance += ModerateAttackChanceWeight;
                        weapon.SetFireRate(Utility.Speed.MODERATE, true);
                    }
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

        attackTargets.add(new Target(target,weapon,speed));
    }




}
