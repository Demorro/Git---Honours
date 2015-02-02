package com.mygdx.game.GameObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 01/02/2015.
 */
public class SteerableObject extends GameObject implements Steerable<Vector2>, Proximity<Vector2> {


    //STEERING BEHAVIOR STUFF//
    private ArrayList<SteerableObject> worldObjects = null;
    private float proximity = 200; //How close an object has to be to be considered in proximity
    protected Vector2 linearVelocity = new Vector2(); //Updated each update loop, used for steering behaviors
    private Vector2 lastFramePosition = new Vector2(0,0);
    protected float angularVelocity = 0; //As above, used for steering behaviors, updated each update loop
    private float lastFrameAngle = 0;
    protected float shipBoundingRadius = 1; //The bounding radius of the ship, used in steering, set in constructor
    //Max speeds for steering behaviours
    protected float maxLinearSpeed = 1;
    protected float maxLinearAcceleration = 1;
    protected float maxAngularSpeed = 1;
    protected float maxAngularAcceleration = 1;
    private Steerable<Vector2> proxOwner = null; //Used in proximity
    private boolean tagged = false; //Used for the steering behaviors
    private boolean independentFacing = false;
    private float orientationOffset = 90; //Sprites arnt neccesarily drawn in the corrent orientation (in fact they're normally 90 deg off) so this fixes it
    private static float dstToUpdateRot = 5.0f; //Distance the movement vector has to be to rotate the ship to it.

    public SteerableObject(Texture gameObjectTexSheet)
    {
        super(gameObjectTexSheet);
        lastFramePosition = GetCenterPosition();
        lastFrameAngle = getRotation();
        proxOwner = this;
    }

    protected void Update(float elapsed)
    {
    }

    //Need to be called in state construction.
    public void SetAllSteerables(ArrayList<SteerableObject> allWorldObjects)
    {
        worldObjects = allWorldObjects;
    }

    protected void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed
        this.translate(linearVelocity.x * time, linearVelocity.y * time);

        // Update orientation and angular velocity
        if (independentFacing) {
            rotate(angularVelocity * time);
            this.angularVelocity += steering.angular * time;
        } else {
            if(linearVelocity.len() > dstToUpdateRot) {
                // For non-independent facing we have to align orientation to linear velocity
                float newOrientation = vectorToAngle(linearVelocity);
                if (newOrientation != getRotation()) {
                    this.angularVelocity = (newOrientation - getRotation()) * time;
                    setRotation(newOrientation + orientationOffset);
                }
            }
        }

        this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());


    }


    @Override
    public Steerable<Vector2> getOwner() {
        return proxOwner;
    }

    @Override
    public void setOwner(Steerable<Vector2> owner) {
        proxOwner = owner;
    }

    @Override
    public int findNeighbors(Proximity.ProximityCallback<Vector2> callback) {

        int neighborCount = 0;

        if(worldObjects == null){
            Gdx.app.log("Error", "WorldObject Array is null in SteerableObject. Fix yo bloody program m8 plz ta thx. You need to call SetAllSteerables()");
        }
        else
        {
            for(SteerableObject obj : worldObjects)
            {
                if(obj.GetID() != this.GetID()) { //Dont want to run behavior on yoself, so compare ids
                    if (obj.GetCenterPosition().dst(this.GetCenterPosition()) <= (obj.getBoundingRadius()/2 + this.getBoundingRadius()/2)) {
                        neighborCount++;
                        callback.reportNeighbor(obj);
                    }
                }
            }
        }

        return neighborCount;
    }
    @Override
    public Vector2 getPosition() {
        return GetCenterPosition();
    }

    @Override
    public float getOrientation() {
        return getRotation();
    }

    @Override
    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    @Override
    public float getAngularVelocity() {
        return angularVelocity;
    }

    @Override
    public float getBoundingRadius() {
        return shipBoundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public Vector2 newVector() {
        return new Vector2();
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return vector.angle();
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.setAngle(angle);
        return outVector;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    public void SetIndependatFacing(boolean _independant)
    {
        independentFacing = _independant;
    }
}
