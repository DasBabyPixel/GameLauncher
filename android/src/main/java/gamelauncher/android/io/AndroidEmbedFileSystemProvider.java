/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.io;

import android.content.res.AssetManager;
import gamelauncher.engine.data.embed.DataSupplier;
import gamelauncher.engine.data.embed.EmbedFileSystemProvider;

public class AndroidEmbedFileSystemProvider extends EmbedFileSystemProvider {
    final AssetManager assetManager;

    public AndroidEmbedFileSystemProvider(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override protected DataSupplier newDataSupplier() {
        return new DataSupplier.Fallback(new AndroidDataSupplier(this), super.newDataSupplier());
    }
}
