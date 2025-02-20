package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AndCondition.class)
public interface AndConditionAccessor {

	@Accessor("conditions")
	Iterable<? extends AndCondition> getConditions();

}
