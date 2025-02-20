package dev.notalpha.dashloader;

import com.mojang.logging.LogUtils;
import dev.notalpha.dashloader.io.Serializer;
import dev.notalpha.dashloader.io.data.CacheInfo;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;

@Mod(DashLoader.MODID)
public final class DashLoader {
	public static final Logger LOG = LogUtils.getLogger();
	public static final Serializer<CacheInfo> METADATA_SERIALIZER = new Serializer<>(CacheInfo.class);
	public static final String MOD_HASH;
	public static final String MODID = "dashloader";
	private static final String VERSION = ModList.get().getModContainerById(MODID).map(container -> container.getModInfo().getVersion().toString()).orElse("unknown version");

	static {
		ArrayList<IModInfo> versions = new ArrayList<>();
		versions.addAll(ModList.get().getMods());

		versions.sort(Comparator.comparing(IModInfo::getModId));

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < versions.size(); i++) {
			IModInfo modInfo = versions.get(i);
			stringBuilder.append(i).append("$").append(modInfo.getModId()).append('&').append(modInfo.getVersion().toString());
		}

		MOD_HASH = DigestUtils.md5Hex(stringBuilder.toString()).toUpperCase();
	}

	public DashLoader(ModContainer container) {

		LOG.info("Initializing DashLoader " + VERSION + ".");
		if (!FMLEnvironment.production) {
			LOG.warn("DashLoader launched in dev.");
		}
	}

	@SuppressWarnings("EmptyMethod")
	public static void bootstrap() {
	}
}
