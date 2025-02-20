package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemOverrides.class)
public interface ItemOverridesListAccessor {


	@Invoker("<init>")
	static ItemOverrides newModelOverrideList() {
		throw new AssertionError();
	}

	@Accessor
	ItemOverrides.BakedOverride[] getOverrides();

	@Accessor
	@Mutable
	void setOverrides(ItemOverrides.BakedOverride[] overrides);

	@Accessor
	ResourceLocation[] getProperties();

	@Accessor
	@Mutable
	void setProperties(ResourceLocation[] conditionTypes);
}
