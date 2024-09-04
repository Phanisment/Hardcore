package io.phanisment.hardcore.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.phanisment.hardcore.util.ConfigManager;

public class TempbanManager {
	private static final HashMap<String, Long> tempBannedPlayers = ConfigManager.loadBannedPlayers();
	public static void tempBanPlayer(ServerPlayerEntity player, long durationInMinutes) {
		String playerName = player.getName().getString();
		long banEndTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(durationInMinutes);
		tempBannedPlayers.put(playerName, banEndTime);
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
	public static boolean isBanned(String playerName) {
		return tempBannedPlayers.containsKey(playerName);
	}
	public static void unbanPlayer(String playerName) {
		if (tempBannedPlayers.containsKey(playerName)) {
			tempBannedPlayers.remove(playerName);
			ConfigManager.saveBannedPlayers(tempBannedPlayers);
		}
	}
}
