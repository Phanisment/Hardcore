package io.phanisment.hardcore.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class ConfigManager {
	private static final File CONFIG_FILE = new File("config/tempbans.json");

	public static HashMap<String, Long> loadBannedPlayers() {
		HashMap<String, Long> bannedPlayers = new HashMap<>();
		if (CONFIG_FILE.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(":");
					if (parts.length == 2) {
						String playerName = parts[0];
						long banEndTime = Long.parseLong(parts[1]);
						bannedPlayers.put(playerName, banEndTime);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bannedPlayers;
	}

	public static void saveBannedPlayers(HashMap<String, Long> bannedPlayers) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
			for (Map.Entry<String, Long> entry : bannedPlayers.entrySet()) {
				writer.write(entry.getKey() + ":" + entry.getValue());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
