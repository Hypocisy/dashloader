package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelResourceLocation.class)
public interface ModelIdentifierAccessor {
	@Invoker("<init>")
	static ModelResourceLocation init(String namespace, String path, String variant, @Nullable ResourceLocation.Dummy extraData) {
		throw new AssertionError();
	}
}
