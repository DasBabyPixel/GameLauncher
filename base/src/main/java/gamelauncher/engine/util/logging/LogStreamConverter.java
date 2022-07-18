package gamelauncher.engine.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LogStreamConverter extends OutputStream {

	private final Lock lock = new ReentrantLock(true);
	CallerPrintStream callerPrintStream;
	private final Charset charset;
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	LogStreamConverter(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void write(int b) throws IOException {
		try {
			lock.lock();
			boolean newLine = b == '\n';
			if (newLine) {
				byte[] array = out.toByteArray();
				out.reset();
				String string = new String(array, charset);
				this.callerPrintStream.converted(string);
				return;
			}
			out.write(b);
		} finally {
			lock.unlock();
		}
	}
}
