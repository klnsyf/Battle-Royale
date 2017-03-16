package com.klnsyf.BattleRoyale;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleRoyaleOption extends JavaPlugin {

	String world;

	double maxHealth;
	float saturation;

	double worldBorderMaxRadius;
	double worldBorderMinRadius;

	double spreadMinRadius;
	double spreadMaxRadius;
	double spreadTargetRadius;
	double spreadMinSpace;

	double cordonRadius;
	ArrayList<Player> craftedCompass;
	ArrayList<Player> crossedCordon;

	double initialWorldBorderSpeed;
	double worldBorderSpeed;
	boolean isTeam;
	boolean isSingle;

	boolean isBattleFieldLoading;

	static boolean isGaming;
	ArrayList<Player> alivePlayers;
	static boolean isLoading;

	boolean enableIronBlock;
	ArrayList<Player> usedIronBlock;
	int ironBlockDuration;
	int ironBlockColdDown;

	boolean canDamageAnimals;
	boolean autoMelt;
	boolean clearWeather;

	int compassColdDown;
	int ironBlockProtect;

	void loadConfig() {
		world = this.getConfig().getString("world");
		maxHealth = this.getConfig().getDouble("maxHealth");
		saturation = this.getConfig().getFloat("saturation");
		worldBorderMaxRadius = this.getConfig().getDouble("worldBorderMaxRadius");
		worldBorderMinRadius = this.getConfig().getDouble("worldBorderMinRadius");
		spreadMinRadius = this.getConfig().getDouble("spreadMinRadius");
		spreadMaxRadius = this.getConfig().getDouble("spreadMaxRadius");
		spreadTargetRadius = this.getConfig().getDouble("spreadTargetRadius");
		spreadMinSpace = this.getConfig().getDouble("spreadMinSpace");
		cordonRadius = this.getConfig().getDouble("cordonRadius");
		initialWorldBorderSpeed = this.getConfig().getDouble("initialWorldBorderSpeed");
		ironBlockDuration = this.getConfig().getInt("ironBlockDuration");
		ironBlockColdDown = this.getConfig().getInt("ironBlockColdDown");
		canDamageAnimals = this.getConfig().getBoolean("canDamageAnimals");
		autoMelt = this.getConfig().getBoolean("autoMelt");
		clearWeather = this.getConfig().getBoolean("clearWeather");
		compassColdDown = this.getConfig().getInt("compassColdDown");
		ironBlockProtect = this.getConfig().getInt("ironBlockProtect");
	}

	public static int getGameState() {
		if (isGaming) {
			return 1;
		} else if (isLoading) {
			return 0;
		} else {
			return -1;
		}
	}

}
