package jp.ne.opt.sbtart

import sbt._


object SbtArtPlugin extends sbt.AutoPlugin {

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  object autoImport {
    // Just use for task scoping
    lazy val art = taskKey[Unit]("")

    lazy val artVersion = TaskKey[Unit]("art-version", "Prints the version number of artifact-cli.")
    lazy val artList = TaskKey[Unit]("art-list", "Lists all the artifacts in the group.")
    lazy val artInfo = InputKey[Unit]("art-info", "Shows the information of the specified artifact.")
    lazy val artUpload = TaskKey[Unit]("art-upload", "Uploads the current artifact.")

    lazy val artGroupId = settingKey[String]("Group id for artifact-cli.")
    lazy val artConfig = settingKey[File]("Path to the configuration file for artifact-cli.")

    // Prefers TaskKey to SettingKey because it could depends on another task (e.g. assemblyOutputPath in assembly)
    lazy val artTarget = taskKey[File]("Path to the all-in-one artifact file.")
  }

  import autoImport._

  lazy val baseArtProjectSettings: Seq[Def.Setting[_]] = Seq(
    artVersion := SbtArt.versionTask(art).value,
    artList := SbtArt.listTask(art).value,
    artInfo <<= SbtArt.infoTask(art),
    artUpload := SbtArt.uploadTask(art).value,

    artConfig := {
      val f = new File(sys.env.get("HOME").map(_ + "/.artifact-cli.conf").getOrElse(""))
      if (f.exists) f else new File("project/artifact-cli.conf")
    },
    artGroupId := Keys.organization.value,
    artTarget := new File("")
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseArtProjectSettings

}
