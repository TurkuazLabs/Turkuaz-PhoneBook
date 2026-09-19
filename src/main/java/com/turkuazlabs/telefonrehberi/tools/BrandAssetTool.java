// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/BrandAssetTool.java
// # 📌 Amac: Uygulama marka ikonunu yukler ve Light/Dark tema paletine uyumlu marka varyantlari uretir.
// # 📌 Tool - Java
// # Version: 1.1.0
// # Aciklama: JAR/portable PNG fallback yuklemesi yapar; seffaf padding'i kirpip marka rengini tema paletine tasir.
// # Bagimli Oldugu Katman: Tool | Config
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.config.AppConfig;
import com.turkuazlabs.telefonrehberi.config.ModernThemePalette;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class BrandAssetTool {
    private static final String CLASSPATH_ICON = "/assets/branding/app-icon-128.png";
    private static final Path FILE_ICON = AppConfig.APP_ROOT.resolve("assets/branding/app-icon-128.png");
    private static final int THEME_ICON_SIZE = 256;
    private static final int ALPHA_THRESHOLD = 8;

    public Optional<Image> loadAppIcon() {
        try (InputStream stream = BrandAssetTool.class.getResourceAsStream(CLASSPATH_ICON)) {
            if (stream != null) return Optional.ofNullable(ImageIO.read(stream));
        } catch (IOException ignored) { }
        if (Files.exists(FILE_ICON)) {
            try { return Optional.ofNullable(ImageIO.read(FILE_ICON.toFile())); }
            catch (IOException ignored) { return Optional.empty(); }
        }
        return Optional.empty();
    }

    public Image createThemeVariant(Image source, boolean dark) {
        if (source == null) return null;
        BufferedImage input = toBufferedImage(source);
        int minX=input.getWidth(), minY=input.getHeight(), maxX=-1, maxY=-1;
        for (int y=0;y<input.getHeight();y++) {
            for (int x=0;x<input.getWidth();x++) {
                int alpha=(input.getRGB(x,y)>>>24)&0xFF;
                if (alpha<=ALPHA_THRESHOLD) continue;
                minX=Math.min(minX,x); minY=Math.min(minY,y);
                maxX=Math.max(maxX,x); maxY=Math.max(maxY,y);
            }
        }
        if (maxX<minX || maxY<minY) return source;

        int cropWidth=maxX-minX+1, cropHeight=maxY-minY+1;
        BufferedImage normalized=new BufferedImage(THEME_ICON_SIZE,THEME_ICON_SIZE,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=normalized.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int margin=10, available=THEME_ICON_SIZE-(margin*2);
            double scale=Math.min((double)available/cropWidth,(double)available/cropHeight);
            int width=Math.max(1,(int)Math.round(cropWidth*scale));
            int height=Math.max(1,(int)Math.round(cropHeight*scale));
            int left=(THEME_ICON_SIZE-width)/2, top=(THEME_ICON_SIZE-height)/2;
            g.drawImage(input,left,top,left+width,top+height,minX,minY,maxX+1,maxY+1,null);
        } finally { g.dispose(); }

        Color accent=ModernThemePalette.brandAccent(dark);
        Color glyph=ModernThemePalette.brandGlyph(dark);
        float[] accentHsb=Color.RGBtoHSB(accent.getRed(),accent.getGreen(),accent.getBlue(),null);
        for (int y=0;y<normalized.getHeight();y++) {
            for (int x=0;x<normalized.getWidth();x++) {
                int argb=normalized.getRGB(x,y);
                int alpha=(argb>>>24)&0xFF;
                if (alpha<=ALPHA_THRESHOLD) continue;
                Color pixel=new Color(argb,true);
                float[] hsb=Color.RGBtoHSB(pixel.getRed(),pixel.getGreen(),pixel.getBlue(),null);
                Color themed;
                if (hsb[1]<0.16f && hsb[2]>0.68f) {
                    themed=glyph;
                } else {
                    float brightness=Math.max(0.22f,Math.min(1.0f,hsb[2]*(dark?1.02f:0.92f)));
                    int rgb=Color.HSBtoRGB(accentHsb[0],Math.max(0.58f,accentHsb[1]),brightness);
                    themed=new Color(rgb);
                }
                normalized.setRGB(x,y,(alpha<<24)|(themed.getRGB()&0x00FFFFFF));
            }
        }
        return normalized;
    }

    private BufferedImage toBufferedImage(Image source) {
        if (source instanceof BufferedImage buffered) return buffered;
        int width=Math.max(1,source.getWidth(null));
        int height=Math.max(1,source.getHeight(null));
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=image.createGraphics();
        try { g.drawImage(source,0,0,null); } finally { g.dispose(); }
        return image;
    }
}
