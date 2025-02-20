package dev.notalpha.dashloader.client.model.fallback;

import dev.notalpha.dashloader.client.Dazy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * An unbaked model which holds a baked model, used for fallback to reuse cached models.
 */
public class UnbakedBakedModel implements UnbakedModel {
	private final Dazy<? extends BakedModel> bakedModel;

	public UnbakedBakedModel(Dazy<? extends BakedModel> bakedModel) {
		this.bakedModel = bakedModel;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return List.of();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelLoader) {
	}

	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer, ResourceLocation modelId) {
		return this.bakedModel.get(textureGetter);
	}
}
