package gamelauncher.lwjgl.render.font.sdf;

public interface Shape {

	double MSDFGEN_CORNER_DOT_EPSILON = 0.000001;
	double MSDFGEN_DECONVERGENCE_FACTOR = 0.000001;


	class Bounds {

		public double l, r, t, b;

		public Bounds() {
		}

		public Bounds(double l, double r, double t, double b) {
			this.l = l;
			this.r = r;
			this.t = t;
			this.b = b;
		}
	}
}
