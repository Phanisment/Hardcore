package io.phanisment.hardcore;

package com.example.tempban;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

import io.phanisment.hardcore.util.TempbanManager;

public class TempbanCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("tempban")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("player", StringArgumentType.string())
				.then(CommandManager.argument("duration", StringArgumentType.string())
				.executes(context -> {
					String playerName = StringArgumentType.getString(context, "player");
					String duration = StringArgumentType.getString(context, "duration");
					return 1;
				})))
		);
		
		dispatcher.register(CommandManager.literal("tunban")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("playerUUID", StringArgumentType.string())
				.executes(context -> {
					String playerUUIDString = StringArgumentType.getString(context, "playerUUID");
					try {
						UUID playerUUID = UUID.fromString(playerUUIDString);
						if (TempBanManager.isBanned(playerUUID)) {
							TempBanManager.unbanPlayer(playerUUID);
							context.getSource().sendFeedback(Text.of("Player unbanned successfully."), true);
						} else {
							context.getSource().sendError(Text.of("Player is not banned."));
						}
					} catch (IllegalArgumentException e) {
						context.getSource().sendError(Text.of("Invalid UUID format."));
					}
					return 1;
				}))
		);
	}
}
