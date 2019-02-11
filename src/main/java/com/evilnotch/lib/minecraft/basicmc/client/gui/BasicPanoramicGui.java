package com.evilnotch.lib.minecraft.basicmc.client.gui;

import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import com.evilnotch.lib.util.simple.ICopy;
import com.google.common.util.concurrent.Runnables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;

public class BasicPanoramicGui extends BasicGui implements ICopy{
	
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
    /**
     * A random number between 0.0 and 1.0, used to determine if the title screen says <a
     * href="https://minecraft.gamepedia.com/Menu_screen#Minceraft">Minceraft</a> instead of Minecraft. Set during
     * construction; if the value is less than .0001, then Minceraft is displayed.
     */
    public float minceraftRoll;
    /** The splash message. */
    public String splashText;
    /** Timer used to rotate the panorama, increases every tick. */
    public float panoramaTimer;
    /** Texture allocated for the current viewport of the main menu's panorama background. */
    public DynamicTexture viewportTexture;
    /** The Object object utilized as a thread lock when performing non thread-safe operations */
    public Object threadLock = new Object();
    public String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
    /** Width of openGLWarning2 */
    public int openGLWarning2Width;
    /** Width of openGLWarning1 */
    public int openGLWarning1Width;
    /** Left x coordinate of the OpenGL warning */
    public int openGLWarningX1;
    /** Top y coordinate of the OpenGL warning */
    public int openGLWarningY1;
    /** Right x coordinate of the OpenGL warning */
    public int openGLWarningX2;
    /** Bottom y coordinate of the OpenGL warning */
    public int openGLWarningY2;
    /** OpenGL graphics card warning. */
    public String openGLWarning1;
    /** OpenGL graphics card warning. */
    public String openGLWarning2;
    /** Link to the Mojang Support about minimum requirements */
    public String openGLWarningLink;
    public ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    public ResourceLocation logo = new ResourceLocation("textures/gui/title/minecraft.png");
    public ResourceLocation mcEdition = new ResourceLocation("textures/gui/title/edition.png");
    /** An array of all the paths to the panorama pictures. */
    public ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public ResourceLocation backgroundTexture;
    /** Minecraft Realms button. */
    public GuiBasicButton modButton;
    public int widthCopyright;
    public int widthCopyrightRest;
    public net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;

    /**
     * is static so objects are not constantly reading and writing to disk
     * To override simply create a new preferably static instance of list<String> and override the setter and getter for the splashes
     */
    public static List<String> default_splashes = null;

	public BasicPanoramicGui(ResourceLocation[] textures)
	{
		this();
		if(textures.length != 6)
			throw new IllegalArgumentException("Textures of panorama must be 6 create your own for more then 6");
		this.TITLE_PANORAMA_PATHS = textures;
	}
	
	public List<String> getSplashes(){return default_splashes;}
	public void setSplashes(List<String> list){default_splashes = list;}
	
    public BasicPanoramicGui()
    {
        this.openGLWarning2 = MORE_INFO_TEXT;
        this.splashText = "missingno";
        IResource iresource = null;
        if(getSplashes() == null)
        {
        	setSplashes(new ArrayList());
        	try
        	{
        		List<String> list = getSplashes();
        		iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
        		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
        		String s;

        		while ((s = bufferedreader.readLine()) != null)
        		{
        			s = s.trim();

        			if (!s.isEmpty())
        			{
        				list.add(s);
        			}
        		}
        	}
        	catch (IOException var8)
        	{
        		;
        	}
        	finally
        	{
        		IOUtils.closeQuietly((Closeable)iresource);
        	}
        }
    	List<String> list = getSplashes();
		if (!list.isEmpty())
		{
			while (true)
			{
				this.splashText = list.get(RANDOM.nextInt(list.size()));

				if (this.splashText.hashCode() != 125780783)
				{
					break;
				}
			}
		}

        this.minceraftRoll = RANDOM.nextFloat();
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())
        {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Is there currently a realms notification screen, and are realms notifications enabled?
     */
    public boolean areRealmsNotificationsEnabled()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen(){}

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException{}

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        this.widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.widthCopyrightRest = this.width - this.widthCopyright - 2;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int i = 24;
        int j = this.height / 4 + 48;

        synchronized (this.threadLock)
        {
            this.openGLWarning1Width = this.fontRenderer.getStringWidth(this.openGLWarning1);
            this.openGLWarning2Width = this.fontRenderer.getStringWidth(this.openGLWarning2);
            int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
            this.openGLWarningX1 = (this.width - k) / 2;
            if(this.buttonList.size() > 0)
            	this.openGLWarningY1 = (this.buttonList.get(0)).y - 24;
            this.openGLWarningX2 = this.openGLWarningX1 + k;
            this.openGLWarningY2 = this.openGLWarningY1 + 24;
        }

        this.mc.setConnectedToRealms(false);
        GuiButton b = getModButton();
        if(b != null)
        	modUpdateNotification = init(this,b);
    }
    /**
     * called for syncing things with forge
     */
    public GuiButton getModButton() {
		return null;
	}

	public static NotificationModUpdateScreen init(GuiScreen guiMainMenu, GuiButton modButton)
    {
        NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
        notificationModUpdateScreen.setGuiSize(guiMainMenu.width, guiMainMenu.height);
        notificationModUpdateScreen.initGui();
        return notificationModUpdateScreen;
    }

