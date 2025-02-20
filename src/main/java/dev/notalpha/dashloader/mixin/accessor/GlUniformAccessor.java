package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(Uniform.class)
public interface GlUniformAccessor {
	@Accessor("intValues")
	IntBuffer getIntData();

	@Accessor("intValues")
	@Mutable
	void setIntData(IntBuffer intData);

	@Accessor("floatValues")
	FloatBuffer getFloatData();

	@Accessor("floatValues")
	@Mutable
	void setFloatData(FloatBuffer floatData);

	@Accessor
	@Mutable
	void setName(String name);
}
