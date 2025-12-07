package com.github.olypolyu.betterwands.gui.components;

import com.github.olypolyu.betterwands.gui.interfaces.GuiInput;
import com.github.olypolyu.betterwands.gui.interfaces.GuiWidget;
import com.github.olypolyu.betterwands.util.Area2d;
import com.github.olypolyu.betterwands.util.Signal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DyeColor;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExpandableSelector<T> extends BaseElement implements GuiWidget, GuiInput {

	public final int marginPx = 2;
	public final int titleHeightPx = 12;
	public final int entryHeightPx = 8;

	public final Function<T, String> headingProvider;

	public final Signal<T> entrySelected = new Signal<>();

	public final String titleKey;
	public boolean isExpanded = true;

	protected final List<T> entries;

	public ExpandableSelector(
		String titleKey,
		int width,
		List<T> entries,
		Function<T, String> headingProvider
	) {
		this.titleKey = titleKey;
		this.entries = entries;
		this.headingProvider = headingProvider;
		this.width = width;
	}

	@Override
	public int getHeight() {
		int elementAmount = 0;
		if (entries != null) elementAmount = entries.size();

		return titleHeightPx + marginPx + (!isExpanded ? 0 : (entryHeightPx + marginPx) * elementAmount);
	}

	protected final int titleArea = (int) (titleHeightPx + (marginPx * 1.5F));

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseKey) {
		if (
			this.y < mouseY && mouseY < (this.y + titleArea) &&
			this.x < mouseX && mouseX < (this.x + getWidth())
		) {
			this.isExpanded = !this.isExpanded;
			Minecraft.getMinecraft().sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
			return;
		}

		if (this.isExpanded) {
			for (Area2dWithEntry area2dWithEntry : makeBoxes()) {
				if (area2dWithEntry.pointIntersects(mouseY, mouseX)) {
					entrySelected.emit(area2dWithEntry.entry);
					Minecraft.getMinecraft().sndManager.playSound("random.click", SoundCategory.GUI_SOUNDS, 1.0F, 1.0F);
					break;
				}
			}
		}
	}

	protected class Area2dWithEntry extends Area2d {
		protected final T entry;

		protected Area2dWithEntry(T entry, int x, int y, int w, int h) {
			super(x, y, w, h);
			this.entry = entry;
		}
	}

	protected List<Area2dWithEntry> makeBoxes() {
		List<Area2dWithEntry> list = new ArrayList<>();

		int localY = titleArea;
		for (T entry : entries) {
			list.add(
				new Area2dWithEntry(entry, this.getX(), this.getY() + localY - marginPx/2, getWidth(), entryHeightPx + marginPx/2)
			);
			localY += entryHeightPx + marginPx;
		}

		return list;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(Gui gui, int mouseX, int mouseY, float partialTick) {
		Minecraft mc = Minecraft.getMinecraft();

		mc.textureManager.loadTexture(	"/assets/betterwands/textures/gui/buttons.png").bind();

		//gui.drawBox(this.x, this.y, this.x + width, this.y+this.getHeight(), DyeColor.PURPLE.color.value, 2);

		gui.drawTexturedModalRect(
			this.getX() + 1, this.getY() + 3,
			this.isExpanded && !this.entries.isEmpty() ? 0 : 9,0,
			9, 9
		);

		if (this.entries.isEmpty()) {
			gui.drawStringNoShadow(
				Minecraft.getMinecraft().font,
				this.titleKey,
				getX() + 12, getY() + 3,
				DyeColor.BLACK.color.value
			);
		}
		else {
			gui.drawString(
				Minecraft.getMinecraft().font,
				this.titleKey,
				getX() + 12, getY() + 3,
				DyeColor.WHITE.color.value
			);
		}

		if (entries.isEmpty()) {
			gui.drawRect(
				getX(),
				getY() + 1,
				getX() + getWidth(),
				getY() + titleArea,
				(DyeColor.GRAY.color.value & 0x00FFFFFF) + 0x80000000
			);
		}

		if (this.isExpanded) {
			// little line
			if (!this.entries.isEmpty()) {
				gui.drawRect(
					this.getX() + 4,
					getY() + titleArea,
					this.getX() + 6,
					this.getY() + this.getHeight() - 4,
					(DyeColor.GRAY.color.value & 0x00FFFFFF) + 0x80000000
				);
			}

			// selection box
			List<Area2dWithEntry> area2dWithEntries = this.makeBoxes();
			for (Area2dWithEntry area2dWithEntry : area2dWithEntries) {
				if (area2dWithEntry.pointIntersects(mouseY, mouseX)) {
					gui.drawRect(
						area2dWithEntry.x, area2dWithEntry.y -1,
						area2dWithEntry.x + area2dWithEntry.width,
						area2dWithEntry.y + area2dWithEntry.height + 2,
						(DyeColor.GRAY.color.value & 0x00FFFFFF) + 0x80000000
					);
				}
			}

			int localY = titleArea;
			for (T entry : entries) {
				gui.drawString(
					Minecraft.getMinecraft().font,
					headingProvider.apply(entry),
					getX() + 10, getY() + localY,
					DyeColor.WHITE.color.value
				);

				localY += entryHeightPx + marginPx;
			}
		}

		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
}
