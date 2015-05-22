package util


trait PersistenceHelper extends PersistenceSupport {

  val debug = false


  def loadSymbols(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Unit = {
    import javax.jdo.JDOHelper
    import scala.collection.JavaConverters._
    import org.datanucleus.api.jdo.JDOPersistenceManager

    val pu = props(jdbcDriver, jdbcUrl, username, password)

    val pmf = JDOHelper.getPersistenceManagerFactory(pu.asJava)
    val pm  = pmf.getPersistenceManager.asInstanceOf[JDOPersistenceManager]

    val exchanges = "classpath:exchanges.csv"
    val loader = new Loader(pm, exchanges)
    val result = loader.loadSymbolsAllLists

    if(debug) {
      result.foreach(s => println(s))
    }

    val size = 4
    val lines =
      Seq(
        "classpath:eoddata.com/symbols/AMEX.txt,1939",
        "classpath:eoddata.com/symbols/NASDAQ.txt,2958",
        "classpath:eoddata.com/symbols/OTCBB.txt,10636",
        "classpath:uol.com.br/symbols/BVSA.json,1612"
      )

    assert(result != null && size == result.size, s"result should have size ${size}".format(size))
    assert(size == lines.size)

    lines.zip(result)
      .foreach({
      case (expected: String, actual: String) =>
        assert(expected == actual, s""""${expected}" not equal to "${actual}"""".format(expected, actual)) })
  }


  def loadIntraday(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Unit = {
    import javax.jdo.JDOHelper
    import scala.collection.JavaConverters._
    import org.datanucleus.api.jdo.JDOPersistenceManager

    val pu = props(jdbcDriver, jdbcUrl, username, password)

    val pmf = JDOHelper.getPersistenceManagerFactory(pu.asJava)
    val pm  = pmf.getPersistenceManager.asInstanceOf[JDOPersistenceManager]

    val exchanges = "classpath:exchanges.csv"
    val loader = new Loader(pm, exchanges)
    val result = Seq( loader.loadIntraday("BVSA", "PETR4.SA") )

    if(debug) {
      result.foreach(s => println(s))
    }

    val size = 1
    val lines =
      Seq(
        "classpath:uol.com.br/intraday/PETR4.SA.json,410"
      )

    assert(result != null && size == result.size, s"result should have size ${size}".format(size))
    assert(size == lines.size)

    lines.zip(result)
      .foreach({
      case (expected: String, actual: String) =>
        assert(expected == actual, s""""${expected}" not equal to "${actual}"""".format(expected, actual)) })
  }


  def countRecords(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Unit = {
    import javax.jdo.JDOHelper
    import scala.collection.JavaConverters._
    import org.datanucleus.api.jdo.JDOPersistenceManager

    val pu = props(jdbcDriver, jdbcUrl, username, password)

    val pmf = JDOHelper.getPersistenceManagerFactory(pu.asJava)
    val pm  = pmf.getPersistenceManager.asInstanceOf[JDOPersistenceManager]

    import model.{Identifier, QIdentifier}
    import org.datanucleus.query.typesafe.TypesafeQuery

    val tq: TypesafeQuery[Identifier] = pm.newTypesafeQuery(classOf[Identifier])
    val q: QIdentifier = QIdentifier.candidate()

    val rs: Seq[Identifier] =
      tq
        .executeList()
        .asInstanceOf[java.util.List[Identifier]]
        .asScala

    // rs.foreach(i => println("%d %s %s".format(i.id, i.symbol, i.name)))

    val expected = 1939 + 2958 + 10636 + 1612
    assert(expected == rs.size, "%d not equal to %d".format(expected, rs.size))
  }


  def querySingleRecords(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Unit = {
    import javax.jdo.JDOHelper
    import scala.collection.JavaConverters._
    import org.datanucleus.api.jdo.JDOPersistenceManager

    val pu = props(jdbcDriver, jdbcUrl, username, password)

    val pmf = JDOHelper.getPersistenceManagerFactory(pu.asJava)
    val pm  = pmf.getPersistenceManager.asInstanceOf[JDOPersistenceManager]

    import model.{Identifier, QIdentifier}
    import org.datanucleus.query.typesafe.TypesafeQuery

    val tq: TypesafeQuery[Identifier] = pm.newTypesafeQuery(classOf[Identifier])
    val q: QIdentifier = QIdentifier.candidate()

    def find(symbol: String): Unit = {
      val i: Identifier =
        tq
          .filter(q.symbol.eq(symbol))
          .executeUnique()
          .asInstanceOf[Identifier]
      assert(symbol == i.symbol, s""""%s" not equal to "%s"""".format(symbol, i.symbol))

      if(debug) {
        println("%s -> %d".format(i.symbol, i.fk))
      }
    }

    find("PETR4.SA")
    find("ACIM")
    find("ZVV")
    find("AAIT")
    find("ZUMZ")
    find("AAAIF.OB")
    find("ZYXI.OB")
    find("AALC34.SA")
    find("ZURI9.SA")
  }


  def queryRecordSet(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Unit = {
    import javax.jdo.JDOHelper
    import scala.collection.JavaConverters._
    import org.datanucleus.api.jdo.JDOPersistenceManager

    val pu = props(jdbcDriver, jdbcUrl, username, password)

    val pmf = JDOHelper.getPersistenceManagerFactory(pu.asJava)
    val pm  = pmf.getPersistenceManager.asInstanceOf[JDOPersistenceManager]

    import model.{Identifier, QIdentifier}
    import org.datanucleus.query.typesafe.TypesafeQuery

    val tq: TypesafeQuery[Identifier] = pm.newTypesafeQuery(classOf[Identifier])
    val q: QIdentifier = QIdentifier.candidate()

    val rs: Seq[Identifier] =
      tq
        .filter(
          (q.symbol.eq("ACIM")).or
            (q.symbol.eq("ZVV")).or
            (q.symbol.eq("AAIT")).or
            (q.symbol.eq("ZUMZ")).or
            (q.symbol.eq("AAAIF.OB")).or
            (q.symbol.eq("ZYXI.OB")).or
            (q.symbol.eq("PETR4.SA")).or
            (q.symbol.eq("AALC34.SA")).or
            (q.symbol.eq("ZURI9.SA")))
        .orderBy(q.symbol.asc())
        .executeList()
        .asInstanceOf[java.util.List[Identifier]]
        .asScala

    assert(rs.size == 9)
    assert(rs(0).symbol == "AAAIF.OB")
    assert(rs(1).symbol == "AAIT")
    assert(rs(2).symbol == "AALC34.SA")
    assert(rs(3).symbol == "ACIM")
    assert(rs(4).symbol == "PETR4.SA")
    assert(rs(5).symbol == "ZUMZ")
    assert(rs(6).symbol == "ZURI9.SA")
    assert(rs(7).symbol == "ZVV")
    assert(rs(8).symbol == "ZYXI.OB")
  }

}
