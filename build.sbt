
organization := "com.typesafe.sbtscalariform"

name := "sbtscalariform"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= (version) { v =>
  import Classpaths._
  Option(if (v endsWith "SNAPSHOT") typesafeSnapshots else typesafeResolver)
}

publishMavenStyle := false

credentials += Credentials(Path.userHome / ".ivy2" / ".typesafe-credentials")
