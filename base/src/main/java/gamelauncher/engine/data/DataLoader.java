/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;

import java.nio.file.Path;

@Api
public class DataLoader {

    @Api public static DataBuffer load(Path path) throws GameException {
        return new DataBuffer(new DataMemory(loadBytes(path)));
    }

    @Api public static byte[] loadBytes(Path path) throws GameException {
        return Files.readAllBytes(path);
    }
}
