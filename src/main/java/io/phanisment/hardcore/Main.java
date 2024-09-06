package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;

public class Main implements ModInitializer {
	public final HashMap<String, Long> ban = new HasMap<>();
	
	@Override
	public void onInitialize() {
		// Respwn Event
		ServerPlayerEvents.AFTER_RESPAWN.register((player, newplayer, alive) -> {
			if (player instanceof ServerPlayerEntity) {
				this.ban((ServerPlayerEntity) player);
			}
		});
		
		// Tick Event
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerManager().getPlayerList().forEach(player -> {
				if (player instanceof ServerPlayerEntity) {
					String playerName = player.getGameProfile().getName();
					if (ban.containsKey(playerName)) {
						long end = ban.get(playerName);
						if (System.currentTimeMillis() < end) {
							player.networkHandler.disconnect(Text.literal("You have been temporarily banned for 20 minutes!"));
						} else {
							this.unban((ServerPlayerEntity) player);
						}
					}
				}
			});
		});
	}
	
	// Code Shortcut
	public void ban(ServerPlayerEntity player) {
		String playerName = player.getGameProfile().getName();
		long duration = 20 * 60 * 1000;
		ban.put(playerName, System.currentTimeMillis() + duration);
	}
	
	public void unban(ServerPlayerEntity player) {
		String playerName = player.getGameProfile().getName();
		ban.remove(playerName);
	}
}