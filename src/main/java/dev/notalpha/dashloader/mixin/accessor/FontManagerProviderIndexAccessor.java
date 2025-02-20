package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.Preparation.class)
public interface FontManagerProviderIndexAccessor {

	@Invoker("<init>")
	static FontManager.Preparation create(Map<ResourceLocation, List<GlyphProvider>> providers, List<GlyphProvider> allProviders) {
		throw new AssertionError();
	}

}
