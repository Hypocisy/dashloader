package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(FontSet.class)
public interface FontStorageAccessor {
	@Accessor
	void setMissingGlyph(BakedGlyph renderer);

	@Accessor
	void setWhiteGlyph(BakedGlyph renderer);

	@Accessor
	CodepointMap<BakedGlyph> getGlyphs();

	@Accessor
	CodepointMap<FontSet.GlyphInfoFilter> getGlyphInfos();

	@Accessor
	Int2ObjectMap<IntList> getGlyphsByWidth();

	@Accessor
	List<GlyphProvider> getProviders();

	@Invoker("stitch")
	BakedGlyph stitch(SheetGlyphInfo c);

	@Invoker
	void callCloseProviders();

	@Invoker
	void callCloseTextures();
}
