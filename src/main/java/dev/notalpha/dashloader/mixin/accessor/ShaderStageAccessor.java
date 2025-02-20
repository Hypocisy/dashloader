package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Program.class)
public interface ShaderStageAccessor {
	@Invoker("<init>")
	static Program create(Program.Type shaderType, int shaderRef, String name) {
		throw new AssertionError();
	}

	@Accessor
	Program.Type getType();

	@Accessor("id")
	int getGlRef();

	@Mixin(Program.Type.class)
	interface TypeAccessor {
		@Accessor
		int getGlType();
	}
}

