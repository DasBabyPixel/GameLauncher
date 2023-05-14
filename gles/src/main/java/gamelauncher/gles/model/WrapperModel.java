/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.model;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

public class WrapperModel extends AbstractGameResource implements gamelauncher.engine.render.model.WrapperModel {
    protected final Property<Model> handle = Property.withValue(null);

    @Override public void render(ShaderProgram program) throws GameException {
        throw new UnsupportedOperationException("You cannot render this model. Call #handle to query the model to render");
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        Model h = this.handle.value();
        if (h != null) {
            CompletableFuture<Void> f = h.cleanup();
            handle.value(null);
            return f;
        }
        return null;
    }

    public void handle(Model model) throws GameException {
        if (cleanedUp()) {
            model.cleanup();
        } else this.handle.value(model);
    }

    @Override public Model handle() {
        return handle.value();
    }

    @Override public Property<Model> handleProperty() {
        return handle;
    }
}
