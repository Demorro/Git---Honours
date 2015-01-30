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
    public Utility.Speed firingSpeed;
    public Utility.Weapon firingWeapon;
    public Utility.Target target;

    public Target(LogicGroups.LogicBlockType target, LogicGroups.LogicBlockType  firingWeapon, LogicGroups.LogicBlockType  firingSpeed) {

        if (target == LogicGroups.LogicBlockType.CAPITALSHIP) {
            this.target = Utility.Target.CAPITAL;
        } else if (target == LogicGroups.LogicBlockType.FRIGATESHIP) {
            this.target = Utility.Target.FRIGATE;
        } else if (target == LogicGroups.LogicBlockType.FIGHTERSHIP) {
            this.target = Utility.Target.FIGHTER;
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
            this.firingSpeed = Utility.Speed.QUICK;
        } else if (firingSpeed == LogicGroups.LogicBlockType.SLOWLY) {
            this.firingSpeed = Utility.Speed.SLOW;
        } else {
            this.firingSpeed = Utility.Speed.MODERATE;
        }

    }
}
