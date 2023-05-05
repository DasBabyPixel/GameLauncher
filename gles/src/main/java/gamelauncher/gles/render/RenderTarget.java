/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.render;

import gamelauncher.engine.util.Key;

public interface RenderTarget {

    Key key();

    abstract class Abstract implements RenderTarget {
        private final Key key;

        public Abstract(Key key) {
            this.key = key;
        }

        @Override public Key key() {
            return key;
        }
    }

    class Framebuffer extends Abstract {
        private final gamelauncher.engine.render.Framebuffer framebuffer;

        public Framebuffer(String name, gamelauncher.engine.render.Framebuffer framebuffer) {
            super(new Key(name));
            this.framebuffer = framebuffer;
        }

        public gamelauncher.engine.render.Framebuffer framebuffer() {
            return framebuffer;
        }
    }
}
