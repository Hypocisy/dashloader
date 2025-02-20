package dev.notalpha.dashloader.client.sprite;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashTextureStitcher<T extends Stitcher.Entry> extends Stitcher<T> {
	@Nullable
	private ExportedData<T> data;
	private int remainingSlots;

	public DashTextureStitcher(int maxWidth, int maxHeight, int mipLevel, @Nullable ExportedData<T> data) {
		super(maxWidth, maxHeight, mipLevel);
		this.data = data;
		this.remainingSlots = data == null ? 0 : data.slots.size();
	}

	@Override
	public int getWidth() {
		if (this.data == null) {
			return super.getWidth();
		}
		return data.width;
	}

	@Override
	public int getHeight() {
		if (this.data == null) {
			return super.getHeight();
		}
		return data.height;
	}

	@Override
	public void registerSprite(T info) {
		if (data == null) {
			super.registerSprite(info);
			return;
		}

		// If it starts recaching, doRecache will re-add the entries to the list.
		var id = info.name();
		var slot = data.slots.get(id);
		if (slot == null) {
			DashLoader.LOG.warn("Sprite {} was not cached last time.", id);

			doFallback();
			// This was never added to the slot, so it would not get added to super.
			this.registerSprite(info);
			return;
		}

		if (slot.contents != null) {
			DashLoader.LOG.warn("Sprite {} was added twice??", id);
		}

		remainingSlots -= 1;
		slot.contents = info;

		if (slot.width != info.width() || slot.height != info.height()) {
			DashLoader.LOG.warn("Sprite {} had changed dimensions since last launch, falling back.", id);
			doFallback();
			return;
		}
	}

	public void doFallback() {
		if (data != null) {
			DashLoader.LOG.error("Using fallback on texture stitcher.");
			var slots = data.slots;
			data = null;
			slots.forEach((identifier, tDashTextureSlot) -> {
				if (tDashTextureSlot.contents != null) {
					this.registerSprite(tDashTextureSlot.contents);
				}
			});
		} else {
			DashLoader.LOG.error("Tried to fallback stitcher twice.");
		}
	}

	@Override
	public void stitch() {
		if (data != null && remainingSlots != 0) {
			DashLoader.LOG.warn("Remaining slots did not match the cached amount, Falling back.");
			data.slots.forEach((identifier, tDashTextureSlot) -> {
				if (tDashTextureSlot.contents == null) {
					DashLoader.LOG.error("Sprite {} was not requested", identifier);
				}
			});
			doFallback();
		}

		if (data == null) {
			super.stitch();
		}
	}

	@Override
	public void gatherSprites(SpriteLoader<T> consumer) {
		if (data == null) {
			super.gatherSprites(consumer);
		} else {
			data.slots.forEach((identifier, dashTextureSlot) -> {
				consumer.load(dashTextureSlot.contents, dashTextureSlot.x, dashTextureSlot.y);
			});
		}
	}

	public static class Data<T extends Stitcher.Entry> {
		public final IntObjectList<DashTextureSlot<T>> slots;
		public final int width;
		public final int height;

		public Data(IntObjectList<DashTextureSlot<T>> slots, int width, int height) {
			this.slots = slots;
			this.width = width;
			this.height = height;
		}

		public Data(RegistryWriter factory, Stitcher<T> stitcher) {
			this.slots = new IntObjectList<>();
			stitcher.gatherSprites((info, x, y) -> {
				this.slots.put(factory.add(info.name()), new DashTextureSlot<>(x, y, info.width(), info.height()));
			});
			this.width = stitcher.getWidth();
			this.height = stitcher.getHeight();
		}

		public ExportedData<T> export(RegistryReader reader) {
			var output = new HashMap<ResourceLocation, DashTextureSlot<T>>();
			this.slots.forEach((key, value) -> {
				output.put(reader.get(key), value);
			});

			return new ExportedData<>(
					output,
					width,
					height
			);
		}
	}

	public static class ExportedData<T extends Stitcher.Entry> {
		public final Map<ResourceLocation, DashTextureSlot<T>> slots;
		public final int width;
		public final int height;

		public ExportedData(Map<ResourceLocation, DashTextureSlot<T>> slots, int width, int height) {
			this.slots = slots;
			this.width = width;
			this.height = height;
		}
	}
}
