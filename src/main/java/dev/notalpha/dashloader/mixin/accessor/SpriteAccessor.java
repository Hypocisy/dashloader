package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextureAtlasSprite.class)
public interface SpriteAccessor {

	@Invoker("<init>")
	static TextureAtlasSprite init(ResourceLocation atlasId, SpriteContents contents, int atlasWidth, int atlasHeight, int width, int height) {
		throw new AssertionError();
	}
}
