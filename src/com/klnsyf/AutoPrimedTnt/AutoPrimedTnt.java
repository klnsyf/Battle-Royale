package com.klnsyf.AutoPrimedTnt;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class AutoPrimedTnt implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void throwPrimedTnt(PlayerInteractEvent event) {
		if (event.getPlayer().getItemInHand().getType() == Material.TNT) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
				event.setCancelled(true);
				Vector vector = event.getPlayer().getLocation().getDirection();
				Entity entity = event.getPlayer().getWorld().spawnEntity(event.getPlayer().getEyeLocation(),
						EntityType.PRIMED_TNT);
				vector.multiply(0.64);
				((TNTPrimed) entity).setFuseTicks(80);
				((TNTPrimed) entity).setVelocity(vector);
				if (event.getPlayer().getItemInHand().getAmount() > 1) {
					event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
				} else {
					event.getPlayer().getInventory().clear(event.getPlayer().getInventory().getHeldItemSlot());
				}
			}
		}
	}

}
