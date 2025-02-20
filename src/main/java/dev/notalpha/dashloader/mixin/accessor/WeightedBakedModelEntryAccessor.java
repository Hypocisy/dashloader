package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WeightedEntry.Wrapper.class)
public interface WeightedBakedModelEntryAccessor {
	@Invoker("<init>")
	static WeightedEntry.Wrapper init(Object data, Weight weight) {
		throw new AssertionError();
	}
}
