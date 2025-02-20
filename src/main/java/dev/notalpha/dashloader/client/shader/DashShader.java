package dev.notalpha.dashloader.client.shader;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.ShaderProgramAccessor;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.*;

public final class DashShader implements DashObject<ShaderInstance, ShaderInstance> {
	public final Map<String, Sampler> samplers;
	public final String name;
	public final DashGlBlendState blendState;
	public final List<String> attributeNames;
	public final DashShaderStage vertexShader;
	public final DashShaderStage fragmentShader;
	public final int format;
	public final List<DashGlUniform> uniforms;
	public final List<String> samplerNames;
	public transient ShaderInstance toApply;

	public DashShader(Map<String, Sampler> samplers, String name, DashGlBlendState blendState, List<String> attributeNames, DashShaderStage vertexShader, DashShaderStage fragmentShader, int format, List<DashGlUniform> uniforms, List<String> samplerNames) {
		this.samplers = samplers;
		this.name = name;
		this.blendState = blendState;
		this.attributeNames = attributeNames;
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		this.format = format;
		this.uniforms = uniforms;
		this.samplerNames = samplerNames;
	}

	public DashShader(ShaderInstance shader, RegistryWriter writer) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) shader;

		this.samplers = new LinkedHashMap<>();
		shaderAccess.getSamplers().forEach((s, o) -> this.samplers.put(s, new Sampler(o)));
		this.name = shader.getName();

		this.blendState = new DashGlBlendState(shaderAccess.getBlendState());
		this.attributeNames = shaderAccess.getAttributeNames();
		this.vertexShader = new DashShaderStage(shader.getVertexProgram());
		this.fragmentShader = new DashShaderStage(shader.getFragmentProgram());
		this.format = writer.add(shader.getVertexFormat());
		this.uniforms = new ArrayList<>();
		Map<String, Uniform> loadedUniforms = shaderAccess.getLoadedUniforms();
		shaderAccess.getUniforms().forEach((glUniform) -> {
			this.uniforms.add(new DashGlUniform(glUniform, loadedUniforms.containsKey(glUniform.getName())));
		});
		this.samplerNames = shaderAccess.getSamplerNames();
	}


	@Override
	public ShaderInstance export(RegistryReader reader) {
		this.toApply = UnsafeHelper.allocateInstance(ShaderInstance.class);
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
		//object init
		shaderAccess.setLoadedSamplerIds(new ArrayList<>());
		shaderAccess.setLoadedUniformIds(new ArrayList<>());
		shaderAccess.setLoadedAttributeIds(new ArrayList<>());

		shaderAccess.setSamplerNames(new ArrayList<>(this.samplerNames));

		//<init> top
		shaderAccess.setName(this.name);
		shaderAccess.setFormat(reader.get(this.format));


		//JsonHelper.getArray(jsonObject, "samplers", (JsonArray)null)
		var samplersOut = new HashMap<String, Object>();
		this.samplers.forEach((s, o) -> samplersOut.put(s, o.sampler));
		shaderAccess.setSamplers(samplersOut);

		// JsonHelper.getArray(jsonObject, "attributes", (JsonArray)null);
		shaderAccess.setAttributeNames(new ArrayList<>(this.attributeNames));

		final ArrayList<Uniform> uniforms = new ArrayList<>();
		shaderAccess.setUniforms(uniforms);
		var uniformsOut = new HashMap<String, Uniform>();
		this.uniforms.forEach((dashGlUniform) -> {
			Uniform uniform = dashGlUniform.export(this.toApply);
			uniforms.add(uniform);
			if (dashGlUniform.loaded) {
				uniformsOut.put(dashGlUniform.name, uniform);
			}
		});
		shaderAccess.setLoadedUniforms(uniformsOut);


		// JsonHelper.getArray(jsonObject, "uniforms", (JsonArray)null);
		this.toApply.markDirty();
		this.toApply.MODEL_VIEW_MATRIX = uniformsOut.get("ModelViewMat");
		this.toApply.PROJECTION_MATRIX = uniformsOut.get("ProjMat");
		this.toApply.INVERSE_VIEW_ROTATION_MATRIX = uniformsOut.get("IViewRotMat");
		this.toApply.TEXTURE_MATRIX = uniformsOut.get("TextureMat");
		this.toApply.SCREEN_SIZE = uniformsOut.get("ScreenSize");
		this.toApply.COLOR_MODULATOR = uniformsOut.get("ColorModulator");
		this.toApply.LIGHT0_DIRECTION = uniformsOut.get("Light0_Direction");
		this.toApply.LIGHT1_DIRECTION = uniformsOut.get("Light1_Direction");
		this.toApply.FOG_START = uniformsOut.get("FogStart");
		this.toApply.FOG_END = uniformsOut.get("FogEnd");
		this.toApply.FOG_COLOR = uniformsOut.get("FogColor");
		this.toApply.FOG_SHAPE = uniformsOut.get("FogShape");
		this.toApply.LINE_WIDTH = uniformsOut.get("LineWidth");
		this.toApply.GAME_TIME = uniformsOut.get("GameTime");
		this.toApply.CHUNK_OFFSET = uniformsOut.get("ChunkOffset");
		return this.toApply;
	}


	@Override
	public void postExport(RegistryReader reader) {
		ShaderProgramAccessor shaderAccess = (ShaderProgramAccessor) this.toApply;
		shaderAccess.setBlendState(this.blendState.export());
		shaderAccess.setVertexShader(this.vertexShader.exportProgram());
		shaderAccess.setFragmentShader(this.fragmentShader.exportProgram());
		final List<Integer> loadedAttributeIds = shaderAccess.getLoadedAttributeIds();

		final int programId = GlStateManager.glCreateProgram();
		shaderAccess.setGlRef(programId);

		if (this.attributeNames != null) {
			ImmutableList<String> names = this.toApply.getVertexFormat().getElementAttributeNames();
			for (int i = 0; i < names.size(); i++) {
				String attributeName = names.get(i);
				Uniform.glBindAttribLocation(programId, i, attributeName);
				loadedAttributeIds.add(i);
			}
		}
		ProgramManager.linkShader(this.toApply);
		shaderAccess.loadref();
	}

	public static class Sampler {
		@DataNullable
		@DataSubclasses({Integer.class, String.class})
		public final Object sampler;

		public Sampler(Object sampler) {
			this.sampler = sampler;
		}
	}

}
