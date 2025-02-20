package dev.notalpha.dashloader.client.model;

import com.mojang.datafixers.util.Pair;
import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryAddException;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.dashloader.mixin.accessor.ModelLoaderAccessor;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModelModule implements DashModule<ModelModule.Data> {
	public static final CachingData<HashMap<ResourceLocation, BakedModel>> MODELS_SAVE = new CachingData<>(CacheStatus.SAVE);
	public static final CachingData<HashMap<ResourceLocation, UnbakedBakedModel>> MODELS_LOAD = new CachingData<>(CacheStatus.LOAD);
	public static final CachingData<HashMap<BlockState, ResourceLocation>> MISSING_READ = new CachingData<>();
	public static final CachingData<HashMap<BakedModel, Pair<List<Condition>, StateDefinition<Block, BlockState>>>> MULTIPART_PREDICATES = new CachingData<>(CacheStatus.SAVE);

	public static StateDefinition<Block, BlockState> getStateManager(ResourceLocation identifier) {
		StateDefinition<Block, BlockState> staticDef = ModelLoaderAccessor.getStaticDefinitions().get(identifier);
		if (staticDef != null) {
			return staticDef;
		} else {
			return ForgeRegistries.BLOCKS.getValue(identifier).getStateDefinition();
		}
	}

	@NotNull
	public static ResourceLocation getStateManagerIdentifier(StateDefinition<Block, BlockState> stateManager) {
		// Static definitions like itemframes.
		for (Map.Entry<ResourceLocation, StateDefinition<Block, BlockState>> entry : ModelLoaderAccessor.getStaticDefinitions().entrySet()) {
			if (entry.getValue() == stateManager) {
				return entry.getKey();
			}
		}

		return ForgeRegistries.BLOCKS.getKey(stateManager.getOwner());
	}

	@Override
	public void reset(Cache cache) {
		MODELS_SAVE.reset(cache, new HashMap<>());
		MODELS_LOAD.reset(cache, new HashMap<>());
		MISSING_READ.reset(cache, new HashMap<>());
		MULTIPART_PREDICATES.reset(cache, new HashMap<>());
	}

	@Override
	public Data save(RegistryWriter factory, StepTask task) {
		var models = MODELS_SAVE.get(CacheStatus.SAVE);

		if (models == null) {
			return null;
		} else {
			var outModels = new IntIntList(new ArrayList<>(models.size()));
			var missingModels = new IntIntList();

			final HashSet<ResourceLocation> out = new HashSet<>();
			task.doForEach(models, (identifier, bakedModel) -> {
				if (bakedModel != null) {
					try {
						final int add = factory.add(bakedModel);
						outModels.put(factory.add(identifier), add);
						out.add(identifier);
					} catch (RegistryAddException ignored) {
						// Fallback is checked later with the blockstates missing.
					}
				}
			});


			// Check missing models for blockstates.
			for (Block block : ForgeRegistries.BLOCKS.getValues()) {
				block.getStateDefinition().getPossibleStates().forEach((blockState) -> {
					final ModelResourceLocation modelId = BlockModelShaper.stateToModelLocation(blockState);
					if (!out.contains(modelId)) {
						missingModels.put(factory.add(blockState), factory.add(modelId));
					}
				});
			}

			return new Data(outModels, missingModels);
		}
	}

	@Override
	public void load(Data data, RegistryReader reader, StepTask task) {
		final HashMap<ResourceLocation, UnbakedBakedModel> out = new HashMap<>(data.models.list().size());
		data.models.forEach((key, value) -> {
			Dazy<? extends BakedModel> model = reader.get(value);
			ResourceLocation identifier = reader.get(key);
			out.put(identifier, new UnbakedBakedModel(model));
		});

		var missingModelsRead = new HashMap<BlockState, ResourceLocation>();
		data.missingModels.forEach((blockState, modelId) -> {
			missingModelsRead.put(reader.get(blockState), reader.get(modelId));
		});

		DashLoader.LOG.info("Found {} Missing BlockState Models", missingModelsRead.size());
		MISSING_READ.set(CacheStatus.LOAD, missingModelsRead);
		MODELS_LOAD.set(CacheStatus.LOAD, out);
	}

	@Override
	public Class<Data> getDataClass() {
		return Data.class;
	}

	@Override
	public float taskWeight() {
		return 1000;
	}

	@Override
	public boolean isActive() {
		return ConfigHandler.optionActive(Option.CACHE_MODEL_LOADER);
	}

	public static final class Data {
		public final IntIntList models; // identifier to model list
		public final IntIntList missingModels;

		public Data(IntIntList models, IntIntList missingModels) {
			this.models = models;
			this.missingModels = missingModels;
		}
	}
}
