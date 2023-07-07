/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.image;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

@Api
public interface ImageDecoder extends GameResource {

    @Api Icon decodeIcon(ResourceStream resourceStream) throws GameException;

}
