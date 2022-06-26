package gamelauncher.engine.util.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * @author DasBabyPixel
 */
public class CallerPrintStream extends PrintStream {

	private final Logger logger;
	private final OutputStream parent;
	private final LogLevel level;
	private StackTraceElement caller = null;

	/**
	 * @param level
	 * @param logger
	 * @param out
	 */
	public CallerPrintStream(LogLevel level, Logger logger, OutputStream out) {
		super(out, true);
		this.level = level;
		this.parent = out;
		this.logger = logger;
	}

	private boolean setCaller() {
		if (caller != null) {
			return false;
		}
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement caller = null;
		String cname = getClass().getName();
		boolean next = false;
		for (int i = 0; i < st.length; i++) {
			StackTraceElement t = st[i];
			if (t.getClassName().equals(cname)) {
				next = true;
			} else if (next) {
				next = false;
				caller = t;
			}
		}
		this.caller = caller;
		logger.setCaller(caller);
		logger.setCallerLevel(level);
		return true;
	}

	private void unsetCaller() {
		this.caller = null;
	}

	@Override
	public void write(int b) {
		boolean bool = setCaller();
		super.write(b);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		boolean bool = setCaller();
		super.write(buf, off, len);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(boolean b) {
		boolean bool = setCaller();
		super.print(b);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(char c) {
		boolean bool = setCaller();
		super.print(c);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(int i) {
		boolean bool = setCaller();
		super.print(i);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(long l) {
		boolean bool = setCaller();
		super.print(l);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(float f) {
		boolean bool = setCaller();
		super.print(f);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(double d) {
		boolean bool = setCaller();
		super.print(d);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(char[] s) {
		boolean bool = setCaller();
		super.print(s);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(String s) {
		boolean bool = setCaller();
		super.print(s);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void print(Object obj) {
		boolean bool = setCaller();
		super.print(obj);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println() {
		boolean bool = setCaller();
		super.println();
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(boolean x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(char x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(int x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(long x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(float x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(double x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(char[] x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(String x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public void println(Object x) {
		boolean bool = setCaller();
		super.println(x);
		if (bool) {
			unsetCaller();
		}
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		boolean bool = setCaller();
		super.printf(format, args);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		boolean bool = setCaller();
		super.printf(l, format, args);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		boolean bool = setCaller();
		super.format(format, args);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		boolean bool = setCaller();
		super.format(l, format, args);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		boolean bool = setCaller();
		super.append(csq);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		boolean bool = setCaller();
		super.append(csq, start, end);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

	@Override
	public PrintStream append(char c) {
		boolean bool = setCaller();
		super.append(c);
		if (bool) {
			unsetCaller();
		}
		return this;
	}

}
