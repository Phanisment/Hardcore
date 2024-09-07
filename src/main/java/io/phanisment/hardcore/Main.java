package io.phanisment.hardcore;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main implements ModInitializer {
	public final HashMap<String, Long> ban = new HashMap<>();
	public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	@Override
	public void onInitialize() {
		// Command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("unban")
				.then(CommandManager.argument("player", StringArgumentType.word())
					.requires(source -> source.hasPermissionLevel(4))
					.executes(this::unbanCommand)
				)
			);
		});
		
		// Respwn Event
		ServerPlayerEvents.AFTER_RESPAWN.register((player, newplayer, alive) -> {
			if (player instanceof ServerPlayerEntity) {
				String playerName = player.getGameProfile().getName();
				player.networkHandler.disconnect(Text.literal("You temporary banned for 20 minutes due to death."));
				scheduler.schedule(() -> {
					this.ban(playerName);
				}, 1, TimeUnit.SECONDS);
			}
		});
		
		// Tick Event
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerManager().getPlayerList().forEach(player -> {
				if (player instanceof ServerPlayerEntity) {
					String playerName = player.getGameProfile().getName();
					if (ban.containsKey(playerName)) {
						long end = ban.get(playerName);
						if (System.currentTimeMillis() < end) {
							player.networkHandler.disconnect(Text.literal("Your ban season is not over yet, try another time again."));
						} else {
							player.sendMessage(Text.literal("Your ban season is over, you can play again"));
							ban.remove(playerName);
						}
					}
				}
			});
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			scheduler.shutdown();
		});
	}
	
	// Executor
	public int unbanCommand(CommandContext<ServerCommandSource> context) {
		String playerName = StringArgumentType.getString(context, "player");
		if (playerName.isEmpty()) {
			context.getSource().sendError(Text.literal("Player name cannot be empty!"));
		} else {
			ban.remove(playerName);
			context.getSource().sendMessage(Text.literal("Unbanned " + playerName + "!"));
		}
		return Command.SINGLE_SUCCESS;
	}
	
	// Code Shortcut
	public void ban(String player) {
		long duration = 20 * 60 * 1000;
		ban.put(player, System.currentTimeMillis() + duration);
	}
}
