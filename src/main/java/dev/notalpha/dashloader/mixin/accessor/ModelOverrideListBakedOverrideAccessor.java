package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemOverrides.BakedOverride.class)
public interface ModelOverrideListBakedOverrideAccessor {
	@Invoker("<init>")
	static ItemOverrides.BakedOverride newItemOverridesBakedOverride(ItemOverrides.PropertyMatcher[] matchers, @Nullable BakedModel model) {
		throw new AssertionError();
	}

	@Accessor("matchers")
	ItemOverrides.PropertyMatcher[] getMatchers();

	@Accessor
	BakedModel getModel();

}
