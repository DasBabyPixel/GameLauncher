package gamelauncher.engine.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LogStreamConverter extends OutputStream {

    private final Lock lock = new ReentrantLock(true);
    private final Charset charset;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    ConverterStream converterStream;
    private boolean carriage = false;

    LogStreamConverter(Charset charset) {
        this.charset = charset;
    }

    @Override public void write(int b) throws IOException {
        try {
            lock.lock();
            boolean newLine = b == '\n';
            if (newLine) {
                carriage = false;
//				String string = out.toString(charset);
                byte[] bytes = out.toByteArray();
                @SuppressWarnings("StringOperationCanBeSimplified") String string = new String(bytes, charset);
                out.reset();
                this.converterStream.converted(string);
                return;
            } else if (carriage) {
                out.write('\r');
            }
            carriage = b == '\r';
            if (carriage) {
                return;
            }
            out.write(b);
        } finally {
            lock.unlock();
        }
    }
}
