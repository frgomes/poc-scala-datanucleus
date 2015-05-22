package net.xkbm.util.protocols

import java.io.InputStream
import java.net.{URL, URLStreamHandler}

import cgta.otest.FunSuite


object ProtocolSpec extends FunSuite {
  test("file: protocol") {
    val path = "file:///etc/hosts"
    val handler: URLStreamHandler = new net.xkbm.util.protocols.file.Handler
    val url: URL = new URL(null, path, handler)
    val is: InputStream = url.openStream
    assert(is.available() > 0)
    is.close()
  }

  test("classpath: protocol") {
    val path = "classpath:protocol.txt"
    val handler: URLStreamHandler = new net.xkbm.util.protocols.classpath.Handler
    val url: URL = new URL(null, path, handler)
    val is: InputStream = url.openStream
    assert(is.available() > 0)
    is.close()
  }

}
