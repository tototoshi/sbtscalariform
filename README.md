sbtscalariform
==============

[Scalariform][scalariform] plugin for [sbt]. This plugin requires sbt 0.11.

[sbt]: https://github.com/harrah/xsbt
[scalariform]: https://github.com/mdr/scalariform


Add plugin
----------

To use the plugin in a project add the following to `project/plugins.sbt`:

    resolvers += Classpaths.typesafeResolver

    addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.2.0")


Add settings
------------

Add the scalariform plugin settings to projects that should have their sources formatted.

In an sbt light definition:

    import com.typesafe.sbtscalariform.ScalariformPlugin._

    seq(scalariformSettings: _*)

In an sbt full definition:

    import com.typesafe.sbtscalariform.ScalariformPlugin

    lazy val someProject = Project(
      id = "some-project",
      base = file("."),
      settings = Defaults.defaultSettings ++ ScalariformPlugin.scalariformSettings)

Using `ScalariformPlugin.scalariformSettings` will automatically format sources when `compile` or `test:compile` are run.

If you don't want this automatic formatting, use `ScalariformPlugin.baseScalariformSettings` like below and execute `format` or `test:format` explicitly:

    import com.typesafe.sbtscalariform.ScalariformPlugin._

    seq((inConfig(Compile)(baseScalariformSettings) ++ inConfig(Test)(baseScalariformSettings)): _*)


Configure scalariform
---------------------

Configure scalariform using the `formatPreferences` setting and a scalariform `FormattingPreferences` object. For example:

    lazy val formatSettings = ScalariformPlugin.scalariformSettings ++ Seq(
      formatPreferences in Compile := formattingPreferences,
      formatPreferences in Test    := formattingPreferences)

    def formattingPreferences = {
      import scalariform.formatter.preferences._
      FormattingPreferences().setPreference(IndentSpaces, 3)
    }

See [scalariform] for more information about possible options.
