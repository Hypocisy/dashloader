package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {

	@Accessor("blur")
	boolean getBilinear();

	@Accessor("blur")
	void setBilinear(boolean bilinear);

	@Accessor("mipmap")
	boolean getMipmap();

	@Accessor("mipmap")
	void setMipmap(boolean mipmap);
}
