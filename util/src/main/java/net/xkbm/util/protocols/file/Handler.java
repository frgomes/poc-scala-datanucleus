package net.xkbm.util.protocols.file;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Richard Gomes<rgomes.info@gmail.com>
 */
public class Handler extends java.net.URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new FileURLConnection(u);
    }

}
