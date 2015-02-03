package com.mygdx.game.GameObjects.Weapons;

import com.badlogic.gdx.Game;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.Utility.Utility;

/**
 * Created by Elliot Morris on 30/01/2015.
 * Stores details about a target
 */
public class Target {
    public Utility.Speed speed;
    public Utility.Weapon firingWeapon;
    public Utility.Target target;

    public Target(LogicGroups.LogicBlockType target, LogicGroups.LogicBlockType  firingWeapon, LogicGroups.LogicBlockType  firingSpeed) {

        if (target == LogicGroups.LogicBlockType.CAPITALSHIP) {
            this.target = Utility.Target.CAPITAL;
        } else if (target == LogicGroups.LogicBlockType.FRIGATESHIP) {
            this.target = Utility.Target.FRIGATE;
        } else if (target == LogicGroups.LogicBlockType.FIGHTERSHIP) {
            this.target = Utility.Target.FIGHTER;
        } else if (target == LogicGroups.LogicBlockType.ASTEROIDS) {
            this.target = Utility.Target.ASTEROID;
        } else {
            this.target = Utility.Target.ALL;
        }

        if (firingWeapon == LogicGroups.LogicBlockType.AUTOCANNON) {
            this.firingWeapon = Utility.Weapon.AUTOCANNON;
        } else if (firingWeapon == LogicGroups.LogicBlockType.LASER) {
            this.firingWeapon = Utility.Weapon.LASER;
        } else if (firingWeapon == LogicGroups.LogicBlockType.MISSILE) {
            this.firingWeapon = Utility.Weapon.MISSILE;
        } else {
            this.firingWeapon = Utility.Weapon.ALL;
        }

        if (firingSpeed == LogicGroups.LogicBlockType.QUICKLY) {
            this.speed = Utility.Speed.QUICK;
        } else if (firingSpeed == LogicGroups.LogicBlockType.SLOWLY) {
            this.speed = Utility.Speed.SLOW;
        } else {
            this.speed = Utility.Speed.MODERATE;
        }

    }
}
