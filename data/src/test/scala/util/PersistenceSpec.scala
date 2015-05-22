package util

import cgta.otest.FunSuite

object PersistenceSpec
  extends FunSuite
  with Databases
  with PersistenceHelper {


  test("classpath") {
    import java.io.File
    val classpath = System.getProperty("java.class.path")
    val cp = classpath.split(File.pathSeparator)
    if (debug) {
      cp.foreach(s => println(s))
    }
    val expected = 33
    assert(expected == cp.size, s"""%d is not equal to %d""".format(expected, cp.size))
  }


  test("Ability to read symbols from EodData test data") {
    val handler: java.net.URLStreamHandler = new net.xkbm.util.protocols.classpath.Handler()
    val url = new java.net.URL(null, "classpath:eoddata.com/symbols/AMEX.txt", handler)
    val reader = new java.io.InputStreamReader(url.openStream())
    assert(reader.ready)
  }


  test("Ability to read symbols from Bovespa test data") {
    val handler: java.net.URLStreamHandler = new net.xkbm.util.protocols.classpath.Handler()
    val url = new java.net.URL(null, "classpath:uol.com.br/symbols/BVSA.json", handler)
    val reader = new java.io.InputStreamReader(url.openStream)
    val parser = new com.google.gson.JsonParser()
    val root = parser.parse(reader)
    val o = root.getAsJsonObject
    val data = o.get("data").getAsJsonArray()

    val expected = 1612
    assert(expected == data.size, s""""%s" is not equal to "%s"""".format(expected, data.size))

    val it = data.iterator()
    val first = it.next.getAsJsonObject

    if(debug) {
      println(first.get("idt").getAsInt)
      println(first.get("code").getAsString)
      println(first.get("name").getAsString.trim)
      println(first.get("companyName").getAsString.trim)
      println(first.get("companyAbvName").getAsString.trim)
    }

    assert(first.get("idt")           .getAsString.trim == "1453"     , s""" "%s" not equal to "%s" """.format("1453"     , first.get("idt").getAsString.trim))
    assert(first.get("code")          .getAsString.trim == "AALC34.SA", s""" "%s" not equal to "%s" """.format("AALC34.SA", first.get("code").getAsString.trim))
    assert(first.get("name")          .getAsString.trim == "ALCOA"    , s""" "%s" not equal to "%s" """.format("ALCOA"    , first.get("name").getAsString.trim))
    assert(first.get("companyName")   .getAsString.trim == "ALCOA"    , s""" "%s" not equal to "%s" """.format("ALCOA"    , first.get("companyName").getAsString.trim))
    assert(first.get("companyAbvName").getAsString.trim == "ALCOA"    , s""" "%s" not equal to "%s" """.format("ALCOA"    , first.get("companyAbvName").getAsString.trim))
  }


  //
  // From this point on all tests should support all database drivers
  //

  configs.foreach {
    config =>
      val db         = config._1
      val jdbcDriver = config._2.jdbcDriver
      val jdbcUrl    = config._2.jdbcUrl
      val username   = config._2.username
      val password   = config._2.password

      test(s"Driver: ${db} :: Load symbol lists") {
        loadSymbols(jdbcDriver, jdbcUrl, username, password)
      }

      test(s"Driver: ${db} :: Load intraday data") {
        loadIntraday(jdbcDriver, jdbcUrl, username, password)
      }

      test(s"Driver: ${db} :: Count number of records") {
        countRecords(jdbcDriver, jdbcUrl, username, password)
      }

      test(s"Driver: ${db} :: Query single records") {
        querySingleRecords(jdbcDriver, jdbcUrl, username, password)
      }

      test(s"Driver: ${db} :: Query record set") {
        queryRecordSet(jdbcDriver, jdbcUrl, username, password)
      }
  }

}
