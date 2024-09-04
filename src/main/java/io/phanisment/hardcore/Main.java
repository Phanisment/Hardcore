package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.phanisment.hardcore.util.ConfigManager;
import io.phanisment.hardcore.util.TempbanManager;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");

	@Override
	public void onInitialize() {
		LOGGER.info("Hardcore?? Nope");
		
		ServerTickEvents.END_SERVER_TICK.register(server -> TempbanManager.checkUnban());
		
		ServerLoginConnectionEvents.QUERY_START.register((handler, server) -> {
			String playerName = handler.getName().getString();
			if (TempbanManager.isBanned(playerName)) {
				handler.disconnect(Text.of("You are still temporarily banned!"));
			}
		});
		
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (oldPlayer instanceof ServerPlayerEntity) {
				TempbanManager.tempBanPlayer((ServerPlayerEntity) oldPlayer, 20);
			}
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LOGGER.info("Saved Banned Player...");
			ConfigManager.saveBannedPlayers(TempbanManager.tempBannedPlayers);
		});
	}
}
