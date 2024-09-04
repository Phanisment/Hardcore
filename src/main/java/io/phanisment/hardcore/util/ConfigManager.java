package io.phanisment.hardcore.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class ConfigManager {
	private static final File CONFIG_FILE = new File("config/tempban.json");
	private static final Gson GSON = new Gson();
	public static HashMap<UUID, Long> loadBannedPlayers() {
		if (!CONFIG_FILE.exists()) {
			return new HashMap<>();
		}
		try (Reader reader = new FileReader(CONFIG_FILE)) {
			Type type = new TypeToken<HashMap<UUID, Long>>(){}.getType();
			return GSON.fromJson(reader, type);
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
	public static void saveBannedPlayers(HashMap<UUID, Long> bannedPlayers) {
		try (Writer writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(bannedPlayers, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
