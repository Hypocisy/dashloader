package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.HashMap;

@Mixin(value = GameRenderer.class, priority = 69)
public abstract class GameRendererMixin {
	@Redirect(
			method = "reloadShaders",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lnet/minecraft/client/renderer/ShaderInstance;"
			)
	)
	private ShaderInstance shaderCreation(ResourceProvider factory, String name, VertexFormat format) throws IOException {
		HashMap<String, ShaderInstance> shaders = ShaderModule.SHADERS.get(CacheStatus.LOAD);
		if (shaders != null) {
			// If we are reading from cache load the shader and check if its cached.
			var shader = shaders.get(name);
			if (shader != null) {
				return shader;
			}
		}

		ShaderInstance shader = new ShaderInstance(factory, name, format);
		ShaderModule.SHADERS.visit(CacheStatus.SAVE, map -> map.put(name, shader));
		return shader;
	}


}
