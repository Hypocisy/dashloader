package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {

	@Accessor("originalImage")
	NativeImage getImage();

	@Accessor("animatedTexture")
	SpriteContents.AnimatedTexture getAnimation();

	@Accessor("byMipLevel")
	NativeImage[] getMipmapLevelsImages();

	@Accessor("name")
	@Mutable
	void setId(ResourceLocation id);

	@Accessor
	@Mutable
	void setWidth(int width);

	@Accessor
	@Mutable
	void setHeight(int height);

	@Accessor("originalImage")
	@Mutable
	void setImage(NativeImage image);

	@Accessor("byMipLevel")
	@Mutable
	void setMipmapLevelsImages(NativeImage[] mipmapLevelsImages);

	@Accessor("animatedTexture")
	@Mutable
	void setAnimation(SpriteContents.AnimatedTexture animation);

}
