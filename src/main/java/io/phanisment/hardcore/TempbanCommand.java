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
		dispatcher.register(
			literal("tempban")
				.then(CommandManager.argument("player", StringArgumentType.string())
				.then(CommandManager.argument("minutes", IntegerArgumentType.integer(1))
				.executes(context -> {
					String playerName = StringArgumentType.getString(context, "player");
					int minutes = IntegerArgumentType.getInteger(context, "minutes");
					ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);
					if (player != null) {
						TempbanManager.tempBanPlayer(player, minutes);
						context.getSource().sendFeedback(Text.of("Player " + playerName + " has been temporarily banned for " + minutes + " minutes."), true);
						return 1;
					} else {
						context.getSource().sendError(Text.of("Player not found."));
						return 0;
					}
				})))
		);
	}
}
