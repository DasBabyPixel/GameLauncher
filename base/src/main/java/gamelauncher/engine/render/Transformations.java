package gamelauncher.engine.render;

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
	}

//	public static interface Transformation {
//
//		public static class BasicTransformation implements Transformations {
//			public final float offsetX, offsetY, offsetZ;
//			public final float rotationX, rotationY, rotationZ;
//			public final float scaleX, scaleY, scaleZ;
//
//			public BasicTransformation(float offsetX, float offsetY, float offsetZ, float rotationX, float rotationY,
//					float rotationZ, float scaleX, float scaleY, float scaleZ) {
//				this.offsetX = offsetX;
//				this.offsetY = offsetY;
//				this.offsetZ = offsetZ;
//				this.rotationX = rotationX;
//				this.rotationY = rotationY;
//				this.rotationZ = rotationZ;
//				this.scaleX = scaleX;
//				this.scaleY = scaleY;
//				this.scaleZ = scaleZ;
//			}
//		}
//	}
}
