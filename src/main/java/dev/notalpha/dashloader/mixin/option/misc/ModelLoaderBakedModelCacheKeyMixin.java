package dev.notalpha.dashloader.mixin.option.misc;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ModelBakery.BakedCacheKey.class, priority = 999)
public class ModelLoaderBakedModelCacheKeyMixin {
	@Shadow
	@Final
	private ResourceLocation id;

	@Shadow
	@Final
	private boolean isUvLocked;

	@Shadow
	@Final
	private Transformation transformation;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ModelLoaderBakedModelCacheKeyMixin that)) return false;

		if (isUvLocked != that.isUvLocked) return false;
		if (!id.equals(that.id)) return false;
		return transformation.equals(that.transformation);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + transformation.hashCode();
		result = 31 * result + (isUvLocked ? 1 : 0);
		return result;
	}
}
