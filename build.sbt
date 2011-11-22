
organization := "com.typesafe.sbtscalariform"

name := "sbtscalariform"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= (version) { version =>
  val (name, url) =
    if (version endsWith "SNAPSHOT")
      "ivy-snapshots" -> "http://typesafe.artifactoryonline.com/typesafe/ivy-snapshots/"
    else
      "ivy-releases" -> "http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"
  Some(Resolver.url(name, new java.net.URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
