package gamelauncher.gles.font.bitmap;

public class GlyphData {

    public int width;
    public int height;
    public float advance;
    public float bearingX;
    public float bearingY;

    @Override public String toString() {
        return "GlyphData{" + "width=" + width + ", height=" + height + ", advance=" + advance + ", bearingX=" + bearingX + ", bearingY=" + bearingY + '}';
    }
}
