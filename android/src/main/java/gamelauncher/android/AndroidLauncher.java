package gamelauncher.android;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import de.dasbabypixel.annotations.Api;
import gamelauncher.android.internal.gl.AndroidFrame;
import gamelauncher.android.internal.gl.LauncherGLSurfaceView;
import gamelauncher.android.internal.util.ImmersiveMode;
import gamelauncher.engine.util.Config;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;

public class AndroidLauncher extends Activity {

    private Logger logger;
    private AndroidGameLauncher launcher;

    @Api public void init(AndroidGameLauncher launcher) {
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        Config.DEBUG.value(true);
        logger = Logger.logger();
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            if (handler != null) {
                handler.uncaughtException(thread, error);
            } else {
                logger.error(error);
            }
        });

        super.onCreate(savedInstanceState);

        Activity activity = this;
        try {
            launcher = new AndroidGameLauncher(this);
            launcher.frame(new AndroidFrame(launcher));
            launcher.view = new LauncherGLSurfaceView(launcher, activity.getApplicationContext());
            setContentView(launcher.view());
            launcher.start(new String[0]);
        } catch (GameException e) {
            logger.error(e);
            Threads.sleep(5000);
            throw new RuntimeException(e);
        }
    }

    @Override protected void onPause() {
        super.onPause();

    }

    @Override protected void onResume() {
        super.onResume();
        ImmersiveMode immersiveMode = launcher.immersiveMode();
        if (immersiveMode != null) immersiveMode.update();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
