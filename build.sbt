
sbtPlugin := true

organization := "com.typesafe.sbtscalariform"

name := "sbt-scalariform"

version := "0.1.5-SNAPSHOT"

libraryDependencies += "org.scalariform" % "scalariform_2.9.0" % "0.1.0"

publishMavenStyle := false

publishTo := Option(Classpaths.typesafeResolver)
