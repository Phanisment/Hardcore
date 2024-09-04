package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");
	
	@Override
	public void onInitialize() {
		LOGGER.info("Hardcore?? NOPE")
		
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