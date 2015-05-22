import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtPgp.PgpKeys.publishSigned
import xerial.sbt.Sonatype.sonatypeSettings

import options.Versions
import options.Keys


// settings and tasks ( see also: project/Keys.scala ) ----------------------------------------------------------

val genjdoql      = TaskKey[Seq[File]]("genjdoql",    "DataNucleus JDOQL Entities")
val gendbconfig   = TaskKey[Seq[File]]("gendbconfig", "Generate Database configurations for testing")


// compilation options ------------------------------------------------------------------------------------------

val javacOpts : Seq[String] =
  Seq(
    "-encoding", "UTF-8",
    "-source", "1.8",
    "-target", "1.8"
  )

val scalacOpts : Seq[String] =
  Seq(
    "-unchecked", "-deprecation", "-feature"
  )

val javacCheckOpts: Seq[String] =
  Seq(
    "-Xlint"
  )

val scalacCheckOpts: Seq[String] =
  Seq(
    "-Xlint",
    "-Yinline-warnings", "-Ywarn-adapted-args",
    "-Ywarn-dead-code", "-Ywarn-inaccessible", "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit", "-Xlog-free-terms"
  )

val librarySettings : Seq[Setting[_]] =
  Seq(
    organization       := "net.xkbm.platform",

    scalaVersion       := "2.11.6",
    crossScalaVersions := Seq("2.11.6", "2.10.5"),
    crossPaths         := true,

    compileOrder in Compile := CompileOrder.JavaThenScala,
    compileOrder in Test    := CompileOrder.JavaThenScala,

    javacOptions  ++= javacOpts,
    scalacOptions ++= scalacOpts,
    fork          :=  true
  )

val paranoidOptions : Seq[Setting[_]] =
  Seq(
    javacOptions  ++= javacCheckOpts,
    scalacOptions ++= scalacCheckOpts
  )

val optimizationOptions : Seq[Setting[_]] =
  Seq(
    scalacOptions ++= Seq("-optimise")
  )

val documentationOptions : Seq[Setting[_]] =
  Seq(
    javacOptions  in (Compile,doc) ++= Seq("-Xdoclint", "-notimestamp", "-linksource"),
    scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")
  )


// code generation ----------------------------------------------------------------------------------------------

val managedSources : Seq[Setting[_]] =
  Seq(
    managedSourceDirectories in Compile +=
      baseDirectory.value / "target" / scalav(scalaVersion.value) / "src_managed" / "main" / "java",
    managedSourceDirectories in Test +=
      baseDirectory.value / "target" / scalav(scalaVersion.value) / "src_managed" / "test" / "java"
  )


// test frameworks ----------------------------------------------------------------------------------------------

val utestFramework : Seq[Setting[_]] =
  Seq(
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

val minitestFramework : Seq[Setting[_]] =
  Seq(
    libraryDependencies += "org.monifu" %%% "minitest" % "0.11" % "test",
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )

val littleSpecFramework : Seq[Setting[_]] =
  Seq(
    libraryDependencies += "org.qirx" %%% "little-spec" % Versions.`little-spec` % "test",
    testFrameworks += new TestFramework("org.qirx.littlespec.scalajs.TestFramework")
  )

val otestFramework : Seq[Setting[_]] =
  Seq(
    libraryDependencies += "biz.cgta" %% "otest-jvm" % "0.2.0" % "test",
    testFrameworks := Seq(new TestFramework("cgta.otest.runner.OtestSbtFramework"))
  )


// dependencies -------------------------------------------------------------------------------------------------

val deps_resolvers : Seq[Setting[_]] =
  Seq(
    resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  )

//XXX val deps_langtools: Seq[Setting[_]] =
//XXX   Seq(
//XXX     libraryDependencies ++= Seq(
//XXX       "org.checkerframework"     %  "checker-qual"                          % Versions.jsr308,
//XXX       "javax.validation"         %   "validation-api"                       % Versions.validation,
//XXX     ))

val deps_tagging : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "com.google.code.gson"   %  "gson"                                  % Versions.gson
      // "org.yaml"               %  "snakeyaml"                             % Versions.snakeyaml,
      // "org.ccil.cowan.tagsoup" %  "tagsoup"                               % Versions.tagsoup
    ))

