package io.phanisment.hardcore.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class ExplosionBlocker {
	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void onUsePreventExplosion(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
		Block block = (Block) (Object) this;
		if (block instanceof BedBlock && (world.getRegistryKey() == World.NETHER || world.getRegistryKey() == World.END)) {
			ci.cancel();
			return;
		}
		if (block instanceof RespawnAnchorBlock && (world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.END)) {
			ci.cancel();
		}
	}
	@Inject(method = "onDestroyedByPlayer", at = @At("HEAD"), cancellable = true)
	private void onDestroyedByPlayerPreventExplosion(World world, BlockPos pos, CallbackInfo ci) {
		Block block = (Block) (Object) this;
		if (block instanceof BedBlock && (world.getRegistryKey() == World.NETHER || world.getRegistryKey() == World.END)) {
			ci.cancel();
			return;
		}
		if (block instanceof RespawnAnchorBlock && (world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.END)) {
			ci.cancel();
		}
	}
}
