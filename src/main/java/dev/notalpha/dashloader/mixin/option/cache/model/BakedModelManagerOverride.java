package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@Shadow
	private Map<ResourceLocation, BakedModel> bakedRegistry;

	@Inject(method = "apply",
			at = @At(value = "TAIL")
	)

	private void yankAssets(ModelManager.ReloadState bakingResult, ProfilerFiller profiler, CallbackInfo ci) {
		ModelModule.MODELS_SAVE.visit(CacheStatus.SAVE, map -> {
			DashLoader.LOG.info("Yanking Minecraft Assets");
			map.putAll(this.bakedRegistry);
		});
	}

}
