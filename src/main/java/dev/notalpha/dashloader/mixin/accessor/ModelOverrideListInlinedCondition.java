package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemOverrides.PropertyMatcher.class)
public interface ModelOverrideListInlinedCondition {


	@Invoker("<init>")
	static ItemOverrides.PropertyMatcher newModelOverrideListInlinedCondition(int index, float threshold) {
		throw new AssertionError();
	}

}
