package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.shaders.BlendMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlendMode.class)
public interface GlBlendStateAccessor {

	@Invoker("<init>")
	static BlendMode create(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int mode) {
		throw new AssertionError();
	}

	@Accessor("srcColorFactor")
	int getSrcRgb();

	@Accessor("srcAlphaFactor")
	int getSrcAlpha();

	@Accessor("dstColorFactor")
	int getDstRgb();

	@Accessor("dstAlphaFactor")
	int getDstAlpha();

	@Accessor("blendFunc")
	int getMode();

	@Accessor("separateBlend")
	boolean getSeparateBlend();

	@Accessor("opaque")
	boolean getBlendDisabled();


}
