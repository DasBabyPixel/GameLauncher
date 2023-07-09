/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.Config;
import gamelauncher.engine.util.DefaultOperatingSystems;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GameDirectoryResolver {
    public static Path resolve(GameLauncher launcher) {
        String gd = Config.GAME_DIRECTORY.value();
        Type type = null;
        for (Type value : Type.values()) {
            if (value.type.equalsIgnoreCase(gd)) {
                type = value;
                break;
            }
        }
        if (type == null) {
            launcher.logger().warnf("Unknown game_directory: %s", gd);
            type = Type.FOLDER;
        }
        return type.resolve(launcher);
    }

    public enum Type {
        FOLDER("folder") {
            @Override Path resolve(GameLauncher launcher) {
                if (launcher.operatingSystem() == DefaultOperatingSystems.ANDROID) return launcher.serviceProvider().service(AndroidProvider.class).gameDirectory();
                return Paths.get(Config.NAME.value()).toAbsolutePath();
            }
        }, APPLICATION("application") {
            @Override Path resolve(GameLauncher launcher) {
                Path path = null;
                if (launcher.operatingSystem() == DefaultOperatingSystems.WINDOWS) {
                    String d = System.getenv("APPDATA");
                    if (d != null) path = Paths.get(d).resolve(Config.NAME.value()).toAbsolutePath();
                }
                if (path == null) {
                    launcher.logger().warn("Failed to use application folder");
                    return FOLDER.resolve(launcher);
                }
                return path;
            }
        },
        ;
        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }

        abstract Path resolve(GameLauncher launcher);
    }

    public interface AndroidProvider {
        Path gameDirectory();
    }
}
