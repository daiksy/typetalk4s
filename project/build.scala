import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt._
import sbt.Keys._
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

object typetalk4sBuild extends Build {
  val appName = "typetalk4s"
  val appOrganization = "com.github.daiksy"
  val appVersion  = "0.1.0-SNAPSHOT"
  val appScalaVersion = "2.11.5"
  val skinnyVersion = "1.3.13"

  val main = Project(
    appName,
    base = file("."),
    settings = Defaults.defaultConfigs ++ Seq(
      organization := appOrganization,
      version := appVersion,
      scalaVersion := appScalaVersion,
      crossScalaVersions := Seq(appScalaVersion, "2.10.4"),
      libraryDependencies ++= Seq(
        "org.skinny-framework" %% "skinny-http-client" % skinnyVersion,
        "org.skinny-framework" %% "skinny-json"        % skinnyVersion,
        "com.typesafe"         %  "config"             % "1.2.1"
      ) ++ testDependencies,
      resolvers ++= Seq(
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
      )
    ) ++  formatSettings
  ).settings(SbtScalariform.scalariformSettings: _*).settings(appPublishSettings: _*)

  val sample = Project(
    appName + "-sample",
    base = file("./sample"),
    settings = Defaults.defaultConfigs ++ Seq(
      organization := appOrganization,
      version := appVersion,
      scalaVersion := appScalaVersion
    )
  ).dependsOn(main).aggregate(main)

  lazy val testDependencies = Seq(
    "junit"       %  "junit"       % "4.12"    % "test",
    "org.scalaz"  %% "scalaz-core" % "7.1.1"   % "test",
    "org.specs2"  %% "specs2-core" % "2.4.16"  % "test",
    "org.mockito" %  "mockito-all" % "1.10.19" % "test"
  )

  lazy val formatSettings = Seq(
    ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(IndentWithTabs, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
  )

  lazy val appPublishSettings = Seq(
    // version is defined in version.sbt in order to support sbt-release
    organization := appOrganization,
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      }
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>https://github.com/daiksy/typetalk4s</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:daiksy/typetalk4s.git</url>
          <connection>scm:git:git@github.com:daiksy/typetalk4s.git</connection>
        </scm>
        <developers>
          <developer>
            <id>daiksy</id>
            <name>daiksy</name>
            <url>https://github.com/daiksy</url>
          </developer>
        </developers>
      )
  )
}
