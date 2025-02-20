package dev.notalpha.dashloader.mixin.accessor;


import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BuiltInModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltInModel.class)
public interface BuiltinBakedModelAccessor {

	@Accessor("itemTransforms")
	ItemTransforms getTransformation();

	@Accessor("overrides")
	ItemOverrides getItemPropertyOverrides();

	@Accessor("particleTexture")
	TextureAtlasSprite getSprite();

	@Accessor("usesBlockLight")
	boolean getSideLit();
}

