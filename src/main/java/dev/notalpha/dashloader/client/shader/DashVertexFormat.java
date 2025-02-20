package dev.notalpha.dashloader.client.shader;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.mixin.accessor.VertexFormatAccessor;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashVertexFormat implements DashObject<VertexFormat, VertexFormat> {
	public static final List<VertexFormat> BUILT_IN = new ArrayList<>();

	static {
		BUILT_IN.add(DefaultVertexFormat.BLIT_SCREEN);
		BUILT_IN.add(DefaultVertexFormat.BLOCK);
		BUILT_IN.add(DefaultVertexFormat.NEW_ENTITY);
		BUILT_IN.add(DefaultVertexFormat.PARTICLE);
		BUILT_IN.add(DefaultVertexFormat.POSITION);
		BUILT_IN.add(DefaultVertexFormat.POSITION_COLOR);
		BUILT_IN.add(DefaultVertexFormat.POSITION_COLOR_NORMAL);
		BUILT_IN.add(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
		BUILT_IN.add(DefaultVertexFormat.POSITION_TEX);
		BUILT_IN.add(DefaultVertexFormat.POSITION_COLOR_TEX);
		BUILT_IN.add(DefaultVertexFormat.POSITION_TEX_COLOR);
		BUILT_IN.add(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
		BUILT_IN.add(DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);
		BUILT_IN.add(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
	}

	@DataNullable
	public final Map<String, DashVertexFormatElement> elementMap;

	public final int builtin;

	public DashVertexFormat(Map<String, DashVertexFormatElement> elementMap, int builtin) {
		this.elementMap = elementMap;
		this.builtin = builtin;
	}

	public DashVertexFormat(VertexFormat vertexFormat) {
		int builtin = -1;
		for (int i = 0; i < BUILT_IN.size(); i++) {
			VertexFormat format = BUILT_IN.get(i);
			if (format == vertexFormat) {
				builtin = i;
				break;
			}
		}
		this.builtin = builtin;
		if (builtin == -1) {
			this.elementMap = new HashMap<>();
			((VertexFormatAccessor) vertexFormat).getElementMap().forEach((s, element) -> {
				this.elementMap.put(s, new DashVertexFormatElement(element));
			});
		} else {
			this.elementMap = null;
		}
	}

	@Override
	public VertexFormat export(RegistryReader reader) {
		if (this.builtin != -1) {
			return BUILT_IN.get(this.builtin);
		} else {
			ImmutableMap.Builder<String, VertexFormatElement> out = ImmutableMap.builderWithExpectedSize(elementMap.size());
			elementMap.forEach((s, dashVertexFormatElement) -> {
				VertexFormatElement export = dashVertexFormatElement.export(reader);
				out.put(s, export);
			});
			return new VertexFormat(out.build());
		}
	}
}
