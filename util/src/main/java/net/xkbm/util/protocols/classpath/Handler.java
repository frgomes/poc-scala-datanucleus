package net.xkbm.util.protocols.classpath;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Richard Gomes<rgomes.info@gmail.com>
 */
public class Handler extends java.net.URLStreamHandler {

    private ClassLoader classLoader;

	public Handler() {
	    final Thread t = Thread.currentThread();
	    this.classLoader = t.getContextClassLoader();
	}

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        return resourceUrl.openConnection();
    }

}
