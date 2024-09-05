package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
		
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (oldPlayer instanceof ServerPlayerEntity) {
				TempbanManager.tempBanPlayer((ServerPlayerEntity) oldPlayer, 20);
			}
		});
		
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerManager().getPlayerList().forEach(player -> {
				if (player instanceof ServerPlayerEntity) {
					TempBanManager.tempBanPlayer((ServerPlayerEntity) player, 20);  // Tempban selama 20 menit
				}
			});
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LOGGER.info("Saved Banned Player...");
			ConfigManager.saveBannedPlayers(TempbanManager.tempBannedPlayers);
		});
	}
}
