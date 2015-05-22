poc-scala-datanuclueus
======================

A proof of concept on Datanucleus JDO with Scala built by SBT.

This project aims to demonstrate how:

1. Datanucleus leverages persistence;
2. Datanucleus Enhancer can be integrated in a SBT build.
3. JDO annotations can be utilized;
4. Scala code can be integrated with Java code;
5. strong typed JDO Query can be utilized in Scala code;

*Note: Requires SBT 0.13.8*


For the impatient
-----------------

    ./sbt test

If you'd like to test with MongoDB or PostgreSQL, please edit ``tests.sbt``.


Current Status
--------------

All test cases pass. Released for public preview and frozen.


For the curious
---------------

*This section explains why JDO, why DataNucleus and why typesafe queries.*

JDO is a technology which allows you to map objects in your application onto database tables and vice-versa.

JDO is conceptually very similar to JPA (Java Persistence Architecture), but it's more powerful than JPA because 
JPO supports NoSQL databases, such as MongoDB, Cassandra, Excel files, among other databases and features. JDO also
supports spatial queries, which are available in enterprise grade databases, such as Oracle and PostgreSQL, among others.

JDO was selected for this proof of concept because the difficulty involved supporting JDO or JPA is more or less the
same, but JDO provides additional features which potentially makes JDO a better option when complex data models is
involved or expected to be involved in future.

DataNucleus is an open-source implementation of the JDO specification and it was selected for this proof of concept
because well... it's free... and because DataNucleus provides a typesafe query interface named JDOQL, which is
inspired on QueryDSL.

Why typesafe queries? The answer must be pretty clear for Scala developers: because the compiler can tell you
at compile time whether your queries are properly defined or not, in accordance with your data model. This is
an example extracted from the sources:
::

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


Given that the JDOQL is database agnostic, this combination allows you the flexibility of developing using an *in-memory* database
such as ``H2``, perform integration tests using ``MongoDB`` and perform user acceptance tests using ``PostgreSQL``.

Notice that ``PostgreSQL`` supports ``NoSQL``. It means that your code can be potentially able to work with both ``SQL`` and ``NoSQL``
data using both ``MongoDB`` and ``PostgreSQL``. This allows you to select the best database for the job later, instead of in advance.
