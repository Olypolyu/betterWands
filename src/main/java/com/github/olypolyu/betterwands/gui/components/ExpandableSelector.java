package com.github.olypolyu.betterwands.gui.components;

import com.github.olypolyu.betterwands.gui.interfaces.GuiWidget;
import com.github.olypolyu.betterwands.util.Signal;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.util.helper.DyeColor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExpandableSelector<T> extends BaseElement implements GuiWidget {

	public final int marginPx = 2;
	public final int titleHeightPx = 12;
	public final int entryHeightPx = 8;

	public final Supplier<List<T>> entriesSupplier;
	public final Function<T, String> headingProvider;

	public final Signal<T> entrySelected = new Signal<>();

	public final String titleKey;
	protected boolean isExpanded = false;

	protected @Nullable List<T> entries;

	public ExpandableSelector(
		String titleKey,
		int width,
		Supplier<List<T>> entries,
		Function<T, String> headingProvider
	) {
		this.titleKey = titleKey;
		this.entriesSupplier = entries;
		this.headingProvider = headingProvider;
		this.width = width;
	}

	@Override
	public int getHeight() {
		int elementAmount = 0;
		if (entries != null) elementAmount = entries.size();

		return titleHeightPx + (isExpanded ? 0 : (entryHeightPx + marginPx) * elementAmount + marginPx);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void render(Gui gui, int mouseX, int mouseY, float partialTick) {
		gui.drawBox(this.x, this.y, this.x + width, this.y+this.getHeight(), DyeColor.PURPLE.color.value, 2);
	}

	@Override
	public void tick() {
		entries = entriesSupplier.get();
	}
}
