package com.klnsyf.BattleRoyale;

import java.util.ArrayList;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import com.klnsyf.Sugar.Sugar;

public class BattleRoyaleFeature extends BattleRoyaleSugar implements Listener {

	@EventHandler
	public void gameStart(GameStartEvent event) {
		if (getGameState() == 0) {
			enableIronBlockTimer();
			initialSupply();
			Bukkit.broadcastMessage("§6§l[Battle Royale] §rSurvivor Locater");
			for (Player player : alivePlayers) {
				Bukkit.broadcastMessage("§7>> §d" + player.getName() + "§b>§f " + (int) (player.getLocation().getX())
						+ "," + (int) (player.getLocation().getY()) + "," + (int) (player.getLocation().getZ()));
			}
			isGaming = true;
			isLoading = false;
		}
	}

	@EventHandler
	public void weatherControl(WeatherChangeEvent event) {
		if (clearWeather) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setPlayerWeather(WeatherType.CLEAR);
			}
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		if (getGameState() == 1) {
			event.getPlayer().damage(11037);
		}
	}

	@EventHandler
	public void craftCompass(CraftItemEvent event) {
		if (getGameState() == 1) {
			if (event.getCurrentItem().getType() == Material.COMPASS) {
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("Drop to get information");
				Sugar.setItemDisplay(event.getInventory(), event.getSlot(), "§rCompass", lore, true);
				craftedCompass.add((Player) event.getWhoClicked());
				Bukkit.getConsoleSender().sendMessage(
						"§6§l[Battle Royale] §c" + event.getWhoClicked().getName() + " has crafted a compass.");
			}
		}
	}

	@EventHandler
	public void cordon(PlayerMoveEvent event) {
		if (getGameState() == 1) {
			if ((!event.getPlayer().getInventory().contains(Material.COMPASS)
					&& !crossedCordon.contains(event.getPlayer()))
					&& (Math.abs(event.getTo().getX()) < cordonRadius
							|| Math.abs(event.getTo().getZ()) < cordonRadius)) {
				event.getPlayer().teleport(event.getFrom());
				event.getPlayer()
						.sendMessage("§6§l[Battle Royale] §cNot allowing to cross the cordon without a compass.");
			} else if (!crossedCordon.contains(event.getPlayer())
					&& event.getPlayer().getInventory().contains(Material.COMPASS)
					&& (Math.abs(event.getTo().getX()) <= cordonRadius
							|| Math.abs(event.getTo().getZ()) <= cordonRadius)) {
				Bukkit.getConsoleSender().sendMessage(
						"§6§l[Battle Royale] §c" + event.getPlayer().getName() + " has crossed the cordon.");
				crossedCordon.add(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void illegalBlockBreak(BlockBreakEvent event) {
		if (getGameState() != 1) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§6§l[Battle Royale] §cBlock break has been disabled.");
		}
	}

	@EventHandler
	public void illegalEntityDamage(EntityDamageEvent event) {
		if (getGameState() != 1) {
			event.setCancelled(true);
		}
		if (event.getEntity() instanceof Animals && event.getEntity().getType() != EntityType.OCELOT
				&& event.getEntity().getType() != EntityType.WOLF && event.getEntity().getType() != EntityType.HORSE
				&& !canDamageAnimals) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void IllgealPlayerChat(AsyncPlayerChatEvent event) {
		if (getGameState() == 1) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§6§l[Battle Royale]§r §cChat has been silenced.");
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (getGameState() == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage("§6§l[Battle Royale]§c " + event.getEntity().getName() + " §cDied.");
			}
			Bukkit.getConsoleSender().sendMessage("§6§l[Battle Royale]§c " + event.getDeathMessage());
			alivePlayers.remove(event.getEntity().getPlayer());
			event.getEntity().setGameMode(GameMode.SPECTATOR);
			if (event.getEntity().getKiller() instanceof Player) {
				Sugar.addPotionEffect(event.getEntity().getKiller(), PotionEffectType.REGENERATION, 10, 6);
			}
			if (isSingle == true) {
				event.getEntity().sendTitle("§6[Battle Royale]", "§c-- Game Over --");
				Bukkit.getPluginManager().callEvent(new GameEndEvent());
				gameReset();
			} else if (alivePlayers.size() == 1) {
				Player winner = alivePlayers.get(0);
				alivePlayers.clear();
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.sendTitle("§6[Battle Royale]", "§c-- Winner: " + winner.getName() + "§c --");
				}
				Bukkit.getPluginManager().callEvent(new GameEndEvent());
				gameReset();
			} else {
				Bukkit.broadcastMessage("§6§l[Battle Royale]§c " + alivePlayers.size() + "§c survivors remaining.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void dropCompass(PlayerDropItemEvent event) {
		if (getGameState() != 1) {
			event.setCancelled(true);
		} else if (event.getItemDrop().getItemStack().getType() == Material.COMPASS) {
			event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
			event.getItemDrop().setPickupDelay(compassColdDown);
			event.getPlayer().sendMessage("§6§l[Battle Royale] §fWorld Border Infomation");
			WorldBorder worldBorder = Bukkit.getWorld(world).getWorldBorder();
			if (worldBorder.getSize() > 64) {
				int px = Math.abs((int) event.getPlayer().getLocation().getX());
				int pz = Math.abs((int) event.getPlayer().getLocation().getZ());
				int pd;
				if (px >= pz) {
					pd = (int) (worldBorder.getSize() / 2) - px;
				} else {
					pd = (int) (worldBorder.getSize() / 2) - pz;
				}

				String string;
				if (pd >= 800) {
					string = "§a";
				} else if (pd >= 500) {
					string = "§e";
				} else if (pd >= 200) {
					string = "§6";
				} else {
					string = "§4";
				}
				if (worldBorderSpeed <= initialWorldBorderSpeed) {
					event.getPlayer().sendMessage("§7>> §aRadius: " + (int) (worldBorder.getSize() / 2) + "m (" + string
							+ (int) pd + "§am)" + "     Speed: " + worldBorderSpeed + " §am/s");
				} else if (worldBorderSpeed >= 3 * initialWorldBorderSpeed) {
					event.getPlayer().sendMessage("§7>> §aRadius: " + (int) (worldBorder.getSize() / 2) + "m (" + string
							+ (int) pd + "§am)" + "     Speed: §c" + worldBorderSpeed + " §am/s");
				} else {
					event.getPlayer().sendMessage("§7>> §aRadius: " + (int) (worldBorder.getSize() / 2) + "m (" + string
							+ (int) pd + "§am)" + "     Speed: §e" + worldBorderSpeed + " §am/s");
				}
			} else {
				event.getPlayer().sendMessage("§6§l[Battle Royale] §aWorld border has already stopped shrinking.");
				if (isSingle) {
					event.getPlayer().sendTitle("§6[Battle Royale]",
							"§c-- Winner: " + event.getPlayer().getName() + "§c --");
					alivePlayers.clear();
					Bukkit.getPluginManager().callEvent(new GameEndEvent());
					gameReset();
				}
			}
			if (isSingle == true) {
			} else {
				event.getPlayer().sendMessage("§6§l[Battle Royale] §rSurvivor Locater");
				for (Player player : alivePlayers) {
					if (player == event.getPlayer()) {
					} else {
						int distance = (int) event.getPlayer().getLocation().distance(player.getLocation());
						String string;
						if (distance >= 800) {
							string = "§a";
						} else if (distance >= 500) {
							string = "§e";
						} else if (distance >= 200) {
							string = "§6";
						} else {
							string = "§4";
						}
						event.getPlayer().sendMessage("§7>> §d" + player.getName() + "§b>§f "
								+ (int) (player.getLocation().getX()) + "," + (int) (player.getLocation().getY()) + ","
								+ (int) (player.getLocation().getZ()) + string + " (" + distance + "m)");
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void dropIronBlock(PlayerDropItemEvent event) {
		if (getGameState() == 1) {
			if (event.getItemDrop().getItemStack().getType() == Material.IRON_BLOCK) {
				if (enableIronBlock) {
					if (!usedIronBlock.contains(event.getPlayer())) {
						event.setCancelled(true);
						if (event.getPlayer().getInventory()
								.getItem(event.getPlayer().getInventory().first(Material.IRON_BLOCK)).getAmount() > 1) {
							event.getPlayer().getInventory()
									.getItem(event.getPlayer().getInventory().first(Material.IRON_BLOCK))
									.setAmount(event.getPlayer().getInventory()
											.getItem(event.getPlayer().getInventory().first(Material.IRON_BLOCK))
											.getAmount() - 1);
						} else {
							event.getPlayer().getInventory()
									.clear(event.getPlayer().getInventory().first(Material.IRON_BLOCK));
						}
						worldBorderSpeed += initialWorldBorderSpeed;
						worldBorderSpeedControl(Bukkit.getWorld(world).getWorldBorder(), worldBorderSpeed);
						usedIronBlock.add(event.getPlayer());
						event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.sendMessage(
									"§6§l[Battle Royale] §cSomeone used Iron Block. World Border shrinks faster.");
						}
						Bukkit.getConsoleSender().sendMessage(
								"§6§l[Battle Royale] §c" + event.getPlayer().getName() + " used Iron Block.");
						new BukkitRunnable() {
							@Override
							public void run() {
								worldBorderSpeed -= initialWorldBorderSpeed;
								worldBorderSpeedControl(Bukkit.getWorld(world).getWorldBorder(), worldBorderSpeed);
							}
						}.runTaskLaterAsynchronously(this, ironBlockDuration);
						new BukkitRunnable() {
							@Override
							public void run() {
								usedIronBlock.remove(event.getPlayer());
							}
						}.runTaskLaterAsynchronously(this, ironBlockColdDown);
					} else {
						event.getPlayer().sendMessage("§6§l[Battle Royale] §cSkill is cooling now.");
					}
				} else {
					event.getPlayer().sendMessage("§6§l[Battle Royale] §cIron Block has been disabled.");
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void autoMelt(BlockBreakEvent event) {
		if (getGameState() == 1 && autoMelt) {
			if (event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.GOLD_ORE) {
				event.setCancelled(true);
				Location location = event.getBlock().getLocation();
				Material material = event.getBlock().getType();
				event.getBlock().setType(Material.AIR);
				ItemStack itemStack = null;
				if (material == Material.IRON_ORE) {
					itemStack = new ItemStack(Material.IRON_INGOT, 1);
				} else if (material == Material.GOLD_ORE) {
					itemStack = new ItemStack(Material.GOLD_INGOT, 1);
				}
				event.getPlayer().getWorld().dropItem(location, itemStack);
			}
		}
	}

	void enableIronBlockTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				enableIronBlock = true;
				for (Player player : alivePlayers) {
					// TODO: SUCESS SUGAR.ACHIEVEMENT API
					player.awardAchievement(Achievement.FULL_BEACON);
					player.removeAchievement(Achievement.FULL_BEACON);
					Bukkit.getConsoleSender().sendMessage("§6§l[Battle Royale] §cIron Block activated.");
				}
			}
		}.runTaskLaterAsynchronously(this, ironBlockProtect);
	}

	void initialSupply() {
		for (Player player : alivePlayers) {
			Sugar.giveItemStack(player, Material.REDSTONE, Material.BOAT);
		}
	}

}
