package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.mixin.accessor.ItemOverridesListAccessor;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.function.Function;

public final class DashModelOverrideList {
	public final DashModelOverrideListBakedOverride[] overrides;
	public final int[] conditionTypes; //identifiers

	public DashModelOverrideList(DashModelOverrideListBakedOverride[] overrides, int[] conditionTypes) {
		this.overrides = overrides;
		this.conditionTypes = conditionTypes;
	}

	public DashModelOverrideList(ItemOverrides modelOverrideList, RegistryWriter writer) {
		final ItemOverrides.BakedOverride[] overrides = ((ItemOverridesListAccessor) modelOverrideList).getOverrides();
		final ResourceLocation[] conditionTypes = ((ItemOverridesListAccessor) modelOverrideList).getProperties();

		this.overrides = new DashModelOverrideListBakedOverride[overrides.length];
		this.conditionTypes = new int[conditionTypes.length];

		for (int i = 0; i < overrides.length; i++) {
			this.overrides[i] = new DashModelOverrideListBakedOverride(overrides[i], writer);
		}

		for (int i = 0; i < conditionTypes.length; i++) {
			this.conditionTypes[i] = writer.add(conditionTypes[i]);
		}
	}

	public DazyImpl export(RegistryReader reader) {
		var conditionTypesOut = new ResourceLocation[this.conditionTypes.length];
		for (int i = 0; i < this.conditionTypes.length; i++) {
			conditionTypesOut[i] = reader.get(this.conditionTypes[i]);
		}

		var overridesOut = new DashModelOverrideListBakedOverride.DazyImpl[this.overrides.length];
		for (int i = 0; i < this.overrides.length; i++) {
			overridesOut[i] = this.overrides[i].export(reader);
		}

		return new DazyImpl(overridesOut, conditionTypesOut);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashModelOverrideList that = (DashModelOverrideList) o;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(overrides, that.overrides)) return false;
		return Arrays.equals(conditionTypes, that.conditionTypes);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(overrides);
		result = 31 * result + Arrays.hashCode(conditionTypes);
		return result;
	}

	public static class DazyImpl extends Dazy<ItemOverrides> {
		public final DashModelOverrideListBakedOverride.DazyImpl[] overrides;
		public final ResourceLocation[] conditionTypes; //identifiers

		public DazyImpl(DashModelOverrideListBakedOverride.DazyImpl[] overrides, ResourceLocation[] conditionTypes) {
			this.overrides = overrides;
			this.conditionTypes = conditionTypes;
		}

		@Override
		protected ItemOverrides resolve(Function<Material, TextureAtlasSprite> spriteLoader) {
			var out = ItemOverridesListAccessor.newModelOverrideList();
			ItemOverridesListAccessor access = (ItemOverridesListAccessor) out;

			var overridesOut = new ItemOverrides.BakedOverride[this.overrides.length];
			for (int i = 0; i < this.overrides.length; i++) {
				overridesOut[i] = this.overrides[i].get(spriteLoader);
			}
			access.setProperties(conditionTypes);
			access.setOverrides(overridesOut);
			return out;
		}
	}
}
