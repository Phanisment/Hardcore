package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.event.player.PlayerDeathCallback;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.phanisment.hardcore.util.ConfigManager;
import io.phanisment.hardcore.util.TempbanManager;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");
	public static final ConcurrentHashMap<UUID, Long> bannedPlayers = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Hardcore?? Nope");
		
		PlayerDeathCallback.EVENT.register((player, damageSource) -> {
			if (player instanceof ServerPlayerEntity) {
				TempbanManager.tempBanPlayer((ServerPlayerEntity) player, 20);
			}
		});
		
		ServerTickEvents.END_SERVER_TICK.register(server -> TempbanManager.checkUnban());
		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			UUID playerUUID = handler.getProfile().getId();
			if (TempbanManager.isBanned(playerUUID)) {
				handler.disconnect(Text.of("You are still temporarily banned!"));
			}
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LOGGER.info("Saved Banned Player...");
			ConfigManager.saveBannedPlayers(TempbanManager.tempBannedPlayers);
		});
	}
}
