package gamelauncher.engine.render;

@SuppressWarnings("javadoc")
public interface Transformations {

    public static interface Projection {

        public static class Projection3D implements Projection {
            public final float fov, zNear, zFar;

            public Projection3D(float fov, float zNear, float zFar) {
                this.fov = fov;
                this.zNear = zNear;
                this.zFar = zFar;
            }
        }

        public static class Projection2D implements Projection {

        }
    }

    public static interface View {

        public static class CameraView implements View {
            public final Camera camera;

            public CameraView(Camera camera) {
                this.camera = camera;
            }
        }
    }
}
