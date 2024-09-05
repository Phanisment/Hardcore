package io.phanisment.hardcore;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerDeathCallback;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main implements ModInitializer {
	private static final long TEMP_BAN_DURATION = 20 * 60 * 1000;
	private static final Path BAN_CONFIG_PATH = Paths.get("config", "tempban.json");
	private final Map<String, Long> bannedPlayers = new HashMap<>();

	@Override
	public void onInitialize() {
		loadBannedPlayers();
		
		PlayerDeathCallback.EVENT.register((player, source) -> {
			String playerName = player.getEntityName();
			bannedPlayers.put(playerName, System.currentTimeMillis() + TEMP_BAN_DURATION);
			saveBannedPlayers();
			player.networkHandler.disconnect(Text.literal("You have been banned for 20 minutes!"));
		});
		
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((server, player, killedEntity, damageSource) -> {
			if (player instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				checkBanStatus(server, serverPlayer);
			}
		});
		
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			long currentTime = System.currentTimeMillis();
			bannedPlayers.entrySet().removeIf(entry -> {
				if (entry.getValue() <= currentTime) {
					server.getPlayerManager().broadcast(Text.literal(entry.getKey() + " is now unbanned."), false);
					return true;
				}
				return false;
			});
			saveBannedPlayers();
		});
	}
	
	private void checkBanStatus(MinecraftServer server, ServerPlayerEntity player) {
		String playerName = player.getEntityName();
		if (bannedPlayers.containsKey(playerName)) {
			long banExpiry = bannedPlayers.get(playerName);
			if (banExpiry > System.currentTimeMillis()) {
				player.networkHandler.disconnect(Text.literal("You are still banned!"));
			} else {
				bannedPlayers.remove(playerName);
				saveBannedPlayers();
			}
		}
	}

	private void loadBannedPlayers() {
		try {
			if (Files.exists(BAN_CONFIG_PATH)) {
				List<String> lines = Files.readAllLines(BAN_CONFIG_PATH);
				for (String line : lines) {
					String[] parts = line.split(",");
					String playerName = parts[0];
					long banExpiry = Long.parseLong(parts[1]);
					bannedPlayers.put(playerName, banExpiry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveBannedPlayers() {
		try {
			List<String> lines = new ArrayList<>();
			for (Map.Entry<String, Long> entry : bannedPlayers.entrySet()) {
				lines.add(entry.getKey() + "," + entry.getValue());
			}
			Files.write(BAN_CONFIG_PATH, lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
