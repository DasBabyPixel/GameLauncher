package gamelauncher.engine.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class OutErrStream extends OutputStream {

	public final OutputStream out;
	public final OutputStream err;
	public final AtomicReference<Output> output = new AtomicReference<>();

	public OutErrStream(OutputStream out, OutputStream err) {
		this.out = out;
		this.err = err;
	}

	@Override
	public void write(int b) throws IOException {
		Output o = output.get();
		if (o == Output.OUT) {
			out.write(b);
		} else if (o == Output.ERR) {
			err.write(b);
		}
	}

	public static enum Output {
		OUT, ERR
	}
}
