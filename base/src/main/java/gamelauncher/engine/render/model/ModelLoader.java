/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.render.model;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface ModelLoader {

    /**
     * @param resource
     * @return the model loaded by the resource
     * @throws GameException
     */
    @Api Model loadModel(Resource resource) throws GameException;

}