val deps_stream : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.scalaz"             %% "scalaz-core"                           % Versions.scalaz,
      "org.scalaz.stream"      %% "scalaz-stream"                         % Versions.`scalaz-stream`
    ))

val deps_httpclient : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.http4s"             %% "http4s-blazeclient"                    % Versions.http4s,
      "org.scalaj"             %% "scalaj-http"                           % Versions.`scalaj-http`
    ))

val deps_httpserver : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.http4s"             %% "http4s-blazeserver"                    % Versions.http4s,
      "org.http4s"             %% "http4s-servlet"                        % Versions.http4s,
      "org.http4s"             %% "http4s-jetty"                          % Versions.http4s
    ))

val deps_saddle : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.scala-saddle"       %% "saddle-core"                           % Versions.saddle
    ))

val deps_talib : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "com.tictactec"          % "ta-lib"                                 % Versions.talib
    ))

val deps_jfreechart : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.jfree"              % "jfreechart"                             % Versions.jfreechart
    ))


val deps_parser : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "com.github.scopt"         %% "scopt"                                 % Versions.scopt ))

val deps_database : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "javax.jdo"              %   "jdo-api"                              % Versions.`jdo-api`,
      "org.datanucleus"        %   "datanucleus-core"                     % "4.0.6",  //TODO:: Versions.datanucleus,
      "org.datanucleus"        %   "datanucleus-api-jdo"                  % "4.0.5",  //TODO:: Versions.datanucleus,
      "org.datanucleus"        %   "datanucleus-jdo-query"                % "4.0.4",  //TODO:: Versions.datanucleus,
      "org.datanucleus"        %   "datanucleus-rdbms"                    % "4.0.11", //TODO:: Versions.datanucleus,
      "org.datanucleus"        %   "datanucleus-mongodb"                  % "4.0.5",  //TODO:: Versions.datanucleus,
      //XXX "org.datanucleus"        %   "javax.persistence"                    % Versions.`jpa-api`,
      //XXX "org.datanucleus"        %   "datanucleus-api-jpa"                  % Versions.datanucleus,
      //XXX "org.datanucleus"        %   "datanucleus-jpa-query"                % Versions.datanucleus,
      //XXX "org.datanucleus"        %   "datanucleus-accessplatform-jpa-rdbms" % Versions.datanucleus,
      //XXX "org.datanucleus"        %   "datanucleus-java8"                    % Versions.datanucleus,
      "com.h2database"         %   "h2"                                   % Versions.h2database,
      "org.mongodb"            %   "mongo-java-driver"                    % Versions.mongodb,
      "org.postgresql"         %   "postgresql"                           % Versions.postgresql ))


// utility functions --------------------------------------------------------------------------------------------

val debug = false


def scalav(scalaVersion: String) =
  if (scalaVersion startsWith "2.10.") "scala-2.10" else "scala-2.11"


def runner(app: String,
           args: Seq[String],
           cwd: Option[File] = None,
           env: Map[String, String] = Map.empty): Int = {
  import scala.collection.JavaConverters._

  val cmd: Seq[String] = app +: args
  val pb = new java.lang.ProcessBuilder(cmd.asJava)
  if (cwd.isDefined) pb.directory(cwd.get)
  pb.inheritIO
  //FIXME: set environment
  val process = pb.start()
  def cancel() = {
    //XXX println("Run canceled.")
    process.destroy()
    15
  }
  try process.waitFor catch { case e: InterruptedException => cancel() }
}


