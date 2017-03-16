package com.klnsyf.BattleRoyale;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import com.klnsyf.Sugar.Sugar;

public class BattleRoyaleSugar extends BattleRoyaleTeam {

	void gameReset() {
		Bukkit.getScheduler().cancelAllTasks();
		Bukkit.getWorld(world).setDifficulty(Difficulty.PEACEFUL);
		Bukkit.getWorld(world).setGameRuleValue("showDeathMessages", "false");
		Bukkit.getWorld(world).setTime(0);
		WorldBorder worldBorder = Bukkit.getWorld(world).getWorldBorder();
		alivePlayers = new ArrayList<Player>();
		usedIronBlock = new ArrayList<Player>();
		crossedCordon = new ArrayList<Player>();
		craftedCompass = new ArrayList<Player>();
		if (isGaming == true || isLoading == true) {
			Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aGame Ended. The world border stopped shrinking at "
					+ (int) worldBorder.getSize() / 2 + " blocks wide.");
			Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aChat is no longer slienced.");
		}
		worldBorderReset(worldBorder);
		for (Player player : Bukkit.getOnlinePlayers()) {
			Sugar.removeAllPotionEffect(player);
			player.setGameMode(GameMode.SURVIVAL);
		}
		isBattleFieldLoading = false;
		isLoading = false;
		isGaming = false;
	}

	boolean worldBorderSpeedControl(WorldBorder worldBorder, double speed) {
		if (worldBorder.getSize() > worldBorderMinRadius) {
			worldBorder.setSize(worldBorderMinRadius * 2,
					(long) ((worldBorder.getSize() / 2 - worldBorderMinRadius) / speed));
			return true;
		} else {
			return false;
		}
	}

	void worldBorderReset(WorldBorder worldBorder) {
		worldBorder.setSize(worldBorderMaxRadius * 2);
		worldBorder.setCenter(0, 0);
		worldBorder.setDamageAmount(1);
		worldBorder.setDamageBuffer(0);
		worldBorder.setWarningDistance(100);
	}

	void forcedStop() {
		Bukkit.broadcastMessage("§6§l[Battle Royale]§r §cGame has been forced to terminate.");
		Bukkit.getPluginManager().callEvent(new GameEndEvent());
		gameReset();
	}

	void playerReset(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		Sugar.removeAllPotionEffect(player);
		player.setFoodLevel(20);
		player.setSaturation(saturation);
		player.setExhaustion(0);
		player.setMaxHealth(maxHealth);
		player.setHealth(maxHealth);
		player.setGameMode(GameMode.SURVIVAL);
	}

	boolean spreadPlayer(ArrayList<Player> players) {
		ArrayList<Double> legalRadian = spreadAttempt(1024);
		if (players.size() > legalRadian.size()) {
			return false;
		} else {
			for (Player player : players) {
				int index = (new Random()).nextInt(legalRadian.size());
				safetyTeleport(player, (int) (spreadTargetRadius * Math.cos(legalRadian.get(index))),
						(int) (spreadTargetRadius * Math.sin(legalRadian.get(index))));
				legalRadian.remove(index);
			}
			return true;
		}
	}

	void safetyTeleport(Player player, int x, int z) {
		Location location = new Location(Bukkit.getWorld(world), x, 255, z);
		location.getChunk().load(true);
		for (; location.getBlock().getType() == Material.AIR; location.setY(location.getY() - 1)) {
		}
		location.getBlock().setType(Material.BEDROCK);
		location.setY(location.getY() + 2);
		location.setX(location.getX() + 0.5);
		location.setZ(location.getZ() + 0.5);
		player.teleport(location);
	}

	ArrayList<Double> spreadAttempt(int attempt) {
		double minRadian = Math.asin(spreadMinRadius / spreadTargetRadius);
		double[] randomRadian = new double[attempt];
		for (int index = 0; index < attempt; index++) {
			randomRadian[index] = (new Random()).nextDouble() * 2 * Math.PI;
		}
		ArrayList<Double> legalRadian = new ArrayList<Double>();
		for (double radian : randomRadian) {
			if ((0 <= radian && radian < minRadian)
					|| (0.5 * Math.PI - minRadian < radian && radian < 0.5 * Math.PI + minRadian)
					|| (Math.PI - minRadian < radian && radian < Math.PI + minRadian)
					|| (1.5 * Math.PI - minRadian < radian && radian < 1.5 * Math.PI + minRadian)
					|| (2 * Math.PI - minRadian < radian && radian <= 2 * Math.PI)
					|| spreadSpaceTest(radian, legalRadian)) {
			} else {
				legalRadian.add(radian);
			}
		}
		return legalRadian;
	}

	boolean spreadSpaceTest(double testRadian, ArrayList<Double> radianArray) {
		double minRadianSpace = 2 * Math.asin(spreadMinSpace / 2 / spreadTargetRadius);
		boolean test = false;
		for (double radian : radianArray) {
			if (Math.abs(testRadian - radian) < minRadianSpace) {
				test = true;
			}
		}
		return test;
	}
}
