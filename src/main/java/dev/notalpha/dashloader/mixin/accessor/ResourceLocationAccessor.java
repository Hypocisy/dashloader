package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceLocation.class)
public interface ResourceLocationAccessor {

	@Invoker("<init>")
	static ResourceLocation init(String namespace, String path, @Nullable ResourceLocation.Dummy extraData) {
		throw new AssertionError();
	}
}
