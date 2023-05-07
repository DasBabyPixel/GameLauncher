package gamelauncher.engine.render;

/**
 * @author DasBabyPixel
 */
public class EmptyCamera implements Camera {

    private static final EmptyCamera instance = new EmptyCamera();

    /**
     * @return the instance
     */
    public static final EmptyCamera instance() {
        return instance;
    }

    private EmptyCamera() {
    }

    @Override public void setPosition(float x, float y, float z) {
        throw new UnsupportedOperationException();
    }

    @Override public void movePosition(float offsetX, float offsetY, float offsetZ) {
        throw new UnsupportedOperationException();
    }

    @Override public float x() {
        return 0;
    }

    @Override public float y() {
        return 0;
    }

    @Override public float z() {
        return 0;
    }

    @Override public float rotX() {
        return 0;
    }

    @Override public float rotY() {
        return 0;
    }

    @Override public float rotZ() {
        return 0;
    }

    @Override public void rotation(float rx, float ry, float rz) {
        throw new UnsupportedOperationException();
    }

    @Override public void rotX(float rx) {
        throw new UnsupportedOperationException();
    }

    @Override public void rotY(float ry) {
        throw new UnsupportedOperationException();
    }

    @Override public void rotZ(float rz) {
        throw new UnsupportedOperationException();
    }

    @Override public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        throw new UnsupportedOperationException();
    }

}
