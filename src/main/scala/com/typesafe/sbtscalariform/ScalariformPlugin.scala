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

import java.io.File
import sbt._
import sbt.Keys._
import sbt.Project.Setting
import scalariform.formatter.ScalaFormatter
import scalariform.formatter.preferences.{ DoubleIndentClassDeclaration, FormattingPreferences, IFormattingPreferences }
import scalariform.parser.ScalaParserException

object ScalariformPlugin {

  object ScalariformKeys {

    val formatSourceDirectories: SettingKey[Seq[File]] =
      SettingKey[Seq[File]]("format-source-directories")

    val formatSourceFilter: SettingKey[FileFilter] =
      SettingKey[FileFilter]("format-source-filter")

    val formatSources: TaskKey[Seq[File]] =
      TaskKey[Seq[File]]("format-sources")

    val formatPreferences: SettingKey[IFormattingPreferences] =
      SettingKey[IFormattingPreferences]("format-preferences")

    val format: TaskKey[Seq[File]] =
      TaskKey[Seq[File]]("format", "Format scala sources using scalariform")
  }

  import ScalariformKeys._

  def scalariformSettings: Seq[Setting[_]] =
    inConfig(Compile)(baseScalariformSettings) ++ inConfig(Test)(baseScalariformSettings) ++ Seq(
      compileInputs in Compile <<= (compileInputs in Compile) dependsOn (format in Compile),
      compileInputs in Test <<= (compileInputs in Test) dependsOn (format in Test))

  def baseScalariformSettings: Seq[Setting[_]] =
    Seq(
      formatSourceDirectories <<= Seq(scalaSource).join,
      formatSourceFilter := "*.scala",
      formatSources <<= collectSourceFiles,
      formatPreferences := FormattingPreferences().setPreference(DoubleIndentClassDeclaration, true),
      format <<= formatTask)

  private def collectSourceFiles =
    (formatSourceDirectories, formatSourceFilter, excludeFilter in formatSources) map {
      (dirs, filter, excludes) => dirs.descendentsExcept(filter, excludes).get
    }

  private def formatTask =
    (formatSources, formatPreferences, thisProjectRef, configuration, cacheDirectory, streams) map {
      (sources, preferences, ref, config, cacheDir, s) =>
        {
          val label = "%s(%s)".format(Project.display(ref), config)
          val cache = cacheDir / "format"
          val logFormatting = (count: String) => s.log.info("Formatting %s %s...".format(count, label))
          val logReformatted = (count: String) => s.log.info("Reformatted %s %s".format(count, label))
          val formatting = cached(cache, logFormatting) { files =>
            for (file <- files if file.exists) {
              val contents = IO.read(file)
              val formatted = ScalaFormatter.format(contents, preferences)
              if (formatted != contents) IO.write(file, formatted)
            }
          }
          val reformatted = cached(cache, logReformatted) { _ => () }
          try {
            formatting(sources.toSet)
            reformatted(sources.toSet).toSeq // recalculate cache because we're formatting in-place
          } catch {
            case e: ScalaParserException =>
              s.log.error("Scalariform parser error: see compile for details")
              Nil
          }
        }
    }

  private def cached(cache: File, log: String => Unit)(update: Set[File] => Unit) = {
    FileFunction.cached(cache)(FilesInfo.hash, FilesInfo.exists) { (in, out) =>
      val files = in.modified -- in.removed
      Util.counted("Scala source", "", "s", files.size) foreach log
      update(files)
      files
    }
  }
}
