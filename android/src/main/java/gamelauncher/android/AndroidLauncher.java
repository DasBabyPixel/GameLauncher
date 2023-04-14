package gamelauncher.android;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import gamelauncher.android.gl.AndroidFrame;
import gamelauncher.android.gl.LauncherGLSurfaceView;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;

public class AndroidLauncher extends Activity {

    private Logger logger;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        getWindow().getDecorView().getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        Activity activity = this;
        try {
            AndroidGameLauncher launcher = new AndroidGameLauncher(this);
            launcher.frame(new AndroidFrame(launcher));
            launcher.view = new LauncherGLSurfaceView(launcher, activity.getApplicationContext());
            setContentView(launcher.view());
            launcher.start(new String[0]);
        } catch (GameException e) {
            e.printStackTrace();
            Threads.sleep(5000);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
