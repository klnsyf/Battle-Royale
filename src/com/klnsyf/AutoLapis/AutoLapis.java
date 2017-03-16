package com.klnsyf.AutoLapis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import com.klnsyf.Sugar.Sugar;

public class AutoLapis implements Listener {

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent evt) {
		if (evt.getInventory().getType() == InventoryType.ENCHANTING) {
			evt.getInventory().setItem(1, Sugar.LAPIS_LAZULI(3));
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent evt) {
		if (evt.getInventory().getType() == InventoryType.ENCHANTING) {
			evt.getInventory().clear(1);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt) {
		if (evt.getInventory().getType() == InventoryType.ENCHANTING && evt.getSlot() == 1) {
			evt.setCancelled(true);
		}
	}
}
