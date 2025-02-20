package dev.notalpha.dashloader.client;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheFactory;
import dev.notalpha.dashloader.client.blockstate.DashBlockState;
import dev.notalpha.dashloader.client.event.DashLoaderInitEvent;
import dev.notalpha.dashloader.client.font.*;
import dev.notalpha.dashloader.client.identifier.DashModelResourceLocation;
import dev.notalpha.dashloader.client.identifier.DashResourceLocation;
import dev.notalpha.dashloader.client.identifier.DashSpriteMaterial;
import dev.notalpha.dashloader.client.model.*;
import dev.notalpha.dashloader.client.model.components.DashBakedQuad;
import dev.notalpha.dashloader.client.model.components.DashBakedQuadCollection;
import dev.notalpha.dashloader.client.model.predicates.*;
import dev.notalpha.dashloader.client.shader.DashShader;
import dev.notalpha.dashloader.client.shader.DashVertexFormat;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import dev.notalpha.dashloader.client.splash.SplashModule;
import dev.notalpha.dashloader.client.sprite.DashImage;
import dev.notalpha.dashloader.client.sprite.DashSprite;
import dev.notalpha.dashloader.client.sprite.SpriteStitcherModule;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

//@Mod(DashLoader.MODID)
@Mod.EventBusSubscriber(modid = DashLoader.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DashLoaderClient {
	public static final Cache CACHE;
	public static boolean NEEDS_RELOAD = false;

	static {
		CacheFactory cacheManagerFactory = CacheFactory.create();

		CACHE = cacheManagerFactory.build(Path.of("./dashloader-cache/client/"));
	}

	@SubscribeEvent
	public void onDashLoaderInit(DashLoaderInitEvent event) {
		var factory = event.getCacheManagerFactory();
		factory.addModule(new FontModule());
		factory.addModule(new ModelModule());
		factory.addModule(new ShaderModule());
		factory.addModule(new SplashModule());
		factory.addModule(new SpriteStitcherModule());

		factory.addMissingHandler(
				ResourceLocation.class,
				(identifier, registryWriter) -> {
					if (identifier instanceof ModelResourceLocation m) {
						return new DashModelResourceLocation(m);
					} else {
						return new DashResourceLocation(identifier);
					}
				}
		);

		factory.addMissingHandler(
				TextureAtlasSprite.class,
				DashSprite::new
		);
		factory.addMissingHandler(
				Condition.class,
				(selector, writer) -> {
					if (selector == Condition.TRUE) {
						return new DashStaticPredicate(true);
					} else if (selector == Condition.FALSE) {
						return new DashStaticPredicate(false);
					} else if (selector instanceof AndCondition s) {
						return new DashAndPredicate(s, writer);
					} else if (selector instanceof OrCondition s) {
						return new DashOrPredicate(s, writer);
					} else if (selector instanceof KeyValueCondition s) {
						return new DashSimplePredicate(s);
					} else if (selector instanceof BooleanSelector s) {
						return new DashStaticPredicate(s.selector);
					} else {
						throw new RuntimeException("someone is having fun with lambda selectors again");
					}
				}
		);

		//noinspection unchecked
		for (Class<? extends DashObject<?, ?>> aClass : new Class[]{
				DashResourceLocation.class,
				DashModelResourceLocation.class,
				DashBasicBakedModel.class,
				DashBuiltinBakedModel.class,
				DashMultipartBakedModel.class,
				DashWeightedBakedModel.class,
				DashBakedQuad.class,
				DashBakedQuadCollection.class,
				DashSpriteMaterial.class,
				DashAndPredicate.class,
				DashOrPredicate.class,
				DashSimplePredicate.class,
				DashStaticPredicate.class,
				DashImage.class,
				DashSprite.class,
				DashBitmapFont.class,
				DashBlankFont.class,
				DashSpaceFont.class,
				DashTrueTypeFont.class,
				DashUnihexFont.class,
				DashBlockState.class,
				DashVertexFormat.class,
				DashShader.class
		}) {
			factory.addDashObject(aClass);
		}
	}
}
