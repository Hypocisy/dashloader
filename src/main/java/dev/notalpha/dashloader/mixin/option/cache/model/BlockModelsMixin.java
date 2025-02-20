package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class BlockModelsMixin {

	@Inject(
			method = "stateToModelLocation(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/ModelResourceLocation;",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private static void cacheModelId(BlockState state, CallbackInfoReturnable<ModelResourceLocation> cir) {
		ModelModule.MISSING_READ.visit(CacheStatus.LOAD, map -> {
			final ResourceLocation identifier = map.get(state);
			if (identifier != null) {
				cir.setReturnValue((ModelResourceLocation) identifier);
			}
		});
	}
}
