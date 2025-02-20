package dev.notalpha.dashloader.client.ui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.notalpha.dashloader.misc.HahaManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class DashToast implements Toast {
	private static final int PROGRESS_BAR_HEIGHT = 2;
	private static final int PADDING = 8;
	private static final int LINES = 125;
	private final Random random = new Random();
	private List<Line> lines = new ArrayList<>();

	@Nullable
	private final String fact = HahaManager.getFact();
	private long oldTime = System.currentTimeMillis();
	public final DashToastState state;

	private static void drawVertex(Matrix4f m4f, BufferBuilder bb, float z, float x, float y, Color color) {
		bb.vertex(m4f, x, y, z).color(color.red(), color.green(), color.blue(), color.alpha()).endVertex();
	}

	public int getWidth() {
		return 200;
	}

	public int getHeight() {
		return 40;
	}

	public DashToast() {
		this.state = new DashToastState();
		// Create lines
		for (int i = 0; i < LINES; i++) {
			this.lines.add(new Line());
		}
	}


	@Override
	public Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
		final int width = this.getWidth();
		final int height = this.getHeight();
		final int barY = height - PROGRESS_BAR_HEIGHT;

		// Get progress
		final float progress;
		final Color progressColor;
		if (state.getStatus() == DashToastStatus.CRASHED) {
			progress = (float) this.state.getProgress();
			progressColor = DrawerUtil.FAILED_COLOR;
		} else {
			progress = (float) this.state.getProgress();
			progressColor = DrawerUtil.getProgressColor(progress);
		}

		// Tick progress
		List<Line> newList = new ArrayList<>();
		List<Line> newListPrio = new ArrayList<>();
		long currentTime = System.currentTimeMillis();
		for (Line line : this.lines) {
			if (line.tick(width, height, progress, (currentTime - this.oldTime) / 17f)) {
				newListPrio.add(line);
			} else {
				newList.add(line);
			}
		}
		this.oldTime = currentTime;
		this.lines = newList;
		this.lines.addAll(newListPrio);


		// Setup scissor
		PoseStack poseStack = context.pose();
		{
			Vector4f vec = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
			vec.mul(poseStack.last().pose());
			Window window = manager.getMinecraft().getWindow();
			double scale = window.getGuiScale();
			RenderSystem.enableScissor(
					(int) (vec.x * scale),
					(int) (window.getHeight() - (vec.y * scale) - height * scale),
					(int) (width * scale),
					(int) (height * scale));
		}

		// Draw the ui
		DrawerUtil.drawRect(context, 0, 0, width, height, DrawerUtil.BACKGROUND_COLOR);

		// Draw the background lines.
		this.drawBatched(poseStack, (matrix4f, bufferBuilder) -> {
			for (Line line : lines) {
				line.draw(matrix4f, bufferBuilder);
			}
		});


		Font textRenderer = manager.getMinecraft().font;
		// Draw progress text
		String progressText = this.state.getProgressText();
		int progressTextY = this.fact != null ? barY - PADDING : (barY / 2) + (textRenderer.lineHeight / 2);
		DrawerUtil.drawText(context, textRenderer, PADDING, progressTextY, this.state.getText(), DrawerUtil.STATUS_COLOR);
		DrawerUtil.drawText(context, textRenderer, (width - PADDING) - textRenderer.self().width(progressText), progressTextY, progressText, DrawerUtil.STATUS_COLOR);

		if (this.fact != null) {
			// Draw the fun fact
			DrawerUtil.drawText(context, textRenderer, PADDING, textRenderer.lineHeight + PADDING, this.fact, DrawerUtil.FOREGROUND_COLOR);
		}

		// Draw progress bar
		DrawerUtil.drawRect(context, 0, barY, width, PROGRESS_BAR_HEIGHT, DrawerUtil.PROGRESS_TRACK);
		DrawerUtil.drawRect(context, 0, barY, (int) (width * progress), PROGRESS_BAR_HEIGHT, progressColor);

		// Epic rtx graphics. aka i slapped some glow on the things.
		this.drawBatched(poseStack, (matrix4f, bb) -> {
			// Line glow
			for (Line line : lines) {
				line.drawGlow(matrix4f, bb);
			}
			// Progress bar glow
			DrawerUtil.drawGlow(matrix4f, bb, 0, barY, (int) (width * progress), PROGRESS_BAR_HEIGHT, 0.75f, progressColor, true, true, true, true);
		});
		RenderSystem.disableScissor();

		if (state.getStatus() == DashToastStatus.CRASHED && System.currentTimeMillis() - state.getTimeDone() > 10000) {
			return Visibility.HIDE;
		}

		if (state.getStatus() == DashToastStatus.DONE && System.currentTimeMillis() - state.getTimeDone() > 2000) {
			return Visibility.HIDE;
		}
		return Visibility.SHOW;
	}

	private void drawBatched(PoseStack ms, BiConsumer<Matrix4f, BufferBuilder> consumer) {
		BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix = ms.last().pose();
		consumer.accept(matrix, bufferBuilder);
		BufferUploader.drawWithShader(bufferBuilder.end());
		RenderSystem.disableBlend();
	}


	private final class Line {
		public ColorKind colorKind;
		public float x;
		public float y;
		public int width;
		public int height;
		public float speedBoost;
		private Color color;

		public Line() {
			this.x = -1000;
			this.y = -1000;
			this.width = DashToast.this.random.nextInt(30, 50);
			this.height = DashToast.this.random.nextInt(2, 5);
			this.colorKind = ColorKind.Neutral;
			this.color = new Color(0xFF0000FF);
		}

		public boolean tick(int screenWidth, int screenHeight, float progress, float delta) {
			// Move the values
			this.x += (float) (speedBoost * (0.8 + (2.5 * progress))) * delta;


			// Check if not visible
			if (x > screenWidth || x + width < 0) {
				// Randomize position
				this.x = -width;
				this.y = screenHeight * DashToast.this.random.nextFloat();

				// Randomise color
				if (state.getStatus() == DashToastStatus.CRASHED) {
					if (DashToast.this.random.nextFloat() > 0.9 || this.colorKind == ColorKind.Progress) {
						this.colorKind = ColorKind.Crashed;
					}
				} else {
					if (DashToast.this.random.nextFloat() > 0.95) {
						this.colorKind = ColorKind.Progress;
					} else {
						this.colorKind = ColorKind.Neutral;
					}
				}

				// Randomise speed based on some values.
				// Weight (the size of the line), 0.2 deviation
				float weight = 1f - getWeight();
				float weightSpeed = (float) (0.7 + (weight * 0.6));

				// Kind (The type of line),
				float kindSpeed;
				if (this.colorKind == ColorKind.Neutral) {
					kindSpeed = (float) (1.0 + (DashToast.this.random.nextFloat() * 0.2f));
				} else {
					kindSpeed = (float) (1.0 + (DashToast.this.random.nextFloat() * 0.8f));
				}

				this.speedBoost = kindSpeed * weightSpeed;
				return this.colorKind != ColorKind.Neutral;
			}
			this.color = getColor(progress);

			return false;
		}

		public void draw(Matrix4f b4, BufferBuilder bb) {
			Color end = DrawerUtil.withOpacity(color, 0f);
			drawVertex(b4, bb, 0f, x + width, y, color); // right top
			drawVertex(b4, bb, 0f, x, y, end); // left top
			drawVertex(b4, bb, 0f, x, y + height, end); // left bottom
			drawVertex(b4, bb, 0f, x + width, y + height, color); // right bottom
		}


		public void drawGlow(Matrix4f b4, BufferBuilder bb) {
			if (this.colorKind != ColorKind.Neutral) {
				DrawerUtil.drawGlow(b4, bb, x, y, width, height, (getWeight() + 2.0f) / 3.0f, this.color, false, true, false, true);
			}
		}

		public Color getColor(double progress) {
			Color color = switch (this.colorKind) {
				case Neutral -> DrawerUtil.NEUTRAL_LINE;
				case Progress -> {
					if (state.getStatus() == DashToastStatus.CRASHED) {
						yield DrawerUtil.FAILED_COLOR;
					}

					yield DrawerUtil.getProgressColor(progress);
				}
				case Crashed -> DrawerUtil.FAILED_COLOR;
			};

			return DrawerUtil.withOpacity(color, Mth.clamp(((this.x) / (this.width)), 0.0f, 1.0f));
		}

		public float getWeight() {
			return ((this.width * (float) this.height) - 60f) / 190f;
		}
	}

	public enum ColorKind {
		Neutral,
		Progress,
		Crashed,
	}
}
