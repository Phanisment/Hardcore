package io.phanisment.hardcore.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.BannedPlayerEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntity {
	@Inject(at = @At(value = "HEAD"), method = "onDeath", require = 1)
	private void onPlayerDeath(DamageSource source, CallbackInfo info) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		
	}
}