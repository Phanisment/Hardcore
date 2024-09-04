package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");
	public static final ConcurrentHashMap<UUID, Long> bannedPlayers = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Hardcore?? Nope");
		
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			TempbanCommand.register(dispatcher);
		});
		
		ServerTickEvents.END_SERVER_TICK.register(server -> TempBanManager.checkUnban());
		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			UUID playerUUID = handler.getProfile().getId();
			if (TempBanManager.isBanned(playerUUID)) {
				handler.disconnect(Text.of("You are still temporarily banned!"));
			}
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ConfigManager.saveBannedPlayers(TempBanManager.tempBannedPlayers);
		});
	}
}
