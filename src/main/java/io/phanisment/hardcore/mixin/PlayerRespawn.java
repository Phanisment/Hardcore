package io.phanisment.hardcore.mixin;

import net.minecraft.util.ActionResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.phanisment.hardcore.Main;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerRespawn {
	private static final ConcurrentHashMap<UUID, Long> bannedPlayers = new ConcurrentHashMap<>();

	@Inject(method = "copyFrom", at = @At("RETURN"))
	private void onPlayerRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		UUID playerUUID = player.getUuid();
		long banEndTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20);
		bannedPlayers.put(playerUUID, banEndTime);
		player.networkHandler.disconnect(Text.of("You have been banned for 20 minutes due to respawn."));
	}
}
