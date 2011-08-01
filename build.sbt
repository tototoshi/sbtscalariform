
sbtPlugin := true

organization := "com.typesafe.sbt-scalariform"

name := "sbt-scalariform"

version := "0.1.3-SNAPSHOT"

libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.0"

publishMavenStyle := true

publishTo := Some("Typesafe Publish Repo" at "http://repo.typesafe.com/typesafe/maven-releases/")
