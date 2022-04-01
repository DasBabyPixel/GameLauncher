package gamelauncher.lwjgl.render;

import java.awt.Color;

import gamelauncher.engine.render.DrawContext;

public class LWJGLDrawContext implements DrawContext {

	private final double tx, ty, tz;
	private final double sx, sy, sz;

	public LWJGLDrawContext() {
		this(0, 0, 0, 1, 1, 1);
	}

	public LWJGLDrawContext(double tx, double ty, double tz, double sx, double sy, double sz) {
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
	}

	@Override
	public void drawRect(double x, double y, double w, double h, Color color) {

	}

	@Override
	public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		
	}

	@Override
	public DrawContext translate(double x, double y, double z) {
		return new LWJGLDrawContext(tx + x, ty + y, tz + z, sx, sy, sz);
	}

	@Override
	public DrawContext scale(double x, double y, double z) {
		return new LWJGLDrawContext(tx, ty, tz, sx * x, sy * y, sz * z);
	}
}
