package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker;

import com.github.olypolyu.betterwands.gui.components.ExpandableSelector;
import com.github.olypolyu.betterwands.gui.components.ScrollableSurface;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.MobPickerEntry;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.MobPickerCategory;
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
import net.minecraft.core.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScreenMobPicker extends Screen {

	protected int xSize = 244;
	protected int ySize = 177;

	protected boolean shouldClose = false;
	protected final ItemStack wand;

	static private final I18n translator = I18n.getInstance();

	public ScreenMobPicker(ItemStack itemStack) {
		super();

		this.wand = itemStack;

		// MobPickerEntry.getEntryFor((Class<? extends Mob>) EntityDispatcher.classForId(wand.getData().getString("monster")))

		try { this.mob = (Mob)  }
		catch (Exception ignored) {}
	}

	public String searchText = "";

	ButtonElement btn;
	TextFieldElement searchField;

	ScrollableSurface<ExpandableSelector<MobPickerEntry>> expandableSelectorScrollableSurface;

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);

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
			}
		);

		expandableSelectorScrollableSurface = new ScrollableSurface<>(
			MobPickerCategory.allCategories.stream().map(this::makeSelectorForCategory).collect(Collectors.toList())
		);

		btn = new ButtonElement(0, 226, 6, 12, 20, "X");
	}

	protected boolean filterEntriesBySearch(MobPickerEntry entry) {
		return searchText.isEmpty() || entry.getMobName(translator).toLowerCase().contains(searchText);
	}

	protected ExpandableSelector<MobPickerEntry> makeSelectorForCategory(MobPickerCategory category) {
		return new ExpandableSelector<>(
			category.name, 106,
			() -> category.getEntries().stream().filter(this::filterEntriesBySearch).collect(Collectors.toList()),
			it -> it.getMobName(translator)
		);
	}

	@Override
	public void removed() {
		super.removed();

		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my) {
		if (eventKey == Keyboard.KEY_ESCAPE || ( !searchField.isFocused && (eventKey == Keyboard.KEY_RETURN || eventKey == Keyboard.KEY_NUMPADENTER))) {
			shouldClose = true;
		}

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
	}

	@Override
	public void mouseReleased(int mx, int my, int buttonNum) {
		super.mouseReleased(mx, my, buttonNum);
	}

	@Override
	public void tick() {
		super.tick();

		expandableSelectorScrollableSurface.tick();

//		int selected = list.getOutput();
//		if (selected != selectedLast) {
//			selectedLast = selected;
//			try {mob = mobs.get(list.getOutput()).getConstructor(World.class).newInstance((Object) null);}
//			catch (Exception ignored) {}
//		}

		searchField.updateCursorCounter();
		searchField.xPosition = (this.width - this.xSize) / 2 + 129;
		searchField.yPosition = (this.height - this.ySize) / 2 + 7;

		btn.xPosition = (this.width - this.xSize) / 2 + 226;
		btn.yPosition = (this.height - this.ySize) / 2 + 6;

		if (shouldClose) {
			this.mc.displayScreen(null);
			if (mob != null) this.wand.getData().putString("monster", EntityDispatcher.classToIdMap.get(mob.getClass()).toString());
		}
	}

	Mob mob;
	int selectedLast = -1;

	@Override
	public void render(int mx, int my, float partialTick) {
		super.render(mx, my, partialTick);

		GL11.glEnable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.textureManager.loadTexture("/assets/betterwands/textures/gui/summoning_wand.png").bind();

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		//list.render(x+130, y+30, mx, my);

		if (mob != null) {
			renderMob(mob, x + 7, y + 13, mx, my, partialTick, 60F);
		}

		searchField.drawTextBox();
		btn.drawButton(mc, mx, my);

		expandableSelectorScrollableSurface.render(this, mx, my, partialTick);

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
