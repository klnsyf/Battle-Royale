package com.klnsyf.BattleRoyale;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameLoadEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	public GameLoadEvent() {
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
