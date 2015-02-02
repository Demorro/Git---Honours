package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
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

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class Ship extends SteerableObject{

    private TextureRegion shipRegion;

    protected ArrayList<Target> attackTargets = new ArrayList<Target>();

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
    protected CollisionAvoidance<Vector2> avoid;
    private static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<Vector2>(new Vector2());


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

        avoid = new CollisionAvoidance<Vector2>(this,this);
        blendedSteering.add(avoid, 0.5f);

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
    public void SetTargetAndFire(ShipGuns gun, GameObject target)
    {
        if(gun == ShipGuns.AUTOCANNON) {
            if(autoCannon != null) {
                autoCannon.SetTarget(target);
                autoCannon.SetIsAutoFiring(true);
            }
        }
        else if(gun == ShipGuns.LASER){

        }
        else if(gun == ShipGuns.MISSILE){

        }
    }


    protected GameObject GetClosestObject(ArrayList<? extends GameObject> objects, OrthographicCamera camera){

        if(objects.size() <= 0) {
            Gdx.app.log("Error", "Ship.GetClosestObject(), object list must be > 0");
        }

        float closestDistance = 999999;
        GameObject closestObj = null;

        for(GameObject obj : objects){
            Vector3 screenPos = camera.unproject(new Vector3(obj.getX(), obj.getY(), 0));
            if(camera.frustum.boundsInFrustum(screenPos.x,screenPos.y,screenPos.z,obj.getWidth()/2, obj.getHeight()/2,1)) {
                if (obj.GetCenterPosition().dst(this.GetCenterPosition()) < closestDistance) {
                    closestDistance = obj.GetCenterPosition().dst(this.GetCenterPosition());
                    closestObj = obj;
                }
            }
        }

        return closestObj;
    }





}
