package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteLoader.Preparations.class)
public interface SpriteLoaderStitchResultAccessor {

	@Accessor
	int getWidth();

	@Accessor
	int getHeight();

	@Accessor
	int getMipLevel();


}
