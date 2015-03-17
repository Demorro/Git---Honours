package com.mygdx.game.GameSystems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.Ships.EnemyCapitalShip;
import com.mygdx.game.GameObjects.Ships.EnemyFighterShip;
import com.mygdx.game.GameObjects.Ships.EnemyFrigateShip;
import com.mygdx.game.GameObjects.Ships.PlayerShip;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/02/2015.
 * Deals with updating and spawning ships
 */

public class ShipHandler {
    //Enemy ship array
    private ArrayList<EnemyCapitalShip> caps = new ArrayList<EnemyCapitalShip>();
    private ArrayList<EnemyFrigateShip> frigs = new ArrayList<EnemyFrigateShip>();
    private ArrayList<EnemyFighterShip> fighters = new ArrayList<EnemyFighterShip>();
    private TextureAtlas largeExplosionAtlas = null;

    private PlayerShip player;

    //Ship spawning
    private static int maxCapitalShips = 2;
    private static int maxFrigateShips = 2;
    private static int maxFighterShips = 2;
    private static int chanceToSpawnCapitalShip = 2; //Out of 100
    private static int chanceToSpawnFrigateShip = 10; //Out of 100
    private static int chanceToSpawnFighterShip = 25; //Out of 100
    private static float timeTOCheckShipSpawn = 0.5f;
    private float shipSpawnTimer = 0.0f;
    private static int shipDestructionDistance = 2400; //If a ship is this far from the player it will be destroyed.


    //Reference to the bullet arrays
    private ArrayList<Bullet> playerShotBullets;
    private ArrayList<Bullet> enemyShotBullets;
    private Pool<Bullet> bulletPool = null;

    private int score = 0;

    //reference to the texture sheet
    Texture gameObjectTextureSheet = null;

    private Camera gameCam;

    public ShipHandler(Texture gameObjectTextureSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> playerShotBullets, ArrayList<Bullet> enemyShotBullets, com.badlogic.gdx.graphics.Camera cam){

        this.gameObjectTextureSheet = gameObjectTextureSheet;

        largeExplosionAtlas = new TextureAtlas(Gdx.files.internal("Images/LargeExplosion/LargeExplosion.txt"));

        this.gameCam = cam;

        player = new PlayerShip(gameObjectTextureSheet, bulletPool, playerShotBullets, caps, frigs, fighters, largeExplosionAtlas, cam);
        player.setPosition(0,0);


        this.playerShotBullets = playerShotBullets;
        this.enemyShotBullets = enemyShotBullets;
        this.bulletPool = bulletPool;


        SetupSteerables();
    }

    public void Update(float elapsed, OrthographicCamera camera){
        player.Update(elapsed,camera,enemyShotBullets);

        for(EnemyCapitalShip ship : caps)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        for(EnemyFrigateShip ship : frigs)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        for(EnemyFighterShip ship : fighters)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        DestroyDeadShips();
        DestroyOverlyFarShips();

        //Do spawn ships
        shipSpawnTimer += elapsed;
        if(shipSpawnTimer > timeTOCheckShipSpawn){
            SpawnNewShips(camera);
            shipSpawnTimer = 0.0f;
        }
    }

    public void RenderEnemyShips(SpriteBatch spriteBatch){
        for(EnemyCapitalShip ship : caps){
            ship.Render(spriteBatch);
        }
        for(EnemyFrigateShip ship : frigs){
            ship.Render(spriteBatch);
        }
        for(EnemyFighterShip ship : fighters){
            ship.Render(spriteBatch);
        }

    }
    public void RenderPlayer(SpriteBatch spriteBatch){
        player.Render(spriteBatch);
    }

    private void DestroyDeadShips()
    {
        for(int i = 0; i < caps.size(); i++) {
            if(caps.get(i).ShouldBeDestroyed()){
                score += caps.get(i).destroyScore;
                caps.remove(i);
            }
        }
        for(int i = 0; i < frigs.size(); i++) {
            if(frigs.get(i).ShouldBeDestroyed()){
                score += frigs.get(i).destroyScore;
                frigs.remove(i);
            }
        }
        for(int i = 0; i < fighters.size(); i++) {
            if(fighters.get(i).ShouldBeDestroyed()){
                score += fighters.get(i).destroyScore;
                fighters.remove(i);
            }
        }

    }