def javaRunner(classpath: Option[Seq[File]] = None,
               runJVMOptions: Seq[String] = Nil,
               mainClass: Option[String] = None,
               args: Seq[String],
               cwd: Option[File] = None,
               javaHome: Option[File] = None,
               javaTool: Option[String] = None,
               envVars: Map[String, String] = Map.empty,
               connectInput: Boolean = false,
               outputStrategy: Option[OutputStrategy] = Some(StdoutOutput)): Unit = {

  val app : String      = javaHome.fold("") { p => p.absolutePath + "/bin/" } + javaTool.getOrElse("java")
  val jvm : Seq[String] = runJVMOptions.map(p => p.toString)
  val cp  : Seq[String] =
    classpath
      .fold(Seq.empty[String]) { paths =>
      Seq(
        "-cp",
        paths
          .map(p => p.absolutePath)
          .mkString(java.io.File.pathSeparator))
    }
  val klass = mainClass.fold(Seq.empty[String]) { name => Seq(name) }
  val xargs : Seq[String] = jvm ++ cp ++ klass ++ args

  if(debug) {
    println("=============================================================")
    println(s"${app} " + xargs.mkString(" "))
    println("=============================================================")
    println("")
  }

  if (cwd.isDefined) IO.createDirectory(cwd.get)
  val errno = runner(app, xargs, cwd, envVars)
  if(errno!=0) throw new IllegalStateException(s"errno = ${errno}")
}


def generateQueryEntities(sourcepath: File,
                          sources: Seq[File],
                          generated: File,
                          classes: File,
                          classpath: Seq[File],
                          javacOptions: Seq[String]): Seq[File] = {
  IO.createDirectory(generated)
  javaRunner(
    javaTool = Option("javac"),
    args =
      javacOptions ++
        (if(debug) Seq("-verbose") else Seq.empty[String]) ++
        Seq(
          "-sourcepath", sourcepath.absolutePath,
          "-s",  generated.absolutePath,
          "-d",  classes.absolutePath) ++
        sources.map(p => p.absolutePath),
    classpath = Option(classpath),
    cwd = Option(classes)
  )
  classes.listFiles.filter(f => f.isFile && (f.ext == "class"))
}

def enhanceSchema(classes: File, classpath: Seq[File]): Seq[File] = {
  javaRunner(
    mainClass = Option("javax.jdo.Enhancer"),
    args =
      (if(debug) Seq("-v") else Seq.empty[String]) ++
      Seq(
        "-pu", "code-generation",
        "-d",  classes.absolutePath),
    classpath = Option(classpath),
    cwd = Option(classes)
  )
  classes.listFiles.filter(f => f.isFile && (f.ext == "class"))
}

