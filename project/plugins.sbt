import sbt._

import Defaults._

// Comment to get more information during initialization
logLevel := Level.Info

resolvers ++= Seq(
    DefaultMavenRepository,
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    Classpaths.typesafeReleases,
    Classpaths.typesafeSnapshots
)

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.3-SNAPSHOT")

addSbtPlugin("com.github.theon" %% "xsbt-coveralls-plugin" % "0.0.5-SNAPSHOT")