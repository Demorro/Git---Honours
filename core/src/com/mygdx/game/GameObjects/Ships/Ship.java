package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.GameObjects.Weapons.Target;
import com.mygdx.game.Utility.Utility;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class Ship extends SteerableObject{

    private TextureRegion shipRegion;

    protected ArrayList<Target> attackTargets = new ArrayList<Target>();
    protected ArrayList<Target> pursueTargets = new ArrayList<Target>();
    protected ArrayList<Target> evadeTargets = new ArrayList<Target>();


    private float hp = 100;


    public enum ShipGuns
    {
        AUTOCANNON,
        LASER,
        MISSILE
    };

    protected Gun autoCannon = null;
    protected Vector2 autoCannonMuzzleOffset = new Vector2(0, -20);
    protected Gun laser = null;
    protected Vector2 laserMuzzleOffset = new Vector2(0,0);
    protected Gun torpedo = null;
    protected Vector2 torpedoMuzzleOffset = new Vector2(0,0);

    //Steering
    protected BlendedSteering<Vector2> blendedSteering;
    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
    protected Pursue<Vector2> pursueBehavior;
    private BlendedSteering.BehaviorAndWeight<Vector2> pursueBlend;
    protected Evade<Vector2> evadeBehavior;
    private BlendedSteering.BehaviorAndWeight<Vector2> evadeBlend;
    protected CollisionAvoidance<Vector2> avoidObjectBehavior;

    private static float fastWeighting = 0.6f;
    private static float moderateWeighting = 0.4f;
    private static float slowWeighting = 0.2f;

    public Ship(Texture gameObjectTexSheet, TextureRegion shipRegion, float startHealth, float boundingRadius, float maxLinearSpeed, float maxLinearAcceleration, float maxAngularSpeed, float maxAngularAcceleration)
    {
        super(gameObjectTexSheet);
        this.shipRegion = shipRegion;
        setRegion(shipRegion);

        hp = startHealth;

        this.shipBoundingRadius = boundingRadius;
        this.maxLinearSpeed = maxLinearSpeed;
        this.maxLinearAcceleration = maxLinearAcceleration;
        this.maxAngularSpeed = maxAngularSpeed;
        this.maxAngularAcceleration = maxAngularAcceleration;

        blendedSteering = new BlendedSteering<Vector2>(this);


        avoidObjectBehavior = new CollisionAvoidance<Vector2>(this,this);
        blendedSteering.add(avoidObjectBehavior, 0.6f);

        pursueBehavior = new Pursue<Vector2>(this, null);
        evadeBehavior = new Evade<Vector2>(this, null);
        pursueBehavior.setEnabled(false);
        evadeBehavior.setEnabled(false);
        pursueBehavior.setMaxPredictionTime(100);
        evadeBehavior.setMaxPredictionTime(100);

        pursueBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(pursueBehavior, 0.0f);
        evadeBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(evadeBehavior, 0.0f);

        blendedSteering.add(pursueBlend);
        blendedSteering.add(evadeBlend);

    }

    public void Update(float elapsed)
    {
        super.Update(elapsed);
        if(autoCannon != null) {
            autoCannon.Update(elapsed);
            autoCannon.SetPosition(GetCenterPosition().x + autoCannonMuzzleOffset.x, GetCenterPosition().y + autoCannonMuzzleOffset.y);
        }

        if(laser != null) {
            laser.Update(elapsed);
            laser.SetPosition(GetCenterPosition().x + laserMuzzleOffset.x, GetCenterPosition().y + laserMuzzleOffset.y);
        }

        if(torpedo != null) {
            torpedo.Update(elapsed);
            torpedo.SetPosition(GetCenterPosition().x + torpedoMuzzleOffset.x, GetCenterPosition().y + torpedoMuzzleOffset.y);
        }

        blendedSteering.calculateSteering(steeringOutput);
        applySteering(steeringOutput, elapsed);
    }

    public void Render(SpriteBatch batch)
    {
        Render(shipRegion, batch);
    }

    public void ChangeHealth(float healthChange){
        hp += healthChange;
    }
    public float GetHealth()
    {
        return hp;
    }


    protected SteerableObject GetClosestObject(ArrayList<? extends SteerableObject> objects, OrthographicCamera camera){

        if(objects.size() <= 0) {
            Gdx.app.log("Error", "Ship.GetClosestObject(), object list must be > 0");
        }

        float closestDistance = 999999;
        SteerableObject closestObj = null;

        for(SteerableObject obj : objects){
            Vector3 screenPos = new Vector3(obj.getX(), obj.getY(), 0);
            if(camera.frustum.boundsInFrustum(screenPos.x,screenPos.y,screenPos.z,obj.getWidth()/2, obj.getHeight()/2,1)) {
                if (obj.GetCenterPosition().dst(this.GetCenterPosition()) < closestDistance) {
                    closestDistance = obj.GetCenterPosition().dst(this.GetCenterPosition());
                    closestObj = obj;
                }
            }
        }

        return closestObj;
    }

    protected void SetPursueTarget(SteerableObject target, Utility.Speed pursueSpeed){
        pursueBehavior.setTarget(target);
        pursueBlend.setWeight(GetWeightFromSpeed(pursueSpeed));
        pursueBehavior.setEnabled(true);

    }
    protected void SetEvadeTarget(SteerableObject target, Utility.Speed pursueSpeed){
        evadeBehavior.setTarget(target);
        evadeBlend.setWeight(GetWeightFromSpeed(pursueSpeed));
        evadeBehavior.setEnabled(true);

    }
    protected void SetBehaviorActive(SteeringBehavior behavior, Boolean active){
        behavior.setEnabled(active);
    }

    private float GetWeightFromSpeed(Utility.Speed pursueSpeed){
        if(pursueSpeed == Utility.Speed.QUICK){return fastWeighting;}
        else if(pursueSpeed == Utility.Speed.MODERATE){return moderateWeighting;}
        else if(pursueSpeed == Utility.Speed.SLOW) {return slowWeighting;}
        return moderateWeighting;
    }
}
