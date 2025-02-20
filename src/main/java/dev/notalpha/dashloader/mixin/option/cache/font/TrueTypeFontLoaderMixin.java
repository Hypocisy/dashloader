package dev.notalpha.dashloader.mixin.option.cache.font;

import com.mojang.blaze3d.font.GlyphProvider;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.font.FontModule;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.stb.STBTTFontinfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TrueTypeGlyphProviderDefinition.class)
public abstract class TrueTypeFontLoaderMixin {

	@Shadow
	public abstract ResourceLocation location();

	@Inject(
			method = "load",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/TextureUtil;readResource(Ljava/io/InputStream;)Ljava/nio/ByteBuffer;"),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void loadInject(ResourceManager manager, CallbackInfoReturnable<GlyphProvider> cir, STBTTFontinfo sTBTTFontinfo) {
		FontModule.FONT_TO_IDENT.visit(CacheStatus.SAVE, map -> {
			map.put(sTBTTFontinfo, location());
		});
	}
}
