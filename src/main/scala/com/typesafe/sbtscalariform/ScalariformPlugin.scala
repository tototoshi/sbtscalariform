/*
 * Copyright 2011 Typesafe Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.typesafe.sbtscalariform

import sbt._
import sbt.Keys._
import scalariform.formatter.ScalaFormatter
import scalariform.formatter.preferences.{ FormattingPreferences, IFormattingPreferences }
import scalariform.parser.ScalaParserException

object ScalariformPlugin extends Plugin {

  def scalariformSettings: Seq[Setting[_]] = {
    import ScalariformKeys._
    defaultScalariformSettings ++ Seq(
      compileInputs in Compile <<= (compileInputs in Compile) dependsOn (format in Compile),
      compileInputs in Test <<= (compileInputs in Test) dependsOn (format in Test)
    )
  }

  def defaultScalariformSettings: Seq[Setting[_]] = {
    val needToBeScoped = needToBeScopedScalariformSettings
    noNeedToBeScopedScalariformSettings ++ inConfig(Compile)(needToBeScoped) ++ inConfig(Test)(needToBeScoped)
  }

  def needToBeScopedScalariformSettings: Seq[Setting[_]] = {
    import ScalariformKeys._
    List(
      unmanagedSourceDirectories in format <<= Seq(scalaSource).join,
      format <<= (
        preferences,
        unmanagedSourceDirectories in format,
        includeFilter in format,
        excludeFilter in format,
        thisProjectRef,
        configuration,
        cacheDirectory,
        streams
      ) map formatTask
    )
  }

  def noNeedToBeScopedScalariformSettings: Seq[Setting[_]] = {
    import ScalariformKeys._
    List(
      preferences := defaultPreferences,
      includeFilter in format := "*.scala"
    )
  }

  private def defaultPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences().
      setPreference(DoubleIndentClassDeclaration, true).
      setPreference(PreserveDanglingCloseParenthesis, true)
  }

  private def formatTask(
    preferences: IFormattingPreferences,
    sourceDirectories: Seq[File],
    includeFilter: FileFilter,
    excludeFilter: FileFilter,
    ref: ProjectRef,
    configuration: Configuration,
    cacheDirectory: File,
    streams: TaskStreams) = {
    try {
      val files = sourceDirectories.descendantsExcept(includeFilter, excludeFilter).get.toSet
      val cache = cacheDirectory / "scalariform"
      val logFun = log("%s(%s)".format(Project.display(ref), configuration), streams.log) _
      handleFiles(files, cache, logFun("Formatting %s %s ..."), performFormat(preferences))
      handleFiles(files, cache, logFun("Reformatted %s %s."), _ => ()).toSeq // recalculate cache because we're formatting in-place
    } catch {
      case e: ScalaParserException =>
        streams.log.error("Scalariform parser error: see compile for details")
        Nil
    }
  }

  private def log(label: String, logger: Logger)(message: String)(count: String) =
    logger.info(message.format(count, label))

  private def handleFiles(
    files: Set[File],
    cache: File,
    logFun: String => Unit,
    updateFun: Set[File] => Unit) =
    FileFunction.cached(cache)(FilesInfo.hash, FilesInfo.exists)(handleUpdate(logFun, updateFun))(files)

  private def performFormat(preferences: IFormattingPreferences)(files: Set[File]) =
    for (file <- files if file.exists) {
      val contents = IO.read(file)
      val formatted = ScalaFormatter.format(contents, preferences)
      if (formatted != contents) IO.write(file, formatted)
    }

  private def handleUpdate(
    logFun: String => Unit,
    updateFun: Set[File] => Unit)(
      in: ChangeReport[File],
      out: ChangeReport[File]) = {
    val files = in.modified -- in.removed
    Util.counted("Scala source", "", "s", files.size) foreach logFun
    updateFun(files)
    files
  }

  object ScalariformKeys {

    val format: TaskKey[Seq[File]] =
      TaskKey[Seq[File]](prefix("format"), "Format (Scala) sources using scalariform.")

    val preferences: SettingKey[IFormattingPreferences] =
      SettingKey[IFormattingPreferences](prefix("preferences"), "Scalariform formatting preferences, e.g. indentation.")

    private def prefix(key: String) = "scalariform-" + key
  }
}
