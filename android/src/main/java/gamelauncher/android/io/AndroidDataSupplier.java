/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.io;

import android.content.res.AssetFileDescriptor;
import gamelauncher.engine.data.embed.DataSupplier;
import gamelauncher.engine.data.embed.EmbedPath;

import java.io.IOException;
import java.io.InputStream;

public class AndroidDataSupplier implements DataSupplier {
    private final AndroidEmbedFileSystemProvider provider;

    public AndroidDataSupplier(AndroidEmbedFileSystemProvider provider) {
        this.provider = provider;
    }

    @Override
    public InputStream open(EmbedPath path) throws IOException {
        return provider.assetManager.open(path.toAbsolutePath().toString().substring(1));
    }

    @Override
    public long size(EmbedPath path) throws IOException {
        try (AssetFileDescriptor fileDescriptor = provider.assetManager.openFd(path.toAbsolutePath().toString().substring(1))) {
            return fileDescriptor.getLength();
        }
    }

    @Override
    public boolean directory(EmbedPath path) throws IOException {
        return provider.assetManager.list(path.toAbsolutePath().toString().substring(1)).length > 0;
    }
}
