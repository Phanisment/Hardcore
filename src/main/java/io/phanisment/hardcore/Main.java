package io.phanisment.hardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerRespawnCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("hardcore");
	private static final ConcurrentHashMap<UUID, Long> bannedPlayers = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		PlayerRespawnCallback.EVENT.register((player, alive) -> {
			if (player instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				UUID playerUUID = serverPlayer.getUuid();
				long banEndTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20);
				bannedPlayers.put(playerUUID, banEndTime);
				serverPlayer.networkHandler.disconnect(Text.of("You have been banned for 20 minutes due to respawn."));
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		/* ServerCommandManager.DISPATCHER.register(
			CommandManager.literal("unban")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("player", EntityArgumentType.player())
					.executes(context -> {
						ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
						UUID playerUUID = player.getUuid();
						bannedPlayers.remove(playerUUID);
						context.getSource().sendFeedback(Text.of("Player " + player.getEntityName() + " has been unbanned."), true);
						return 1;
					})
				)
		);*/

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