package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MultiPartBakedModel.class)
public interface MultipartBakedModelAccessor {

	@Accessor
	List<Pair<Predicate<BlockState>, BakedModel>> getSelectors();

	@Accessor
	@Mutable
	void setSelectors(List<Pair<Predicate<BlockState>, BakedModel>> components);

	@Accessor
	Map<BlockState, BitSet> getSelectorCache();

	@Accessor
	@Mutable
	void setSelectorCache(Map<BlockState, BitSet> stateBitSetMap);

	@Accessor
	@Mutable
	void setParticleIcon(TextureAtlasSprite sprite);
}
