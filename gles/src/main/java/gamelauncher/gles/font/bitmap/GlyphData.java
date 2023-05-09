package gamelauncher.gles.font.bitmap;

public class GlyphData {

    public int width;
    public int height;
    public int advance;
    public int bearingX;
    public int bearingY;

    @Override public String toString() {
        return "GlyphData{" + "width=" + width + ", height=" + height + ", advance=" + advance + ", bearingX=" + bearingX + ", bearingY=" + bearingY + '}';
    }
}