    /**
     * gui buttons must be BasicGuiButton unless you override this method
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
    	for(int i=0;i<this.buttonList.size();i++)
    	{
    		GuiBasicButton b = (GuiBasicButton) buttonList.get(i);
    		if(button.id == b.id)
    		{
    			b.actionPerformed();
    			break;
    		}
    	}
    }

    /**
     * have no clue what this does if you don't need this for your menu override it so it does nothing
     */
    public void confirmClicked(boolean result, int id)
    {
        if (result && id == 12)
        {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        }
        else if (id == 12)
        {
            this.mc.displayGuiScreen(this);
        }
        else if (id == 13)
        {
            if (result)
            {
                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke((Object)null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                }
                catch (Throwable throwable)
                {
                    LOGGER.error("Couldn't open link", throwable);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama(int mouseX, int mouseY, float partialTicks)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = 8;

        for (int j = 0; j < 64; ++j)
        {
            GlStateManager.pushMatrix();
            float f = ((float)(j % 8) / 8.0F - 0.5F) / 64.0F;
            float f1 = ((float)(j / 8) / 8.0F - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int k = 0; k < 6; ++k)
            {
                GlStateManager.pushMatrix();

                if (k == 1)
                {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (k == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (k == 3)
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (k == 4)
                {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (k == 5)
                {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[k]);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l = 255 / (j + 1);
                float f3 = 0.0F;
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
                bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
                bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox()
    {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GlStateManager.glTexParameteri(3553, 10241, 9729);
        GlStateManager.glTexParameteri(3553, 10240, 9729);
        GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i = 3;

        for (int j = 0; j < 3; ++j)
        {
            float f = 1.0F / (float)(j + 1);
            int k = this.width;
            int l = this.height;
            float f1 = (float)(j - 1) / 256.0F;
            bufferbuilder.pos((double)k, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos((double)k, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos(0.0D, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    /**
     * Renders the skybox in the main menu
     */
    private void renderSkybox(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(mouseX, mouseY, partialTicks);
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.rotateAndBlurSkybox();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f = 120.0F / (float)(this.width > this.height ? this.width : this.height);
        float f1 = (float)this.height * f / 256.0F;
        float f2 = (float)this.width * f / 256.0F;
        int i = this.width;
        int j = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos((double)i, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos((double)i, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.panoramaTimer += partialTicks;
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        int i = 274;
        int j = this.width / 2 - 137;
        int k = 30;
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(this.logo);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double)this.minceraftRoll < 1.0E-4D)
        {
            this.drawTexturedModalRect(j + 0, 30, 0, 0, 99, 44);
            this.drawTexturedModalRect(j + 99, 30, 129, 0, 27, 44);
            this.drawTexturedModalRect(j + 99 + 26, 30, 126, 0, 3, 44);
            this.drawTexturedModalRect(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
        }
        else
        {
            this.drawTexturedModalRect(j + 0, 30, 0, 0, 155, 44);
            this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
        }

        this.mc.getTextureManager().bindTexture(this.mcEdition);
        drawModalRectWithCustomSizedTexture(j + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);

        this.splashText = renderMainMenu(this, this.fontRenderer, this.width, this.height, this.splashText);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
        f = f * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
        GlStateManager.scale(f, f, f);
        this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, -256);
        GlStateManager.popMatrix();
        String s = "Minecraft 1.12.2";

        if (this.mc.isDemo())
        {
            s = s + " Demo";
        }
        else
        {
            s = s + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());
        }

        java.util.List<String> brandings = com.google.common.collect.Lists.reverse(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(true));
        for (int brdline = 0; brdline < brandings.size(); brdline++)
        {
            String brd = brandings.get(brdline);
            if (!com.google.common.base.Strings.isNullOrEmpty(brd))
            {
                this.drawString(this.fontRenderer, brd, 2, this.height - ( 10 + brdline * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
            }
        }

        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, -1);

        if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow())
        {
            drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
        }

        if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty())
        {
            drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
            this.drawString(this.fontRenderer, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
            this.drawString(this.fontRenderer, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, (this.buttonList.get(0)).y - 12, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        if(modUpdateNotification != null)
        	modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
    }

	/**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (this.threadLock)
        {
            if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && mouseX >= this.openGLWarningX1 && mouseX <= this.openGLWarningX2 && mouseY >= this.openGLWarningY1 && mouseY <= this.openGLWarningY2)
            {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
        if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height)
        {
            this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed(){}

    public static String renderMainMenu(GuiScreen gui, FontRenderer font, int width, int height, String splashText)
    {
        Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            String line = I18n.format("forge.update.beta.1", TextFormatting.RED, TextFormatting.RESET);
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (0 * (font.FONT_HEIGHT + 1)), -1);
            line = I18n.format("forge.update.beta.2");
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (1 * (font.FONT_HEIGHT + 1)), -1);
        }

        String line = null;
        switch(status)
        {
            //case FAILED:        line = " Version check failed"; break;
            //case UP_TO_DATE:    line = "Forge up to date"}; break;
            //case AHEAD:         line = "Using non-recommended Forge build, issues may arise."}; break;
            case OUTDATED:
            case BETA_OUTDATED: line = I18n.format("forge.update.newversion", ForgeVersion.getTarget()); break;
            default: break;
        }

        if (line != null)
        {
            // if we have a line, render it in the bottom right, above Mojang's copyright line
            gui.drawString(font, line, width - font.getStringWidth(line) - 2, height - (2 * (font.FONT_HEIGHT + 1)), -1);
        }

        return splashText;
    }

	@Override
	public ICopy copy() {
		return new BasicPanoramicGui(this.TITLE_PANORAMA_PATHS);
	}

}
