package com.klnsyf.BattleRoyale;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.klnsyf.AutoLapis.AutoLapis;
import com.klnsyf.AutoPrimedTnt.AutoPrimedTnt;
import com.klnsyf.AutoRespawn.AutoRespawn;
import com.klnsyf.GlobalOption.GlobalOption;

public class BattleRoyaleSystem extends BattleRoyaleCommand implements Listener {

	public void onEnable() {
		this.saveDefaultConfig();
		loadConfig();
		GlobalOption.plugin = this;
		GlobalOption.server = Bukkit.getServer().getClass().getPackage().getName();
		GlobalOption.server = GlobalOption.server.substring(GlobalOption.server.lastIndexOf(".") + 1);
		if (GlobalOption.server.equalsIgnoreCase("v1_8_R1") || GlobalOption.server.equalsIgnoreCase("v1_7_")) {
			GlobalOption.oldVersion = true;
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new AutoRespawn(), this);
		Bukkit.getPluginManager().registerEvents(new AutoLapis(), this);
		Bukkit.getPluginManager().registerEvents(new AutoPrimedTnt(), this);
	}

}
