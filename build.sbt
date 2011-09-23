
sbtPlugin := true

organization := "com.typesafe.sbtscalariform"

name := "sbt-scalariform"

version := "0.1.4-SNAPSHOT"

libraryDependencies += "org.scalariform" % "scalariform_2.9.0" % "0.1.0"

publishMavenStyle := false

publishTo := Some("Typesafe Publish Repo" at "http://repo.typesafe.com/typesafe/ivy-releases/")
