/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util;

import android.view.Window;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import de.dasbabypixel.api.property.Property;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.engine.util.Config;
import gamelauncher.engine.util.service.ServiceReference;

public class ImmersiveMode {

    public static final ServiceReference<ImmersiveMode> IMMERSIVE_MODE = new ServiceReference<>(ImmersiveMode.class);

    public static final Config<Boolean> ENABLED = Config.createBoolean("immersive_mode_enabled", false);
    private final Window window;
    private final AndroidGameLauncher launcher;
    private boolean enabled = ENABLED.value();

    public ImmersiveMode(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        launcher.frame().fullscreen().addListener(Property::value);
        launcher.frame().fullscreen().addListener((p, o, n) -> {
            launcher.activity().runOnUiThread(() -> enabled(n));
        });
        this.window = launcher.activity().getWindow();
        update();
    }

    public void update() {
        if (enabled) enable();
        else disable();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        if (this.enabled == enabled) return;
        if (enabled) enable();
        else disable();
    }

    private void disable() {
        enabled = false;
        launcher.frame().fullscreen().value(enabled);
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
    }

    private void enable() {
        enabled = true;
        launcher.frame().fullscreen().value(enabled);
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}
