sbtscalariform
==============

This plugin adds support for source code formatting using `Scalariform`_.


Installing sbtscalariform
-------------------------

- You probably want to install this plugin as a global plugin, but of course you could also go for a local one. For details about sbt plugins see the `sbt documentation`_:

  - Global plugin are defined in a *plugins.sbt* file in the *~/.sbt/plugins/* directory
  - Local plugins are defined in a *plugins.sbt* file in the *project/* folder of your project

- Just add the following lines to your plugin definition, paying attention to the blank line between settings

::
  
  resolvers += Classpaths.typesafeResolver
  
  addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.3.0")


Adding sbcscalariform settings
------------------------------

- If you installed this plugin globally (see above section), then you should add the below line to your global build definition file *build.sbt* in the *~/.sbt/* directory

- If you installed this plugin locally (see above section) or if you prefer to have more flexibility to tweak your settings on a per project basis, then you should add the below line to your local build definition file *build.sbt* of your project

- In any case, add the below line to your build definition, paying attention to the blank line between settings

- These settings will add the task *scalariform-format* in the scopes *compile* and *test* and additionally run this task automatically when compiling; for more control see the below section *Configuring sbtscalariform*

::

  seq(scalariformSettings: _*)


Using sbtscalariform
--------------------

If you added the settings of this plugin like described above, you can either format your sources manually or automatically:

- Whenever you run the tasks *compile* or *test:compile* your source files will be automatically formatted by Scalariform

- If you want to start formatting your source files explicitly, just run the task *scalariform-format* or *test:scalariform-format*


Configuring sbtscalariform
--------------------------

This plugin comes with varoius configuration options. Changing the formatting preferences and deactivating the automatic formatting on compile are probably the most important ones and described in detail.

You can provide your own formatting preferences for Scalariform via the setting key *ScalariformKeys.preferences* which expects an instance of *IFormattingPreferences*. Make sure you import all necessary members from the package *scalariform.formatter.preferences*. Let's look at an example which would change the behavior of the default preferences provided by this plugin (by default the below preferences are set to *true*):

::

  import scalariform.formatter.preferences._

  ScalariformKeys.preferences := FormattingPreferences().
    setPreference(DoubleIndentClassDeclaration, false).
    setPreference(PreserveDanglingCloseParenthesis, false)

If you don't want sbt to automatically format your source files when the tasks *compile* or *test:compile*, just use *defaultScalariformSettings* instead of *scalariformSettings*:

::

  seq(defaultScalariformSettings: _*)

Other useful configuration options are provided by common sbt setting keys:

- *includeFilter in format*: Defaults to "*.scala"
- *excludeFilter in format*: Using the default of sbt 

For advanced users only: If you need more control over configuration options, you could use these settings instead of the above:

- *needToBeScopedScalariformSettings*: These depend on *scalaSource* which is not defined in the global scope
- *noNeedToBeScopedScalariformSettings*: These can be used without scoping


Mailing list
------------

Please use the `sbt mailing list`_ and prefix the subject with "[sbtscalariform]".


Contribution policy
-------------------

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the groll project under the project's open source license.


License
-------

This code is open source software licensed under the `Apache 2.0 License`_. Feel free to use it accordingly.

.. _`Scalariform`: https://github.com/mdr/scalariform
.. _`sbt documentation`: https://github.com/harrah/xsbt/wiki/Plugins
.. _`sbt mailing list`: mailto:simple-build-tool@googlegroups.com
.. _`Apache 2.0 License`: http://www.apache.org/licenses/LICENSE-2.0.html
