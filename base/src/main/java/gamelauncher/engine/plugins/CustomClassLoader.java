package gamelauncher.engine.plugins;

import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassLoader extends URLClassLoader {
	
	public CustomClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
	static {
		ClassLoader.registerAsParallelCapable();
	}
}
