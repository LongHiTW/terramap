package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractLivingMarker extends AbstractMovingMarkers {
	
	protected static final ResourceLocation ENTITY_MARKERS_TEXTURE = new ResourceLocation(TerramapMod.MODID, "textures/gui/entity_markers.png");
	
	protected ResourceLocation texture;
	protected int u, v, textureWidth, textureHeight;
	protected Entity entity;
	protected double actualLongitude, actualLatitude;

	public AbstractLivingMarker(MarkerController<?> controller, int width, int height, ResourceLocation texture, int u, int v, int textureWidth, int textureHeight, Entity entity) {
		super(controller, width, height, 16, Integer.MAX_VALUE);
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.entity = entity;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		GlStateManager.color(1, 1, 1, 1);
		boolean drawName = hovered;
		if(parent instanceof MapWidget) {
			MapWidget map = (MapWidget) parent;
			drawName = drawName && !map.getContext().equals(MapContext.MINIMAP);
		}
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x +1, y +1, x + 1 + this.width, y + 1 + this.height, 0x50000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x, y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);

		if(drawName) {
			String name = this.entity.getDisplayName().getFormattedText();
			int strWidth = parent.getFont().getStringWidth(name);
			int nameY = y - parent.getFont().FONT_HEIGHT - 2;
			Gui.drawRect(x + this.width / 2 - strWidth / 2 - 2, y - parent.getFont().FONT_HEIGHT - 4, x + strWidth / 2 + this.width / 2 + 2, y - 1, 0x50000000);
			parent.getFont().drawCenteredString(x + this.width / 2, nameY, name, 0xFFFFFFFF, false);
		}
		
		GlStateManager.color(1, 1, 1, 1);
	}
	
	@Override
	public void onUpdate(Screen parent) {
		double x = this.entity.posX;
		double z = this.entity.posZ;
		double[] lola = {Double.NaN, Double.NaN};
		GeographicProjection proj = TerramapRemote.getRemote().getProjection();
		if(proj != null) {
			lola = proj.toGeo(x, z);
		}
		this.actualLongitude = lola[0];
		this.actualLatitude = lola[1];
		super.onUpdate(parent);
		if(this.entity.isDead) parent.scheduleForNextScreenUpdate(() -> parent.removeWidget(this));
	}

	@Override
	protected double getActualLongitude() {
		return this.actualLongitude;
	}

	@Override
	protected double getActualLatitude() {
		return this.actualLatitude;
	}
	
	@Override
	public int getDeltaX() {
		return -this.getWidth() / 2;
	}

	@Override
	public int getDeltaY() {
		return -this.getHeight() / 2;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	
	@Override
	public boolean canBeTracked() {
		return true;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return this.entity.getDisplayName();
	}
	
	@Override
	public String getIdentifier() {
		return this.getControllerId() + ":" + this.entity.getUniqueID().toString();
	}

}
