package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.lwjgl.stb.STBTTFontinfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.ByteBuffer;

@Mixin(TrueTypeGlyphProvider.class)
public interface TrueTypeFontAccessor {
	@Accessor("fontMemory")
	@Mutable
	void setBuffer(ByteBuffer thing);

	@Accessor("font")
	STBTTFontinfo getInfo();

	@Accessor("font")
	@Mutable
	void setInfo(STBTTFontinfo thing);

	@Accessor
	float getOversample();

	@Accessor
	@Mutable
	void setOversample(float thing);

	@Accessor("skip")
	IntSet getExcludedCharacters();

	@Accessor("skip")
	@Mutable
	void setExcludedCharacters(IntSet thing);

	@Accessor
	float getShiftX();

	@Accessor
	@Mutable
	void setShiftX(float thing);

	@Accessor
	float getShiftY();

	@Accessor
	@Mutable
	void setShiftY(float thing);

	@Accessor("pointScale")
	float getScaleFactor();

	@Accessor("pointScale")
	@Mutable
	void setScaleFactor(float thing);

	@Accessor
	float getAscent();

	@Accessor
	@Mutable
	void setAscent(float thing);


}
