package com.klnsyf.Sugar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.klnsyf.ActionBar.ActionBarMessageEvent;
import com.klnsyf.GlobalOption.GlobalOption;

public class Sugar extends JavaPlugin implements Listener {

	public static boolean works = true;

	public static void sendActionBarMessage(Player player, String message) {

		// Call the event, if cancelled don't send Action Bar
		ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
		Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
		if (actionBarMessageEvent.isCancelled())
			return;

		try {
			Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + GlobalOption.server + ".entity.CraftPlayer");
			Object p = c1.cast(player);
			Object ppoc;
			Class<?> c4 = Class.forName("net.minecraft.server." + GlobalOption.server + ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + GlobalOption.server + ".Packet");
			if (GlobalOption.oldVersion) {
				Class<?> c2 = Class.forName("net.minecraft.server." + GlobalOption.server + ".ChatSerializer");
				Class<?> c3 = Class.forName("net.minecraft.server." + GlobalOption.server + ".IChatBaseComponent");
				Method m3 = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
				ppoc = c4.getConstructor(new Class<?>[] { c3, byte.class }).newInstance(cbc, (byte) 2);
			} else {
				Class<?> c2 = Class.forName("net.minecraft.server." + GlobalOption.server + ".ChatComponentText");
				Class<?> c3 = Class.forName("net.minecraft.server." + GlobalOption.server + ".IChatBaseComponent");
				Object o = c2.getConstructor(new Class<?>[] { String.class }).newInstance(message);
				ppoc = c4.getConstructor(new Class<?>[] { c3, byte.class }).newInstance(o, (byte) 2);
			}
			Method m1 = c1.getDeclaredMethod("getHandle");
			Object h = m1.invoke(p);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		} catch (Exception e) {
			e.printStackTrace();
			works = false;
		}
	}

	public static void giveItemStack(Player player, Material... materials) {
		for (Material material : materials) {
			ItemStack itemStack = new ItemStack(material, 1);
			player.getInventory().addItem(itemStack);
		}
	}

	public static void giveItemStack(Player player, Material material, int amount) {
		ItemStack itemStack = new ItemStack(material, amount);
		player.getInventory().addItem(itemStack);
	}

	public static void giveItemStack(Player player, Material material, ItemMeta itemMeta) {
		ItemStack itemStack = new ItemStack(material, 1);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().addItem(itemStack);
	}

	public static void inventoryItemEdit(Inventory inventory, int index, Material material) {
		ItemStack itemStack = new ItemStack(material, 1);
		inventory.setItem(index, itemStack);
	}

	public static void inventoryItemEdit(Inventory inventory, int index, Material material, int amount) {
		ItemStack itemStack = new ItemStack(material, amount);
		inventory.setItem(index, itemStack);
	}

	public static void inventoryItemEdit(Inventory inventory, int index, Material material, ItemMeta itemMeta) {
		ItemStack itemStack = new ItemStack(material, 1);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(index, itemStack);
	}

	public static void inventoryItemEdit(Inventory inventory, int index, Material material, ItemMeta itemMeta,
			int amount) {
		ItemStack itemStack = new ItemStack(material, amount);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(index, itemStack);
	}

	public static void removeAllPotionEffect(Player player) {
		player.removePotionEffect(PotionEffectType.ABSORPTION);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.removePotionEffect(PotionEffectType.CONFUSION);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.HARM);
		player.removePotionEffect(PotionEffectType.HEAL);
		player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
		player.removePotionEffect(PotionEffectType.HUNGER);
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		player.removePotionEffect(PotionEffectType.POISON);
		player.removePotionEffect(PotionEffectType.REGENERATION);
		player.removePotionEffect(PotionEffectType.SATURATION);
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.WATER_BREATHING);
		player.removePotionEffect(PotionEffectType.WEAKNESS);
		player.removePotionEffect(PotionEffectType.WITHER);
	}

	public static void addPotionEffect(Player player, PotionEffectType potionEffectType, int duration, int amplifier) {
		PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, true, false);
		player.addPotionEffect(potionEffect, false);
	}

	public static ItemStack LAPIS_LAZULI(int amount) {
		Dye dye = new Dye();
		dye.setColor(DyeColor.BLUE);
		return dye.toItemStack(amount);
	}

	public static void addEnchantEffect(int slot, Inventory inventory) {
		ItemMeta itemMeta = inventory.getItem(slot).getItemMeta();
		itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		inventory.getItem(slot).setItemMeta(itemMeta);
	}

	public static void setItemDisplay(Inventory inventory, int index, String name, ArrayList<String> lore,
			boolean hideNBT) {
		ItemMeta itemMeta = inventory.getItem(index).getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);
		if (hideNBT) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
		}
		inventory.getItem(index).setItemMeta(itemMeta);
	}


}