def testDbConfigs(options: Seq[Seq[String]], classes: File): Seq[File] = {
  val file: File = classes / "util" / "Databases.scala"
  val lines =
    options
      .map({ case item =>
      assert(item(0).charAt(0) == '-')
      val db  : String = s""""%s"""".format(item(0).substring(1))
      var args: String = item.tail.map(s => s""""${s}"""").mkString(",")
      s"""      ${db} -> DbConfig(${args})""" })
      .mkString(",\n")

  println("=======================================================================================")
  println("Test Databases:")
  println(lines)
  println("=======================================================================================")

  IO.write(
    file,
    s"""package util
       |
       |// *****************************************************************
       |// *          THIS FILE IS AUTOMAGICALLY GENERATED BY SBT          *
       |// *                     DO NOT EDIT BY HAND                       *
       |// * SINCE IT WILL BE OVERWRITTEN NEXT TIME YOU BUILD THIS PROJECT *
       |// *****************************************************************
       |
       |trait Databases {
       |
       |  case class DbConfig(jdbcDriver: String, jdbcUrl: String, username: String, password: String)
       |
       |  val configs: Map[String, DbConfig] =
       |    Map(
       |${lines}
       |    )
       |}
      """.stripMargin)
  Seq(file)
}



// projects -----------------------------------------------------------------------------------------------------

lazy val root =
  project.in(file("."))
    .settings(buildInfoSettings:_*)
    .settings(disablePublishing:_*)
    .aggregate(model, data) //XXX util, fixtures, tools)

lazy val model =
  project.in(file("model"))
    .settings(publishSettings:_*)
    .settings(librarySettings:_*)
    .settings(paranoidOptions:_*)
    .settings(otestFramework: _*)
    .settings(deps_resolvers:_*)
    //XXX .settings(deps_langtools:_*)
    .settings(deps_tagging:_*)
    .settings(deps_stream:_*)
    .settings(deps_database:_*)
    .settings(managedSources:_*)
    .settings(
      Seq(
        // generate JDOQL Entities
        genjdoql in Compile := {
          generateQueryEntities(
            sourcepath = (javaSource in Compile).value,
            sources = (unmanagedSources in Compile).value,
            generated = baseDirectory.value / "target" / scalav(scalaVersion.value) / "src_managed" / "main"  / "java",
            classes = (classDirectory in Compile).value,
            classpath = (managedClasspath in Compile).value.files,
            javacOptions = javacOpts :+ "-AqueryMode=PROPERTY"
          )},
        sourceGenerators in Compile <+= genjdoql in Compile,
        // prevent javac from running annotation processors
        javacOptions ++= Seq( "-proc:none" ),
        // perform bytecode enhancement
        manipulateBytecode in Compile := {
          val previous = (manipulateBytecode in Compile).value
          enhanceSchema(
            classes = (classDirectory in Compile).value,
            classpath =
              (managedClasspath in Compile).value.files ++
                (unmanagedResourceDirectories in Compile).value :+
                (classDirectory in Compile).value)
          previous
        }
      ):_*)
    .dependsOn(util)

lazy val data =
  project.in(file("data"))
    .settings(publishSettings:_*)
    .settings(librarySettings:_*)
    .settings(paranoidOptions:_*)
    .settings(otestFramework: _*)
    .settings(deps_resolvers:_*)
    //XXX .settings(deps_langtools:_*)
    .settings(deps_tagging:_*)
    .settings(deps_stream:_*)
    .settings(deps_database:_*)
    .settings(managedSources:_*)
    .settings(
      Seq(
        // generate Database Configurations
        gendbconfig in Test := {
          testDbConfigs(
            options = (Keys.testDatabases in ThisBuild).value,
            classes = baseDirectory.value / "target" / scalav(scalaVersion.value) / "src_managed" / "test" / "scala"
          )},
        sourceGenerators in Test <+= gendbconfig in Test
      ):_*)
    .dependsOn(model, util)

lazy val util =
  project.in(file("util"))
    .settings(publishSettings:_*)
    .settings(librarySettings:_*)
    .settings(paranoidOptions:_*)
    .settings(otestFramework: _*)
    .settings(deps_resolvers:_*)
    //XXX .settings(deps_langtools:_*)
    .settings(deps_tagging:_*)
    .settings(deps_stream:_*)

//XXX val fixtures =
//XXX   project.in(file("fixtures"))
//XXX     .settings(disablePublishing:_*)
//XXX     .settings(librarySettings:_*)
//XXX     .settings(paranoidOptions:_*)
//XXX     .settings(otestFramework:_*)
//XXX     .settings(deps_stream:_*)
//XXX     .settings(deps_database:_*)
//XXX
//XXX val tools =
//XXX   project.in(file("tools"))
//XXX     .settings(disablePublishing:_*)
//XXX     .settings(librarySettings:_*)
//XXX     .settings(paranoidOptions:_*)



// publish settings ---------------------------------------------------------------------------------------------

lazy val disablePublishing =
  sonatypeSettings ++
    Seq(
      publishArtifact := false,
      publishSigned := (),
      publish := (),
      publishLocal := ()
    )



// FIXME: review URLs
lazy val publishSettings =
  sonatypeSettings ++
    Seq(
      publishTo := {
        //-- val nexus = "https://bitbucket.org/xkbm/maven-repository"
        val nexus = "https://localhost/artifactory/maven-repository"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      pomIncludeRepository := { _ => false },
      pomExtra := {
        <url>http://github.com/frgomes/poc-scala-datanucleus</url>
          <licenses>
            <license>
              <name>BSD</name>
            </license>
          </licenses>
          <scm>
            <connection>scm:git:github.com/frgomes/poc-scala-datanucleus.git</connection>
            <developerConnection>scm:git:frgomes@github.com:frgomes/poc-scala-datanucleus.git</developerConnection>
            <url>http://github.com/frgomes/poc-scala-datanucleus</url>
          </scm>
          <developers>
            <developer>
              <id>frgomes</id>
              <name>Richard Gomes</name>
              <url>http://rgomes-info.blogspot.com</url>
            </developer>
          </developers>
      }
    )