    private void SpawnNewShips(OrthographicCamera camera)
    {

        if(caps.size() < maxCapitalShips){
            if(MathUtils.random(0,100) < chanceToSpawnCapitalShip){
                //Spawn new capital
                EnemyCapitalShip capitalShipToAdd = new EnemyCapitalShip(gameObjectTextureSheet,player, largeExplosionAtlas, bulletPool, enemyShotBullets, gameCam);
                Vector2 pointToSpawnAt = GenPointOutsideOfCam(camera);
                capitalShipToAdd.setPosition(pointToSpawnAt.x, pointToSpawnAt.y);
                caps.add(capitalShipToAdd);
                SetupSteerables();

                System.out.println("Spawning Capital Ship");
            }
        }
        if(frigs.size() < maxFrigateShips){
            if(MathUtils.random(0,100) < chanceToSpawnFrigateShip){
                //Spawn new frigate
                EnemyFrigateShip frigateShipToAdd = new EnemyFrigateShip(gameObjectTextureSheet,player, largeExplosionAtlas, bulletPool, enemyShotBullets, gameCam);
                Vector2 pointToSpawnAt = GenPointOutsideOfCam(camera);
                frigateShipToAdd.setPosition(pointToSpawnAt.x, pointToSpawnAt.y);
                frigs.add(frigateShipToAdd);
                SetupSteerables();

                System.out.println("Spawning Frigate Ship");
            }
        }
        if(fighters.size() < maxFighterShips){
            if(MathUtils.random(0,100) < chanceToSpawnFighterShip){
                //Spawn new fighter
                EnemyFighterShip fighterShipToAdd = new EnemyFighterShip(gameObjectTextureSheet,player, largeExplosionAtlas, bulletPool, enemyShotBullets, gameCam);
                Vector2 pointToSpawnAt = GenPointOutsideOfCam(camera);
                fighterShipToAdd.setPosition(pointToSpawnAt.x, pointToSpawnAt.y);
                fighters.add(fighterShipToAdd);
                SetupSteerables();

                System.out.println("Spawning Fighter Ship");
            }
        }
    }

    //Destroys any ships that are overly far away, so new, closer ones can be spawned
    private void DestroyOverlyFarShips(){
        for(int i = 0; i < caps.size(); i++) {
            if(caps.get(i).GetCenterPosition().dst(player.GetCenterPosition()) > shipDestructionDistance){
                caps.remove(i);
            }
        }
        for(int i = 0; i < frigs.size(); i++) {
            if(frigs.get(i).GetCenterPosition().dst(player.GetCenterPosition()) > shipDestructionDistance){
                frigs.remove(i);
            }
        }
        for(int i = 0; i < fighters.size(); i++) {
            if(fighters.get(i).GetCenterPosition().dst(player.GetCenterPosition()) > shipDestructionDistance){
                fighters.remove(i);
            }
        }
    }

    private Vector2 GenPointOutsideOfCam(OrthographicCamera camera)
    {
        int directionToSpawn = MathUtils.random(0,3); //Gets whether to spawn above, below, left of right of the camera, starting at up with 0 then going clockwise

        //Get random point in camera bounds
        float xPos = MathUtils.random(player.getPosition().x - Gdx.graphics.getWidth()/2, player.getPosition().x + Gdx.graphics.getWidth()/2);
        float yPos = MathUtils.random(player.getPosition().y - Gdx.graphics.getHeight()/2, player.getPosition().y + Gdx.graphics.getHeight()/2);


        if(directionToSpawn == 0){
            yPos += Gdx.graphics.getHeight();
        }
        else if(directionToSpawn == 1){
            xPos += Gdx.graphics.getWidth();
        }
        else if(directionToSpawn == 2){
            yPos -= Gdx.graphics.getHeight();
        }
        else if(directionToSpawn == 3){
            xPos -= Gdx.graphics.getWidth();
        }


        return new Vector2(xPos,yPos);
    }

    private void SetupSteerables()
    {
        ArrayList<SteerableObject> allObjects = new ArrayList<SteerableObject>();
        allObjects.addAll(caps);
        allObjects.addAll(frigs);
        allObjects.addAll(fighters);
        allObjects.add(player);
        player.SetAllSteerables(allObjects);
        for(SteerableObject obj : caps){ obj.SetAllSteerables(allObjects);}
        for(SteerableObject obj : frigs){ obj.SetAllSteerables(allObjects);}
        for(SteerableObject obj : fighters){ obj.SetAllSteerables(allObjects);}
    }

    public PlayerShip GetPlayer()
    {
        return player;
    }

    public int GetScore(){
        return score;
    }

}
