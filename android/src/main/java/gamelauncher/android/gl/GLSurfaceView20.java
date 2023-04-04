package gamelauncher.android.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

public class GLSurfaceView20 extends GLSurfaceView {
    private GLRenderer renderer;

    public GLSurfaceView20(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(renderer = new GLRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
