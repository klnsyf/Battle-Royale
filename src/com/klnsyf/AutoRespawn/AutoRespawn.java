package com.klnsyf.AutoRespawn;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class AutoRespawn implements Listener {

	@EventHandler
	public void autoRespawn(PlayerDeathEvent event) {
		Location location = event.getEntity().getLocation();
		((CraftPlayer) event.getEntity()).getHandle().playerConnection
				.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
		event.getEntity().teleport(location);
	}
}
