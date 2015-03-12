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
import com.mygdx.game.GameObjects.Weapons.Explosion;
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
    private float maxHP = 100;


    public enum ShipGuns
    {
        AUTOCANNON,
        LASER,
        MISSILE
    };

    protected Gun autoCannon = null;
    protected Vector2 autoCannonMuzzleOffset = new Vector2(0, 0);
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
    private Vector2 collisionBoxNegativeOffset = new Vector2(0,0);

    //Destruction
    private ArrayList<Explosion> destructionExplosions = new ArrayList<Explosion>();
    private TextureAtlas destructionExplosionAtlas;
    protected int noOfDeathExplosions = 25;
    protected float timeBetweenExplosionSpawns = 0.03f;
    protected float explosionDamage = 10.0f;
    private float deathExplosionSpawnTimer = 0.0f;
    private boolean isExploding = false;
    private float fadeOutAlpha = 1.0f;
    private static float fadeOutSpeed = 1.5f;
    private boolean needsToBeDestroyed = false;

    public Ship(Texture gameObjectTexSheet, TextureRegion shipRegion, float startHealth, float boundingRadius, float maxLinearSpeed, float maxLinearAcceleration, float maxAngularSpeed, float maxAngularAcceleration, Vector2 collisionBoxNegativeOffset, TextureAtlas destructionExplosionAtlas)
    {
        super(gameObjectTexSheet);
        this.shipRegion = shipRegion;
        setRegion(shipRegion);

        hp = startHealth;
        maxHP = startHealth;

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
        noiseAddWanderBehavior.setEnabled(true);
        cohesionBehavior.setEnabled(false);
        alignmentBehavior.setEnabled(false);


        pursueBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(pursueBehavior, 0.0f);
        evadeBlend = new BlendedSteering.BehaviorAndWeight<Vector2>(evadeBehavior, 0.0f);


        blendedSteering.add(pursueBlend);
        blendedSteering.add(evadeBlend);
        blendedSteering.add(noiseAddWanderBehavior, 0.2f);
        //blendedSteering.add(avoidObjectBehavior, 0.4f);
        //blendedSteering.add(sepationBehavior, 500.0f);

        this.collisionBoxNegativeOffset = collisionBoxNegativeOffset;
        this.destructionExplosionAtlas = destructionExplosionAtlas;
    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
    {
        super.Update(elapsed);
        if(isExploding == false) {
            if (autoCannon != null) {
                autoCannon.Update(elapsed);
                autoCannon.SetPosition(GetCenterPosition().x + autoCannonMuzzleOffset.x, GetCenterPosition().y + autoCannonMuzzleOffset.y);
            }

            if (laser != null) {
                laser.Update(elapsed);
                laser.SetPosition(GetCenterPosition().x + laserMuzzleOffset.x, GetCenterPosition().y + laserMuzzleOffset.y);
            }

            if (torpedo != null) {
                torpedo.Update(elapsed);
                torpedo.SetPosition(GetCenterPosition().x + torpedoMuzzleOffset.x, GetCenterPosition().y + torpedoMuzzleOffset.y);
            }


            this.linearVelocity.scl(1.0f - (steeringFriction * elapsed));

            blendedSteering.calculateSteering(steeringOutput);

            /*
            if ((pursueBehavior.isEnabled()) || (evadeBehavior.isEnabled())) {
                noiseAddWanderBehavior.setEnabled(true);
            } else {
                noiseAddWanderBehavior.setEnabled(false);
            }
            */
            applySteering(steeringOutput, elapsed);

            BulletCollision(bullets);
            shapeRenderer.setProjectionMatrix(camera.combined);

            if (hp <= 0) {
                ExplodeAndDestroy();
            }
        }
        else //Explosion logic
        {
            deathExplosionSpawnTimer += elapsed;

            //Spawn explosions
            if(deathExplosionSpawnTimer > timeBetweenExplosionSpawns){
                if(destructionExplosions.size() < noOfDeathExplosions){
                    deathExplosionSpawnTimer = 0.0f;
                    destructionExplosions.add(new Explosion(destructionExplosionAtlas, 12.0f, true, explosionDamage));
                    Vector2 explosionSpawnPoint = GetCenterPosition();
                    explosionSpawnPoint.set(explosionSpawnPoint.x - destructionExplosions.get(destructionExplosions.size()-1).GetFrameWidth()/2, explosionSpawnPoint.y - destructionExplosions.get(destructionExplosions.size()-1).GetFrameHeight()/2);
                    explosionSpawnPoint.set(explosionSpawnPoint.x + MathUtils.random(-getRegionWidth()/2,getRegionWidth()/2), explosionSpawnPoint.y + MathUtils.random(-getRegionHeight() /2,getRegionHeight()/2));
                    destructionExplosions.get(destructionExplosions.size() - 1).setPosition(explosionSpawnPoint.x, explosionSpawnPoint.y);
                }
                else
                {
                    if(destructionExplosions.get(destructionExplosions.size() -1).IsCurrentlyExploding() == false)
                    {
                        needsToBeDestroyed = true;
                    }
                }
            }


            fadeOutAlpha -= fadeOutSpeed * elapsed;
            if(fadeOutAlpha <= 0.0f){ fadeOutAlpha = 0.0f;}

        }

        for(Explosion explosion : destructionExplosions){
            explosion.Update(elapsed);
        }
    }

    private void BulletCollision(ArrayList<Bullet> bullets)
    {
        Rectangle thisRect = new Rectangle(getX() + collisionBoxNegativeOffset.x/2, getY() + collisionBoxNegativeOffset.y/2, getRegionWidth() - collisionBoxNegativeOffset.x, getRegionHeight() - collisionBoxNegativeOffset.y);
        for(Bullet bullet : bullets ){
            if(bullet.IsExploding() == false) {
                if (thisRect.contains(bullet.GetCenterPosition())) {
                    bullet.ExplodeAndDestroy();
                    hp -= bullet.GetBulletDamage();
                }
            }
        }
    }

    private void ExplodeAndDestroy()
    {
        isExploding = true;
    }

    public void Render(SpriteBatch batch)
    {

        batch.setColor(1.0f, 1.0f, 1.0f, fadeOutAlpha);
        Render(shipRegion, batch);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        for(Explosion explosion : destructionExplosions)
        {
            if(explosion.IsCurrentlyExploding()) {
                explosion.Render(batch);
            }
        }

        if(_DEBUGBOUNDS) {
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(getX() + collisionBoxNegativeOffset.x/2, getY() + collisionBoxNegativeOffset.y/2, getRegionWidth() - collisionBoxNegativeOffset.x, getRegionHeight() - collisionBoxNegativeOffset.y);
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
    public float GetMaxHealth() {return maxHP;}

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

    public boolean ShouldBeDestroyed()
    {
        return needsToBeDestroyed;
    }
}
