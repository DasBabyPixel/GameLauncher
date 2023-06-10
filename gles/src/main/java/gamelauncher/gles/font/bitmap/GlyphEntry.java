/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.font.bitmap;

public class GlyphEntry {

    public final GlyphKey key;
    public GlyphData data;
    public byte[] pixels;

    public GlyphEntry(GlyphData data, GlyphKey key, byte[] pixels) {
        this.data = data;
        this.key = key;
        this.pixels = pixels;
    }
}
