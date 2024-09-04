package io.phanisment.hardcore.api;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TempbanManager {
	private static final HashMap<UUID, Long> tempBannedPlayers = ConfigManager.loadBannedPlayers();
	public static void tempBanPlayer(ServerPlayerEntity player, long durationInMinutes) {
		UUID playerUUID = player.getUuid();
		long banEndTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(durationInMinutes);
		tempBannedPlayers.put(playerUUID, banEndTime);
		ConfigManager.saveBannedPlayers(tempBannedPlayers);
		player.networkHandler.disconnect(Text.of("You have been temporarily banned for " + durationInMinutes + " minutes."));
	}
	public static void checkUnban() {
		long currentTime = System.currentTimeMillis();
		tempBannedPlayers.entrySet().removeIf(entry -> {
			if (currentTime >= entry.getValue()) {
				ConfigManager.saveBannedPlayers(tempBannedPlayers);
				return true;
			}
			return false;
		});
	}
	public static boolean isBanned(UUID playerUUID) {
		return tempBannedPlayers.containsKey(playerUUID);
	}
}
