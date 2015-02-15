package com.mygdx.game.GameObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.proximities.InfiniteProximity;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 01/02/2015.
 */
public class SteerableObject extends GameObject implements Steerable<Vector2>, Proximity<Vector2>{


    //STEERING BEHAVIOR STUFF//
    protected ArrayList<SteerableObject> worldObjects = null;
    private float proximityRadius = 200; //How close an object has to be to be considered in proximity
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
    private long frameId = 0;

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
        if(linearVelocity.len() > getMaxLinearSpeed())
        {
            linearVelocity = linearVelocity.nor();
            linearVelocity.scl(getMaxLinearSpeed());
        }

        this.translate(linearVelocity.x * time, linearVelocity.y * time);

        // Update orientation and angular velocity
        if (independentFacing) {
            rotate(angularVelocity * time);
            this.angularVelocity += steering.angular * time;
        } else {
            if (linearVelocity.len() > dstToUpdateRot) {
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
    public Vector2 getPosition() {
        return new Vector2(GetCenterPosition().x, GetCenterPosition().y);
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
        Vector2 outVec = new Vector2(outVector.x, outVector.y);
        outVec.setAngle(angle);
        return outVec;
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

    @Override
    public Steerable<Vector2> getOwner() {
        return this.proxOwner;
    }

    @Override
    public void setOwner(Steerable<Vector2> owner) {
        this.proxOwner = owner;
    }

    @Override
    public int findNeighbors(ProximityCallback<Vector2> callback) {
        int agentCount = worldObjects.size();
        int neighborCount = 0;

        // Check current frame id to avoid repeating calculations
        // when this proximity is used by multiple group behaviors.
        if (this.frameId != Gdx.graphics.getFrameId()) {
            // Save the frame id
            this.frameId = Gdx.graphics.getFrameId();

            Vector2 ownerPosition = proxOwner.getPosition();

            // Scan the agents searching for neighbors
            for (int i = 0; i < agentCount; i++) {
                Steerable<Vector2> currentAgent = worldObjects.get(i);

                // Make sure the agent being examined isn't the owner
                if (currentAgent != proxOwner) {
                    float squareDistance = ownerPosition.dst2(currentAgent.getPosition());

                    // The bounding radius of the current agent is taken into account
                    // by adding it to the range
                    float range = proximityRadius + currentAgent.getBoundingRadius();

                    // If the current agent is within the range, report it to the callback
                    // and tag it for further consideration.
                    if (squareDistance < range * range) {
                        if (callback.reportNeighbor(currentAgent)) {
                            currentAgent.setTagged(true);
                            neighborCount++;
                            continue;
                        }
                    }
                }

                // Clear the tag
                currentAgent.setTagged(false);
            }
        } else {
            // Scan the agents searching for tagged neighbors
            for (int i = 0; i < agentCount; i++) {
                Steerable<Vector2> currentAgent = worldObjects.get(i);

                // Make sure the agent being examined isn't the owner and that
                // it's tagged.
                if (currentAgent != proxOwner && currentAgent.isTagged()) {

                    if (callback.reportNeighbor(currentAgent)) {
                        neighborCount++;
                    }
                }
            }
        }
        return neighborCount;
    }
}
