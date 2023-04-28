/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.os.Build;
import de.dasbabypixel.annotations.Api;
import gamelauncher.android.gl.supported.SupportedAndroidGLES20;
import gamelauncher.android.gl.supported.SupportedAndroidGLES30;
import gamelauncher.android.gl.supported.SupportedAndroidGLES31;
import gamelauncher.android.gl.supported.SupportedAndroidGLES32;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.gl.GLES32;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES30;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES31;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES32;

public class AndroidGLLoader {

    private final GLES20 gles20;
    private final GLES30 gles30;
    private final GLES31 gles31;
    private final GLES32 gles32;

    public AndroidGLLoader() {
        gles20 = new SupportedAndroidGLES20();
        gles30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? new SupportedAndroidGLES30() : new UnsupportedGLES30(gles20);
        gles31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new SupportedAndroidGLES31() : new UnsupportedGLES31(gles30);
        gles32 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new SupportedAndroidGLES32() : new UnsupportedGLES32(gles31);
    }

    @Api public GLES32 gles() {
        return gles32;
    }

    @Api public GLES32 gles32() {
        return gles32;
    }

    @Api public GLES31 gles31() {
        return gles31;
    }

    @Api public GLES30 gles30() {
        return gles30;
    }

    @Api public GLES20 gles20() {
        return gles20;
    }
}
