package options

import sbt._


object Keys {
  val testDatabases = SettingKey[Seq[Seq[String]]]("testDatabases", "Databases employed in test cases")
}
