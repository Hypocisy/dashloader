package dev.notalpha.dashloader.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import dev.notalpha.dashloader.io.IOHelper;
import dev.notalpha.dashloader.mixin.accessor.GlUniformAccessor;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.renderer.ShaderInstance;

public final class DashGlUniform {
	public final int dataType;
	public final boolean loaded;

	public final String name;
	public final int @DataNullable [] intData;

	public final float @DataNullable [] floatData;

	public DashGlUniform(int dataType, boolean loaded, String name, int[] intData, float[] floatData) {
		this.dataType = dataType;
		this.loaded = loaded;
		this.name = name;
		this.intData = intData;
		this.floatData = floatData;
	}

	public DashGlUniform(Uniform glUniform, boolean loaded) {
		GlUniformAccessor access = (GlUniformAccessor) glUniform;
		this.intData = IOHelper.toArray(access.getIntData());
		this.floatData = IOHelper.toArray(access.getFloatData());
		this.dataType = glUniform.getType();
		this.name = glUniform.getName();
		this.loaded = loaded;
	}


	public Uniform export(ShaderInstance shader) {
		Uniform glUniform = new Uniform(this.name, this.dataType, 0, shader);
		GlUniformAccessor access = (GlUniformAccessor) glUniform;
		access.setIntData(IOHelper.fromArray(this.intData));
		access.setFloatData(IOHelper.fromArray(this.floatData));
		return glUniform;
	}
}
