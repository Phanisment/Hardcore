package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");
	public static final ConcurrentHashMap<UUID, Long> bannedPlayers = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Hardcore?? Nope");
		
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			long currentTime = System.currentTimeMillis();
			bannedPlayers.forEach((uuid, banEndTime) -> {
				if (currentTime >= banEndTime) {
					bannedPlayers.remove(uuid);
				}
			});
		});
	}
}