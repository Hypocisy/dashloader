package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mixin(value = ModelBakery.class, priority = 69420)
public abstract class ModelLoaderMixin {

	@Mutable
	@Shadow
	@Final
	private Map<ResourceLocation, UnbakedModel> unbakedCache;

	@Shadow
	protected abstract void lambda$new$7(BlockState blockState);

	@Mutable
	@Shadow
	@Final
	private Map<ResourceLocation, UnbakedModel> topLevelModels;

	@Shadow @Final private Map<ResourceLocation, BakedModel> bakedTopLevelModels;

	@Inject(
			method = "<init>",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=static_definitions", shift = At.Shift.AFTER)
	)
	private void injectLoadedModels(BlockColors blockColors, ProfilerFiller profiler, Map<ResourceLocation, BlockModel> jsonUnbakedModels, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStates, CallbackInfo ci) {
		ModelModule.MODELS_LOAD.visit(CacheStatus.LOAD, dashModels -> {
			int total = dashModels.size();
			this.unbakedCache.keySet().forEach(dashModels::remove);
			this.topLevelModels.keySet().forEach(dashModels::remove);
			DashLoader.LOG.info("Injecting {}/{} Cached Models", dashModels.size(), total);
			this.unbakedCache.putAll(dashModels);
			this.topLevelModels.putAll(dashModels);
		});
	}

	/**
	 * We want to not load all of the blockstate models as we have a list of them available on which ones to load to save a lot of computation
	 */
	@Redirect(
			method = "<init>",
			at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0)
	)
	private boolean loadMissingModels(Iterator<?> instance) {
		var map = ModelModule.MISSING_READ.get(CacheStatus.LOAD);
		if (map != null) {
			for (BlockState blockState : map.keySet()) {
				// load thing lambda
				this.lambda$new$7(blockState);
			}
			DashLoader.LOG.info("Loaded {} unsupported models.", map.size());
			return false;
		}
		return instance.hasNext();
	}

	@Inject(
			method = "bakeModels",
			at = @At(
					value = "HEAD"
			)
	)
	private void countModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> spriteLoader, CallbackInfo ci) {
		if (ModelModule.MODELS_LOAD.active(CacheStatus.LOAD)) {
			// Cache stats
			int cachedModels = 0;
			int fallbackModels = 0;
			for (UnbakedModel value : this.topLevelModels.values()) {
				if (value instanceof UnbakedBakedModel) {
					cachedModels += 1;
				} else {
					fallbackModels += 1;
				}
			}
			long totalModels = cachedModels + fallbackModels;
			DashLoader.LOG.info("{}% Cache coverage", (int) (((cachedModels / (float) totalModels) * 100)));
			DashLoader.LOG.info("with {} Fallback models", fallbackModels);
			DashLoader.LOG.info("and  {} Cached models", cachedModels);
		}

	}

	@Inject(
			method = "bakeModels",
			at = @At(
					value = "TAIL"
			)
	)
	private void debug(BiFunction<ResourceLocation, Material, TextureAtlasSprite> spriteLoader, CallbackInfo ci) {
//var models = new HashMap<Identifier, BakedModel>();
//this.bakedModels.forEach((identifier, bakedModel) -> {
//	if (
//			bakedModel.getClass() == BasicBakedModel.class ||
//			bakedModel.getClass() == MultipartBakedModel.class ||
//			bakedModel.getClass() == WeightedBakedModel.class ||
//					bakedModel.getClass() == BuiltinBakedModel.class
//	) {
//		return;
//	}
//
//	models.put(identifier, bakedModel);
//});
//		System.out.println();

//
		//String dump = ObjectDumper.dump(new ObjectDumper.Wrapper(models));
		//try {
		//	Files.writeString(Path.of("./output." + DashLoaderClient.CACHE.getStatus()), dump);
		//} catch (IOException e) {
		//	throw new RuntimeException(e);
		//}
	}


}
