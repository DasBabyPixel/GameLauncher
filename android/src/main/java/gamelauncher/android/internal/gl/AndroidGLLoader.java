/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.gl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import de.dasbabypixel.annotations.Api;
import gamelauncher.android.AndroidLauncher;
import gamelauncher.android.internal.gl.supported.SupportedAndroidGLES20;
import gamelauncher.android.internal.gl.supported.SupportedAndroidGLES30;
import gamelauncher.android.internal.gl.supported.SupportedAndroidGLES31;
import gamelauncher.android.internal.gl.supported.SupportedAndroidGLES32;
import gamelauncher.engine.util.service.ServiceReference;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.gl.GLES32;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES30;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES31;
import gamelauncher.gles.gl.unsupported.UnsupportedGLES32;

public class AndroidGLLoader {

    public static final ServiceReference<AndroidGLLoader> ANDROID_GL_LOADER = new ServiceReference<>(AndroidGLLoader.class);

    private final GLES20 gles20;
    private final GLES30 gles30;
    private final GLES31 gles31;
    private final GLES32 gles32;
    private final boolean hasGLES20;
    private final boolean hasGLES30;
    private final boolean hasGLES31;
    private final boolean hasGLES32;

    public AndroidGLLoader(AndroidLauncher activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        int gles = configurationInfo.reqGlEsVersion;
        hasGLES20 = gles >= 0x20000;
        hasGLES30 = gles >= 0x30000;
        hasGLES31 = gles >= 0x30001;
        hasGLES32 = gles >= 0x30002;
        if (!hasGLES20) throw new Error("No OpenGL ES 2.0");

        gles20 = new SupportedAndroidGLES20();
        gles30 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && hasGLES30 ? new SupportedAndroidGLES30() : new UnsupportedGLES30(gles20);
        gles31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && hasGLES31 ? new SupportedAndroidGLES31() : new UnsupportedGLES31(gles30);
        gles32 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && hasGLES32 ? new SupportedAndroidGLES32() : new UnsupportedGLES32(gles31);
    }

    @Api public boolean hasGLES20() {
        return hasGLES20;
    }

    @Api public boolean hasGLES30() {
        return hasGLES30;
    }

    @Api public boolean hasGLES31() {
        return hasGLES31;
    }

    @Api public boolean hasGLES32() {
        return hasGLES32;
    }

    /**
     * @see #gles20()
     * @see #gles30()
     * @see #gles31()
     * @see #gles32()
     * @deprecated Please use specific GLES versions
     */
    @Deprecated(forRemoval = true) @Api public GLES32 gles() {
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
