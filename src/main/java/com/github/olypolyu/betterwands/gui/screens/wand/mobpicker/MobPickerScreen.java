package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker;

import com.github.olypolyu.betterwands.BetterWands;
import com.github.olypolyu.betterwands.gui.components.ExpandableSelector;
import com.github.olypolyu.betterwands.gui.components.ScrollBarVertical;
import com.github.olypolyu.betterwands.gui.components.VerticalLayout;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.MobPickerEntry;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.MobPickerCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.TextFieldElement;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Scissor;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MobPickerScreen extends Screen {

	protected int xSize = 244;
	protected int ySize = 177;

	protected boolean shouldClose = false;
	protected final ItemStack wand;

	static private final I18n translator = I18n.getInstance();

	public MobPickerScreen(ItemStack itemStack) {
		super();

		this.wand = itemStack;

		MobPickerEntry entry = MobPickerEntry.getEntryFor(wand.getData().getString("monster"));

		this.mob = entry != null ? entry.makeMob() : Minecraft.getMinecraft().thePlayer;
	}

	public String searchText = "";

	ButtonElement btn;
	TextFieldElement searchField;
	ScrollBarVertical scrollBarVertical;

	VerticalLayout<ExpandableSelector<MobPickerEntry>> selectorVerticalLayout;

	@Override
	public void removed() {
		super.removed();
		Keyboard.enableRepeatEvents(false);
	}

	protected int centerX() {
		return (this.width - this.xSize) /2;
	}

	protected int centerY() {
		return (this.height - this.ySize) /2;
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);

		ArrayList<ExpandableSelector<MobPickerEntry>> expandableSelectors = new ArrayList<>();

		for (MobPickerCategory category : MobPickerCategory.allCategories) {
			expandableSelectors.add(this.makeSelectorForCategory(category));
		}

		this.searchField = new TextFieldElement(
			this,
			this.font,
			129, 7,
			96, 18,
			searchText,
			I18n.getInstance().translateKey("gui.inventory.creative.label.search")
		);

		this.searchField.setMaxStringLength(14);
		this.searchField.setTextChangeListener(
			textFieldElement -> {
				searchText = textFieldElement.getText().toLowerCase();

				expandableSelectors.clear();

				for (MobPickerCategory category : MobPickerCategory.allCategories) {
					expandableSelectors.add(this.makeSelectorForCategory(category));
				}
			}
		);

		searchField.xPosition = centerX() + 129;
		searchField.yPosition = centerY() + 7;

		selectorVerticalLayout = new VerticalLayout<>(expandableSelectors);
		selectorVerticalLayout.setX(centerX() + 130);
		selectorVerticalLayout.setY(centerY() + 29);

		scrollBarVertical = new ScrollBarVertical(centerX() + 228, centerY() + 29, 140);
		scrollBarVertical.cursorMoved.connect(BetterWands.id("update_layout_pos"), this::onScrollCursorChanged);

		btn = new ButtonElement(0, 226, 6, 12, 20, "X");
		btn.xPosition = centerX() + 226;
		btn.yPosition = centerY() + 6;
	}

	protected boolean filterEntriesBySearch(MobPickerEntry entry) {
		return searchText.isEmpty() || entry.getMobName(translator).toLowerCase().contains(searchText);
	}

	protected ExpandableSelector<MobPickerEntry> makeSelectorForCategory(MobPickerCategory category) {
		ExpandableSelector<MobPickerEntry> selector = new ExpandableSelector<>(
			category.name, 98,
			category.getEntries().stream().filter(this::filterEntriesBySearch).collect(Collectors.toList()),
			it -> it.getMobName(translator)
		);

		selector.entrySelected.connect(BetterWands.id("mob_selected"), this::onSelected);
		return selector;
	}

	protected void onScrollCursorChanged(float cursorPercent) {
		selectorVerticalLayout.setY((int) (centerY() + 29 - (selectorVerticalLayout.getYDiff() * cursorPercent)));
	}

	protected void onSelected(MobPickerEntry entry) {
		mob = entry.makeMob();
	}

	@Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my) {
		if (eventKey == Keyboard.KEY_ESCAPE
			|| (!searchField.isFocused
				&& (eventKey == Keyboard.KEY_RETURN || eventKey == Keyboard.KEY_NUMPADENTER)
			)
		) { shouldClose = true; }

		searchField.textboxKeyTyped(eventCharacter, eventKey);
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonNum) {
		super.mouseClicked(mx, my, buttonNum);
		searchField.mouseClicked(mx, my, buttonNum);

		if (btn.mouseClicked(mc, mx, my)) {
			searchText = "";
			searchField.setText("");
		}

		selectorVerticalLayout.mouseClicked(mx, my, buttonNum);
		scrollBarVertical.mouseClicked(mx, my, buttonNum);
	}

	@Override
	public void mouseReleased(int mx, int my, int buttonNum) {
		super.mouseReleased(mx, my, buttonNum);
		if (buttonNum == 0) {
			scrollBarVertical.mouseReleased(mx, my);
		}
	}

	@Override
	public void tick() {
		super.tick();

		scrollBarVertical.tick();
		selectorVerticalLayout.tick();
		searchField.updateCursorCounter();

		if (shouldClose) {
			this.mc.displayScreen(null);
			if (mob != null) this.wand.getData().putString("monster", EntityDispatcher.classToIdMap.get(mob.getClass()).toString());
		}
	}

	Mob mob;

	@Override
	public void render(int mx, int my, float partialTick) {
		super.render(mx, my, partialTick);

		GL11.glEnable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.textureManager.loadTexture("/assets/betterwands/textures/gui/summoning_wand.png").bind();

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		if (mob != null) {
			renderMob(mob, x + 7, y + 13, mx, my, partialTick, 60F);
		}

		searchField.drawTextBox();
		btn.drawButton(mc, mx, my);

		Scissor.enable(centerX() + 130, centerY() + 30, 98, 140);
		selectorVerticalLayout.render(this, mx, my, partialTick);
		Scissor.disable();

		scrollBarVertical.render(this, mx, my, partialTick);
	}

	private void renderMob(Mob mob, int x, int y, int mouseX, int mouseY, float partialTicks, float scale) {
		float heightFactor = 27.777779F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Scissor.enable(x, y, 109, 157);

		GL11.glEnable(32826);
		GL11.glEnable(2903);
		GL11.glEnable(2929);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(x + 52), (float)(y + 44 + 96), 50.0F);

		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		Lighting.enableLight();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

		float offsetX = (float)(x + 34) - (float)mouseX;
		float offsetY = (float)(y + 44 + 32) - heightFactor * mob.bbHeight - (float)mouseY;

		GL11.glRotatef(-((float)Math.atan(offsetY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);

		mob.yBodyRot = (float)Math.atan(offsetX / 40.0F) * 20.0F;
		mob.yRot = (float)Math.atan(offsetX / 40.0F) * 40.0F;
		mob.xRot = -((float)Math.atan(offsetY / 40.0F)) * 20.0F;
		mob.entityBrightness = 1.0F;

		GL11.glTranslatef(0.0F, mob.heightOffset, 0.0F);
		EntityRenderDispatcher.instance.viewLerpYaw = 180.0F;
		EntityRenderDispatcher.instance.renderEntityPreviewWithPosYaw(Tessellator.instance, mob, 0f, 0.0F, 0.0F, 0.0F, 1.0F);

		GL11.glPopMatrix();
		Lighting.disable();
		GL11.glDisable(32826);
		GL11.glDisable(2929);
		Scissor.disable();
	}
}
