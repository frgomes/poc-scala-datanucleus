import options.Keys._

//****************************************************************************************
//
// This file keeps configurations for test databases
//
// Each line entry contains:
//    * database vendor, preceeded by a minus (-) sign
//    * jdbcDriver class
//    * jdbcURL  for connection to your database
//    * username
//    * password
//
// Notice that only "-h2", "-mongodb" and "-postgres" are supported at the moment.
// For adding support to other database vendors, please review class PersistenceSupport
//
//****************************************************************************************

testDatabases in ThisBuild :=
  Seq(
    Seq("-h2"      , "org.h2.Driver",            "jdbc:h2:mem:test"                      , "sa"  ,  "")
// ,Seq("-mongo"   , "mongodb.jdbc.MongoDriver", "mongodb:/alpha1"                        , "test",  "test")
// ,Seq("-postgres", "org.postgresql.Driver",    "jdbc:postgresql://localhost:5432/alpha1", "test",  "test")
  )
