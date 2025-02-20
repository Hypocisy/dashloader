package dev.notalpha.dashloader.mixin.accessor;

import dev.quantumfusion.hyphen.thr.HyphenException;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelBakery.class)
public interface ModelLoaderAccessor {

	@Accessor("ITEM_FRAME_FAKE_DEFINITION")
	static StateDefinition<Block, BlockState> getTheItemFrameThing() {
		throw new HyphenException("froge", "your dad");
	}

	@Accessor("STATIC_DEFINITIONS")
	static Map<ResourceLocation, StateDefinition<Block, BlockState>> getStaticDefinitions() {
		throw new HyphenException("froge", "your dad");
	}
}
