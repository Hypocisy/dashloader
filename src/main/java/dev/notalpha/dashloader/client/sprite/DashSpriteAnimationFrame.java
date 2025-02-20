package dev.notalpha.dashloader.client.sprite;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.mixin.accessor.SpriteAnimationFrameAccessor;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;

import java.util.List;

public final class DashSpriteAnimationFrame implements DashObject<SpriteContents.FrameInfo, SpriteContents.FrameInfo> {
	public final int index;
	public final int time;

	public DashSpriteAnimationFrame(int index, int time) {
		this.index = index;
		this.time = time;
	}

	public DashSpriteAnimationFrame(SpriteContents.FrameInfo animationFrame) {
		SpriteAnimationFrameAccessor access = ((SpriteAnimationFrameAccessor) animationFrame);
		this.index = access.getIndex();
		this.time = access.getTime();
	}

	@Override
	public SpriteContents.FrameInfo export(RegistryReader exportHandler) {
		return SpriteAnimationFrameAccessor.newSpriteFrame(this.index, this.time);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashSpriteAnimationFrame that = (DashSpriteAnimationFrame) o;

		if (index != that.index) return false;
		return time == that.time;
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + time;
		return result;
	}
}
