/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.sound;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.resource.Resource;

public interface SoundManager extends GameResource {

    Sound loadSound(Resource resource);

}
