open module gamelauncher.lwjgl {
	requires org.lwjgl.glfw;
	requires org.jetbrains.annotations;
	requires org.joml;
	requires org.lwjgl.opengl;
	requires org.lwjgl.opengles;
	requires io.netty.buffer;
	requires de.dasbabypixel.property;
	requires io.netty.handler;
	requires org.lwjgl.stb;
	requires com.google.gson;
	requires gamelauncher.base;

	requires org.lwjgl.glfw.natives;
	requires org.lwjgl.natives;
	requires org.lwjgl.opengl.natives;
	requires org.lwjgl.opengles.natives;
	requires org.lwjgl.stb.natives;

}
