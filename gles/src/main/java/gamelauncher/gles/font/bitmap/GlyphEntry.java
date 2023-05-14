package gamelauncher.gles.font.bitmap;

import java.nio.ByteBuffer;

public class GlyphEntry {

    public final GlyphKey key;
    public GlyphData data;
    public ByteBuffer buffer;

    public GlyphEntry(GlyphData data, GlyphKey key, ByteBuffer buffer) {
        this.data = data;
        this.key = key;
        this.buffer = buffer;
    }
}
