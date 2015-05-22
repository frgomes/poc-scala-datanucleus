package util


trait PersistenceSupport {

  def props(jdbcDriver: String, jdbcUrl: String, username: String, password: String): Map[String, Any] =
    jdbcDriver match {
      case "org.h2.Driver" =>
        Map[String, Any](
          "javax.jdo.option.Mapping"                 -> "h2",
          "datanucleus.schema.autoCreateAll"         -> "true",
          "javax.jdo.PersistenceManagerFactoryClass" -> "org.datanucleus.api.jdo.JDOPersistenceManagerFactory",
          "javax.jdo.option.ConnectionDriverName"    -> jdbcDriver,
          "javax.jdo.option.ConnectionURL"           -> jdbcUrl,
          "javax.jdo.option.ConnectionUserName"      -> username,
          "javax.jdo.option.ConnectionPassword"      -> password
        )

      case "org.postgresql.Driver" =>
        Map[String, Any](
          "datanucleus.schema.autoCreateAll"         -> "true",
          "javax.jdo.PersistenceManagerFactoryClass" -> "org.datanucleus.api.jdo.JDOPersistenceManagerFactory",
          "javax.jdo.option.ConnectionDriverName"    -> jdbcDriver,
          "javax.jdo.option.ConnectionURL"           -> jdbcUrl,
          "javax.jdo.option.ConnectionUserName"      -> username,
          "javax.jdo.option.ConnectionPassword"      -> password,
          "javax.jdo.option.RetainValues"            -> "true",
          "javax.jdo.option.RestoreValues"           -> "true",
          "javax.jdo.option.Optimistic"              -> "true",
          "javax.jdo.option.NontransactionalWrite"   -> "false",
          "javax.jdo.option.NontransactionalRead"    -> "true",
          "javax.jdo.option.Multithreaded"           -> "true",
          "javax.jdo.option.IgnoreCache"             -> "false"
        )

      case "mongodb.jdbc.MongoDriver" =>
        Map[String, Any](
          "javax.jdo.option.Mapping"                 -> "mongo",
          "datanucleus.schema.autoCreateAll"         -> "true",
          "javax.jdo.PersistenceManagerFactoryClass" -> "org.datanucleus.api.jdo.JDOPersistenceManagerFactory",
          "javax.jdo.option.ConnectionDriverName"    -> jdbcDriver,
          "javax.jdo.option.ConnectionURL"           -> jdbcUrl,
          "javax.jdo.option.ConnectionUserName"      -> username,
          "javax.jdo.option.ConnectionPassword"      -> password
        )

      case _ => throw new IllegalArgumentException(s"unknown driver %s".format(jdbcDriver))
    }

}
