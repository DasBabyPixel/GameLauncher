package gamelauncher.engine.plugins;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StreamClassLoader extends ClassLoader {

	private final Map<String, byte[]> classData;
	private final CodeSource codeSource;
	private final ProtectionDomain protectionDomain;

	public StreamClassLoader(ClassLoader parent, URL source, ZipInputStream in) throws IOException {
		super(parent);
		classData = new HashMap<>();
		ZipEntry ze;
		while ((ze = in.getNextEntry()) != null) {
			if (ze.isDirectory())
				continue;
			String ename = ze.getName();
			if (ename.endsWith(".class")) {
				long esize = ze.getSize();
				byte[] edata = new byte[(int) esize];
				in.read(edata, 0, (int) esize);
				String cname = ename.replace('/', '.').substring(0, ename.length() - 6);
				classData.put(cname, edata);
			}
		}
		in.close();
		codeSource = new CodeSource(source, new CodeSigner[0]);
		protectionDomain = new ProtectionDomain(codeSource, new Permissions(), this, new Principal[0]);
	}

	public Map<String, byte[]> getClassData() {
		return classData;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (!classData.containsKey(name)) {
			throw new ClassNotFoundException(name);
		}
		byte[] data = classData.get(name);
//		System.out.println(new String(data, StandardCharsets.UTF_8));
		return defineClass(name, data, 0, data.length, protectionDomain);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}

	static {
		ClassLoader.registerAsParallelCapable();
	}
}
