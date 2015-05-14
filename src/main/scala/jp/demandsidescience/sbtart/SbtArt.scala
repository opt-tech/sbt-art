package jp.demandsidescience.sbtart

import sbt._
import sbt.Keys.streams
import sbt.Def.Initialize


object SbtArt {

  import SbtArtPlugin.autoImport._

  def versionTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    implicit val log = (streams in key).value.log
    ArtifactCli().version()
  }

  def listTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    implicit val log = (streams in key).value.log
    ArtifactCli().withConfig((artConfig in key).value).list((artGroupId in key).value)
  }

  def infoTask(key: TaskKey[Unit]): Initialize[InputTask[Unit]] = Def.inputTask {
    val args = Def.spaceDelimited("<revision>").parsed
    val revision = args.headOption.getOrElse("latest")

    implicit val log = (streams in key).value.log
    ArtifactCli()
      .withConfig((artConfig in key).value).info((artGroupId in key).value, (artTarget in key).value, revision)
  }

  def uploadTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    implicit val log = (streams in key).value.log
    ArtifactCli().withConfig((artConfig in key).value).upload((artGroupId in key).value, (artTarget in key).value)
  }

}
