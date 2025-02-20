package dev.notalpha.dashloader.mixin.option.cache.model;

import com.mojang.datafixers.util.Pair;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import dev.notalpha.dashloader.mixin.accessor.MultipartModelComponentAccessor;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(MultiPart.class)
public class MultipartUnbakedModelMixin {
	@Shadow
	@Final
	private List<Selector> selectors;

	@Shadow
	@Final
	private StateDefinition<Block, BlockState> definition;

	@Inject(
			method = "bake",
			at = @At(value = "RETURN"),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true
	)
	private void addPredicateInfo(ModelBaker pBaker, Function<Material, TextureAtlasSprite> pSpriteGetter, ModelState pState, ResourceLocation pLocation, CallbackInfoReturnable<BakedModel> cir, MultiPartBakedModel.Builder builder) {
		ModelModule.MULTIPART_PREDICATES.visit(CacheStatus.SAVE, map -> {
			var bakedModel = builder.build();
			var outSelectors = new ArrayList<Condition>();
			this.selectors.forEach(multipartModelComponent -> outSelectors.add(((MultipartModelComponentAccessor) multipartModelComponent).getCondition()));
			map.put(bakedModel, Pair.of(outSelectors, this.definition));
			cir.setReturnValue(bakedModel);
		});
	}

}
