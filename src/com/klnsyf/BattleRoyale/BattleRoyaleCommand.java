package com.klnsyf.BattleRoyale;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import com.klnsyf.Sugar.Sugar;

public class BattleRoyaleCommand extends BattleRoyaleFeature implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (label.equalsIgnoreCase("br")) {
			if (args.length == 0) {
				sender.sendMessage("§6==========[§b Help§6 ]==========");
				for (Method method : BattleRoyaleCommand.class.getDeclaredMethods()) {
					if (!method.isAnnotationPresent(SubCommand.class)) {
						continue;
					}
					SubCommand sub = method.getAnnotation(SubCommand.class);
					sender.sendMessage("§6/br §b" + sub.cmd() + " §3" + sub.arg() + "§6-§a " + sub.des());
				}
				return true;
			}
			for (Method method : BattleRoyaleCommand.class.getDeclaredMethods()) {
				if (!method.isAnnotationPresent(SubCommand.class)) {
					continue;
				}
				SubCommand sub = method.getAnnotation(SubCommand.class);
				if (!sub.cmd().equalsIgnoreCase(args[0])) {
					continue;
				}
				try {
					method.invoke(this, sender, args);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return true;
			}
			sender.sendMessage("§aUndefinded SubCommand:§c " + args[0]);
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@SubCommand(cmd = "start", des = "Game Start!")
	public void start(CommandSender sender, String[] args) {
		if (isGaming == true || isLoading == true) {
			forcedStop();
		}
		gameReset();
		isLoading = true;
		Bukkit.broadcastMessage("§6§l[Battle Royale]§r §cChat has been silenced.");
		worldBorderSpeed = initialWorldBorderSpeed;
		alivePlayers.addAll(Bukkit.getOnlinePlayers());
		if (Bukkit.getOnlinePlayers().size() == 1) {
			isSingle = true;
		} else {
			isSingle = false;
		}
		boolean isSpreadSuccess = spreadPlayer(alivePlayers);
		if (isSpreadSuccess) {
			for (Player player : alivePlayers) {
				playerReset(player);
				Sugar.addPotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 1103700, 32);
				Sugar.addPotionEffect(player, PotionEffectType.JUMP, 1103700, 128);
				Sugar.addPotionEffect(player, PotionEffectType.SLOW, 1103700, 128);
				if (isSingle == true) {
					player.sendTitle("§6[Battle Royale]", "§c-- SinglePlayer --");
				} else {
					player.sendTitle("§6[Battle Royale]", "§c-- MutiPlayer --");
				}
				Location location = player.getLocation();
				location.setY(location.getY() - 1);
				new BukkitRunnable() {
					int t = 0;

					@Override
					public void run() {
						if (t <= 180) {
							for (int i = 0; i < 100; i++) {
								player.playEffect(location, Effect.WITCH_MAGIC, 0);
							}
							t++;
						} else {
							this.cancel();
						}

					}
				}.runTaskTimerAsynchronously(this, 0, 5);
			}
			new BukkitRunnable() {
				double t = 450;

				@Override
				public void run() {
					if (t == 300) {
						Bukkit.getPluginManager().callEvent(new GameLoadEvent());
					}
					if (t < 300) {
						for (Player player : alivePlayers) {
							player.setLevel((int) (t / 10));
							player.setExp((float) (t / 300));
							String timer = "Game Start  ";
							for (int i = 0; i < (int) ((300 - t) / 20); i++) {
								timer = timer.concat("§2▋");
							}
							for (int i = 0; i < 15 - (int) ((300 - t) / 20); i++) {
								timer = timer.concat("§4▋");
							}
							timer = timer.concat("§r  " + t / 10 + " seconds");
							Sugar.sendActionBarMessage(player, timer);
						}
					}
					if (t == 1) {
						for (Player player : alivePlayers) {
							player.sendTitle("§6[Battle Royale]", "§c-- Game Start --");
							player.playEffect(player.getLocation(), Effect.CLICK2, 0);
							Sugar.removeAllPotionEffect(player);
							Sugar.sendActionBarMessage(player, "-= Game Start =-");
							player.getInventory().clear();
							player.closeInventory();
							player.setLevel(0);
						}
						worldBorderSpeedControl(Bukkit.getWorld(world).getWorldBorder(), initialWorldBorderSpeed);
						Bukkit.getWorld(world).setDifficulty(Difficulty.HARD);
						Bukkit.getPluginManager().callEvent(new GameStartEvent());
						this.cancel();

					}
					t--;
				}
			}.runTaskTimer(this, 0, 2);
		} else {
			Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aToo many players. Spread failed.");
			forcedStop();
			gameReset();
			Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aChat is no longer slienced.");
		}
	}

	@SubCommand(cmd = "reset", des = "Reset the game")
	public void reset(CommandSender sender, String[] args) {
		Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aGame has been reset.");
		if (isGaming == true || isLoading == true) {
			forcedStop();
		}
		gameReset();
	}

	@SubCommand(cmd = "reload", des = "Reload the config")
	public void reload(CommandSender sender, String[] args) {
		Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aConfig has been reloaded.");
		loadConfig();
	}

	@SubCommand(cmd = "default", des = "Use default config")
	public void defaultConfig(CommandSender sender, String[] args) {
		Bukkit.broadcastMessage("§6§l[Battle Royale]§r §aconfig.yml has been rebuild with default configuration.");
		File file = new File(getDataFolder(), "config.yml");
		file.delete();
		this.saveDefaultConfig();
	}

	@SubCommand(cmd = "team")
	public void team(CommandSender sender, String[] args) {
		ArrayList<Player> players=new ArrayList<Player>();
		players.addAll(Bukkit.getOnlinePlayers());
		randomTeam(players);
	}

}
