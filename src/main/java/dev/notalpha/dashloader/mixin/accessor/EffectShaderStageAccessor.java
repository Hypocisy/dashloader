package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EffectProgram.class)
public interface EffectShaderStageAccessor {
	@Invoker("<init>")
	static EffectProgram create(Program.Type shaderType, int shaderRef, String name) {
		throw new AssertionError();
	}
}
