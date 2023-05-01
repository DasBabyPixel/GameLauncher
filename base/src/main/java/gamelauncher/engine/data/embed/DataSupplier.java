/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data.embed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface DataSupplier {

    InputStream open(EmbedPath path) throws IOException;

    long size(EmbedPath path) throws IOException;

    boolean directory(EmbedPath path) throws IOException;

    class ClassLoader implements DataSupplier {
        private final java.lang.ClassLoader cl;

        public ClassLoader(java.lang.ClassLoader cl) {
            this.cl = cl;
        }

        @Override
        public InputStream open(EmbedPath path) {
            return cl.getResourceAsStream(path.toAbsolutePath().toString().substring(1));
        }

        @Override
        public long size(EmbedPath path) throws IOException {
            URL url = cl.getResource(path.toAbsolutePath().toString().substring(1));
            if (url == null) throw new FileNotFoundException();
            URLConnection con = url.openConnection();
            return con.getContentLengthLong();
        }

        @Override
        public boolean directory(EmbedPath path) {
            String p = path.toString();
            if (!p.endsWith("/")) {
                p = p + "/";
            }
            URL url = cl.getResource(p);
            if (url == null) {
                return false;
            }
            if (url.getProtocol().equals("file")) {
                try {
                    Path jp = Paths.get(url.toURI());
                    return Files.isDirectory(jp);
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        }
    }

    class Fallback implements DataSupplier {
        private final DataSupplier main;
        private final DataSupplier fallback;

        public Fallback(DataSupplier main, DataSupplier fallback) {
            this.main = main;
            this.fallback = fallback;
        }

        @Override
        public InputStream open(EmbedPath path) throws IOException {
            try {
                return main.open(path);
            } catch (IOException e) {
                try {
                    return fallback.open(path);
                } catch (IOException ex) {
                    ex.addSuppressed(e);
                    throw ex;
                }
            }
        }

        @Override
        public long size(EmbedPath path) throws IOException {
            try {
                return main.size(path);
            } catch (IOException e) {
                try {
                    return fallback.size(path);
                } catch (IOException ex) {
                    ex.addSuppressed(e);
                    throw ex;
                }
            }
        }

        @Override
        public boolean directory(EmbedPath path) throws IOException {
            try {
                return main.directory(path);
            } catch (IOException e) {
                try {
                    return fallback.directory(path);
                } catch (IOException ex) {
                    ex.addSuppressed(e);
                    throw ex;
                }
            }
        }
    }
}

