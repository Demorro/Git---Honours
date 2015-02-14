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
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.CustomCollisionAvoidance;
import com.mygdx.game.CustomSeperation;
import com.mygdx.game.CustomWander;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.GameObjects.Weapons.Target;
import com.mygdx.game.Utility.Utility;
import jdk.nashorn.internal.runtime.Debug;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class Ship extends SteerableObject{

    private boolean _DEBUGBOUNDS = false;

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
    protected CustomCollisionAvoidance<Vector2> avoidObjectBehavior;
    protected CustomSeperation<Vector2> sepationBehavior;
    protected CustomWander<Vector2> noiseAddWanderBehavior; //Used for adding some noise to pursue/evade/others
    protected Cohesion<Vector2> cohesionBehavior;
    protected Alignment<Vector2> alignmentBehavior;

    private static float fastWeighting = 0.8f;
    private static float moderateWeighting = 0.6f;
    private static float slowWeighting = 0.4f;

    protected float steeringFriction = 0.5f;

    //Collision
    ShapeRenderer shapeRenderer = new ShapeRenderer();

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

        avoidObjectBehavior = new CustomCollisionAvoidance<Vector2>(this,this);
        pursueBehavior = new Pursue<Vector2>(this, null);
        evadeBehavior = new Evade<Vector2>(this, null);
        sepationBehavior = new CustomSeperation<Vector2>(this,this);
        noiseAddWanderBehavior = new CustomWander<Vector2>(this);
        cohesionBehavior = new Cohesion<Vector2>(this, this);
        alignmentBehavior = new Alignment<Vector2>(this,this);
        pursueBehavior.setEnabled(false);
        evadeBehavior.setEnabled(false);
        pursueBehavior.setMaxPredictionTime(0.5f);
        evadeBehavior.setMaxPredictionTime(0.5f);
        sepationBehavior.setDecayCoefficient(1);
        noiseAddWanderBehavior.setOwner(this);
        noiseAddWanderBehavior.setFaceEnabled(false);
        noiseAddWanderBehavior.setEnabled(false);
        noiseAddWanderBehavior.setWanderRate(2500);
        cohesionBehavior.setEnabled(false);
        alignmentBehavior.setEnabled(false);


        pursueBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(pursueBehavior, 0.0f);
        evadeBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(evadeBehavior, 0.0f);


        blendedSteering.add(pursueBlend);
        blendedSteering.add(evadeBlend);
        blendedSteering.add(avoidObjectBehavior, 0.4f);
        //blendedSteering.add(sepationBehavior, 500.0f);
        blendedSteering.add(noiseAddWanderBehavior, 0.1f);
        blendedSteering.add(alignmentBehavior,0.5f);
        blendedSteering.add(cohesionBehavior,0.5f);


    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
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


        this.linearVelocity.scl(1.0f - (steeringFriction * elapsed));

        blendedSteering.calculateSteering(steeringOutput);

        if((pursueBehavior.isEnabled()) || (evadeBehavior.isEnabled())){
            noiseAddWanderBehavior.setEnabled(true);
        }
        else{
            noiseAddWanderBehavior.setEnabled(false);
        }
        applySteering(steeringOutput, elapsed);

        BulletCollision(bullets);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void BulletCollision(ArrayList<Bullet> bullets)
    {
        Rectangle thisRect = new Rectangle(getX(), getY(), getRegionWidth(), getRegionHeight());
        for(Bullet bullet : bullets ){
            if(bullet.IsExploding() == false) {
                if (thisRect.contains(bullet.GetCenterPosition())) {
                    bullet.ExplodeAndDestroy();
                }
            }
        }
    }

    public void Render(SpriteBatch batch)
    {

        Render(shipRegion, batch);

        if(_DEBUGBOUNDS) {
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(getX(), getY(), getRegionWidth(), getRegionHeight());
            shapeRenderer.end();
            batch.begin();
        }

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
            if(obj != null) {
                if (obj.GetID() != GetID()) {
                    Vector3 screenPos = new Vector3(obj.getX(), obj.getY(), 0);
                    if (camera.frustum.boundsInFrustum(screenPos.x, screenPos.y, screenPos.z, obj.getWidth() / 2, obj.getHeight() / 2, 1)) {
                        if (obj.GetCenterPosition().dst(this.GetCenterPosition()) < closestDistance) {
                            closestDistance = obj.GetCenterPosition().dst(this.GetCenterPosition());
                            closestObj = obj;
                        }
                    }
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
    protected boolean GetBehaviorActive(SteeringBehavior behavior){
        return behavior.isEnabled();
    }

    private float GetWeightFromSpeed(Utility.Speed pursueSpeed){
        if(pursueSpeed == Utility.Speed.QUICK){return fastWeighting;}
        else if(pursueSpeed == Utility.Speed.MODERATE){return moderateWeighting;}
        else if(pursueSpeed == Utility.Speed.SLOW) {return slowWeighting;}
        return moderateWeighting;
    }
}
