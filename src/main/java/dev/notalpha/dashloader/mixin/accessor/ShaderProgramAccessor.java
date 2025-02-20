package dev.notalpha.dashloader.mixin.accessor;

import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(ShaderInstance.class)
public interface ShaderProgramAccessor {

	@Accessor("samplerMap")
	Map<String, Object> getSamplers();

	@Accessor("samplerMap")
	@Mutable
	void setSamplers(Map<String, Object> samplers);

	@Accessor("blend")
	BlendMode getBlendState();

	@Accessor("blend")
	@Mutable
	void setBlendState(BlendMode blendState);

	@Accessor("attributes")
	List<Integer> getLoadedAttributeIds();

	@Accessor("attributes")
	@Mutable
	void setLoadedAttributeIds(List<Integer> loadedAttributeIds);

	@Accessor("uniformMap")
	Map<String, Uniform> getLoadedUniforms();

	@Accessor("uniformMap")
	@Mutable
	void setLoadedUniforms(Map<String, Uniform> loadedUniforms);

	@Accessor("uniforms")
	List<Uniform> getUniforms();

	@Accessor
	@Mutable
	void setUniforms(List<Uniform> uniforms);

	@Accessor
	List<String> getAttributeNames();

	@Accessor
	@Mutable
	void setAttributeNames(List<String> attributeNames);

	@Accessor
	List<String> getSamplerNames();

	@Accessor
	@Mutable
	void setSamplerNames(List<String> samplerNames);

	@Accessor("samplerLocations")
	@Mutable
	void setLoadedSamplerIds(List<Integer> loadedSamplerIds);

	@Accessor("uniformLocations")
	@Mutable
	void setLoadedUniformIds(List<Integer> loadedUniformIds);

	@Accessor("programId")
	@Mutable
	void setGlRef(int glRef);

	@Accessor
	@Mutable
	void setName(String name);

	@Accessor("vertexProgram")
	@Mutable
	void setVertexShader(Program vertexShader);

	@Accessor("fragmentProgram")
	@Mutable
	void setFragmentShader(Program fragmentShader);

	@Accessor("vertexFormat")
	@Mutable
	void setFormat(VertexFormat format);

	@Invoker("updateLocations")
	void loadref();
}


