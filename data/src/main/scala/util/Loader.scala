package util

import org.datanucleus.api.jdo.JDOPersistenceManager


class Loader(pm: JDOPersistenceManager, exchanges: String) {

  import java.net.{URL, URLStreamHandler}
  import scala.io.Source
  import scala.util.matching.Regex
  import model.{Identifier, Intraday, QIdentifier}
  import org.datanucleus.query.typesafe.TypesafeQuery

  val FIXPOINT = 1000000 //FIXME: refactor this variable to some other place

  private val handler: URLStreamHandler = new net.xkbm.util.protocols.classpath.Handler()
  private val eoddata: Regex = """.+eoddata\.com/.*""".r
  private val uol    : Regex = """.+uol\.com\.br/.*""".r

  case class Exchange(mic: String, name: String, symbols: String, intraday: String)

  val exchangeMap: Map[String, Exchange] =
    Source
      .fromURL(new URL(null, exchanges, handler))
      .getLines()
      .map({
      line =>
        val parts = line.split(",")
        val exchange = Exchange(mic = parts(0), name = parts(1), symbols = parts(2), intraday = parts(3))
        (parts(0) -> exchange) })
      .toMap


  def loadSymbolsAllLists: Seq[String] =
    exchangeMap.map({ case (mic, _) => loadSymbols(mic) }).toSeq


  def loadSymbols(mic: String): String = {
    val url = exchangeMap.get(mic).flatMap(exchange => Option(exchange.symbols))
    url match {
      case Some(eoddata(_*)) => loadSymbolsEoddata(url.get)
      case Some(uol(_*))     => loadSymbolsUOL(url.get)
      case None              => throw new IllegalArgumentException(s"Cannot find URL for loading ${mic} symbols")
      case _                 => throw new IllegalArgumentException(s"Cannot find loader able to process ${url}")
    }
  }


  private def loadSymbolsEoddata(url: String): String = {
    val source = Source.fromURL(new URL(null, url, handler))
    var count = -1
    pm.currentTransaction.begin
    for (line <- source.getLines()) {
      if(count > -1) {
        val parts = line.split("\t")
        pm.makePersistent(new Identifier(parts(0), parts(1), parts(1)))
      }
      count = count + 1
    }
    pm.currentTransaction.commit
    s"${url},${count}"
  }


  private def loadSymbolsUOL(url: String): String = {
    val stream = new java.net.URL(null, url, handler)
    val reader = new java.io.InputStreamReader(stream.openStream)
    val parser = new com.google.gson.JsonParser()
    val root = parser.parse(reader)
    val o = root.getAsJsonObject
    val data = o.get("data").getAsJsonArray()
    val it = data.iterator()

    var count = 0
    pm.currentTransaction.begin
    while(it.hasNext) {
      val o = it.next.getAsJsonObject
      pm.makePersistent(
        new Identifier(
          o.get("code").getAsString.trim,
          o.get("name").getAsString.trim,
          o.get("companyName").getAsString.trim,
          o.get("companyAbvName").getAsString.trim,
          o.get("idt").getAsString.trim))
      count = count + 1
    }
    pm.currentTransaction.commit
    s"${url},${count}"
  }


  def loadIntraday(mic: String, symbol: String): String = {
    val url = exchangeMap.get(mic).flatMap(exchange => Option(exchange.intraday))
    url match {
      //TODO: case eoddata(_*) => loadIntradayEoddata(url)
      case Some(uol(_*)) => loadIntradayUOL(url.get, symbol)
      case None          => throw new IllegalArgumentException(s"Cannot find URL for loading ${symbol} intraday data")
      case _             => throw new IllegalArgumentException(s"Cannot find loader able to process ${url}")
    }
  }

  private def loadIntradayUOL(url: String, symbol: String): String = {
    var count = 0
    val tq: TypesafeQuery[Identifier] = pm.newTypesafeQuery(classOf[Identifier])
    val q: QIdentifier = QIdentifier.candidate()
    val id: Identifier = tq.filter(q.symbol.eq(symbol)).executeUnique()
    if(id!=null) {
      val stream = new java.net.URL(null, url.format(id.fk), handler)
      val reader = new java.io.InputStreamReader(stream.openStream)
      val parser = new com.google.gson.JsonParser()
      val root = parser.parse(reader)
      val o = root.getAsJsonObject
      val data = o.get("data").getAsJsonArray()
      val it = data.iterator()
      pm.currentTransaction.begin
      while(it.hasNext) {
        val o = it.next.getAsJsonObject
        pm.makePersistent(
          new Intraday(
            id,
            new java.sql.Timestamp(o.get("date").getAsLong),
            (o.get("price").getAsFloat  * FIXPOINT).toLong,
            (o.get("high").getAsFloat   * FIXPOINT).toLong,
            (o.get("low").getAsFloat    * FIXPOINT).toLong,
            (o.get("var").getAsFloat    * FIXPOINT).toLong,
            (o.get("varpct").getAsFloat * FIXPOINT).toLong,
            (o.get("vol").getAsLong)))
        count = count + 1
      }
      pm.currentTransaction.commit
    }
    s"${url},${count}"
  }


}
