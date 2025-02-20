package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public interface BasicBakedModelAccessor {

	@Accessor("unculledFaces")
	List<BakedQuad> getQuads();

	@Accessor("culledFaces")
	Map<Direction, List<BakedQuad>> getFaceQuads();

	@Accessor("hasAmbientOcclusion")
	boolean getUsesAo();

	@Accessor("isGui3d")
	boolean getHasDepth();

	@Accessor("usesBlockLight")
	boolean getIsSideLit();

	@Accessor("particleIcon")
	TextureAtlasSprite getSprite();

	@Accessor("transforms")
	ItemTransforms getTransformation();

	@Accessor("overrides")
	ItemOverrides getItemPropertyOverrides();
}
