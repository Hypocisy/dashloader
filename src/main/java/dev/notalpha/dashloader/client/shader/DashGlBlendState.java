package dev.notalpha.dashloader.client.shader;

import com.mojang.blaze3d.shaders.BlendMode;
import dev.notalpha.dashloader.mixin.accessor.GlBlendStateAccessor;

public final class DashGlBlendState {
	public final int srcRgb;
	public final int srcAlpha;
	public final int dstRgb;
	public final int dstAlpha;
	public final int mode;
	public final boolean separateBlend;
	public final boolean blendDisabled;

	public DashGlBlendState(
			int srcRgb, int srcAlpha, int dstRgb, int dstAlpha, int mode, boolean separateBlend, boolean blendDisabled) {
		this.srcRgb = srcRgb;
		this.srcAlpha = srcAlpha;
		this.dstRgb = dstRgb;
		this.dstAlpha = dstAlpha;
		this.mode = mode;
		this.separateBlend = separateBlend;
		this.blendDisabled = blendDisabled;
	}


	public DashGlBlendState(GlBlendStateAccessor blendStateAccess) {
		this(
				blendStateAccess.getSrcRgb(),
				blendStateAccess.getSrcAlpha(),
				blendStateAccess.getDstRgb(),
				blendStateAccess.getDstAlpha(),
				blendStateAccess.getMode(),
				blendStateAccess.getSeparateBlend(),
				blendStateAccess.getBlendDisabled());
	}

	public DashGlBlendState(BlendMode blendState) {
		this((GlBlendStateAccessor) blendState);
	}

	public BlendMode export() {
		return GlBlendStateAccessor.create(this.separateBlend, this.blendDisabled, this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha, this.mode);
	}
}
