// # 📄 Dosya Yolu: C:/Projects/TelefonRehberi/src/main/java/com/turkuazlabs/telefonrehberi/tools/ContactPhotoTool.java
// # 📌 Amac: Kisi profil fotografini guvenli boyuta indirger ve Swing icin goruntuye cevirir.
// # 📌 Tool - Java
// # Version: 2.8.0
// # Aciklama: PNG/JPEG girdisini 640 piksele kadar olceklendirip PNG byte dizisi olarak normalize eder.
// # Bagimli Oldugu Katman: Tool | Language
package com.turkuazlabs.telefonrehberi.tools;

import com.turkuazlabs.telefonrehberi.language.Messages;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ContactPhotoTool {
    private static final int MAX_DIMENSION = 640;

    public byte[] loadAndNormalize(Path path) {
        if (path == null) return null;
        try {
            byte[] source = Files.readAllBytes(path);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(source));
            if (image == null) throw new IllegalArgumentException(Messages.ERROR_PHOTO_FORMAT);
            return encodePng(scaleDown(image));
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_PHOTO_READ, exception);
        }
    }

    public BufferedImage decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException exception) {
            throw new IllegalStateException(Messages.ERROR_PHOTO_READ, exception);
        }
    }

    public Image scaledImage(byte[] bytes, int width, int height) {
        BufferedImage image = decode(bytes);
        if (image == null) return null;
        return image.getScaledInstance(Math.max(1, width), Math.max(1, height), Image.SCALE_SMOOTH);
    }

    private BufferedImage scaleDown(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        double factor = Math.min(1.0, Math.min((double) MAX_DIMENSION / width, (double) MAX_DIMENSION / height));
        int targetWidth = Math.max(1, (int) Math.round(width * factor));
        int targetHeight = Math.max(1, (int) Math.round(height * factor));
        if (targetWidth == width && targetHeight == height) return source;
        BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private byte[] encodePng(BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }
}
