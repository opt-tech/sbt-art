package jp.demandsidescience.sbtart

import sbt.File

import org.scalatest.{Inside, FlatSpec, MustMatchers}
import scala.collection.mutable

/** Mock system command executor */
class ArtifactCliTest(commandInstalled: Boolean,
                      exitCode: Int,
                      val history: mutable.ListBuffer[(String, Boolean)] = mutable.ListBuffer.empty,
                      prefix: List[String] = List("art"))
                     (implicit log: sbt.Logger) extends ArtifactCli(prefix) {

  override private[sbtart] def runSystemCommand(command: sbt.ProcessBuilder, output: Boolean): Int = {
    history += command.toString -> output

    val args = command.toString.drop(1).dropRight(1).split(", ").toList
    args match {
      case "art" :: _ if !commandInstalled => 127
      case "art" :: "--version" :: Nil => 0
      case "art" :: "--config" :: _ :: "list" :: _ :: Nil => exitCode
      case "art" :: "--config" :: _ :: "info" :: _ :: _ :: _ :: Nil => exitCode
      case "art" :: "--config" :: _ :: "--force" :: "upload" :: _ :: _ :: Nil => exitCode
      case "art" :: _ => 1
      case _ => 2
    }
  }

  override def withConfig(config: File): ArtifactCliTest =
    new ArtifactCliTest(commandInstalled, exitCode, history, super.withConfig(config).prefix)(log)

}

/** Mock logger */
class TestLogger extends sbt.Logger {
  val traceHistory: mutable.ListBuffer[Throwable] = mutable.ListBuffer.empty
  val successHistory: mutable.ListBuffer[String] = mutable.ListBuffer.empty
  val logHistory: mutable.ListBuffer[(sbt.Level.Value, String)] = mutable.ListBuffer.empty

  def trace(t: => Throwable): Unit = traceHistory += t

  def success(message: => String): Unit = successHistory += message

  def log(level: sbt.Level.Value, message: => String): scala.Unit = logHistory += level -> message
}

/** Specifications for ArtifactCli */
class ArtifactCliSpec extends FlatSpec with MustMatchers with Inside {

  class context(commandInstalled: Boolean = true, exitcode: Int = 0) {
    implicit val log = new TestLogger
    val art = new ArtifactCliTest(commandInstalled, exitcode)
    val conf = new File("a/b/c.conf")
    val confAbs = conf.getAbsolutePath
    val target = new File("target-test-0.1.jar")
    val targetAbs = target.getAbsolutePath
  }

  "ArtifactCli#version" should "execute art command" in new context() {
    art.version()
    art.history mustBe List("[art, --version]" -> false, "[art, --version]" -> true)
    log.logHistory mustBe List(sbt.Level.Info -> "Executing: [art, --version]")
  }
  it should "throw exception when artifact-cli is not installed" in new context(false) {
    val t = the[NotInstalledError] thrownBy art.version()
    t.getMessage mustBe "Not installed artifact-cli."
    log.logHistory mustBe List(sbt.Level.Error -> Seq(
      "",
      "'artifact-cli' is not installed properly in your computer.",
      "To use this plugin, please run the following command ('Python' and 'pip' are required):",
      "",
      "    pip install artifact-cli",
      "",
      ""
    ).mkString("\n"))
  }

  "ArtifactCli#list" should "execute art command" in new context() {
    art.withConfig(conf).list("group-id-test")
    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, list, group-id-test]" -> true)
    log.logHistory mustBe List(
      sbt.Level.Info -> s"Executing: [art, --config, ${confAbs}, list, group-id-test]")
  }
  it should "throw exception when artifact-cli is not installed" in new context(false) {
    the[NotInstalledError] thrownBy art.list("group-id-test")
  }
  it should "throw exception when the exit code is non-zero" in new context(true, 2) {
    val t = the[CommandFailedError] thrownBy art.withConfig(conf).list("group-id-test")
    t.getMessage mustBe "Failed to execute command. (exitCode=2)"

    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, list, group-id-test]" -> true)
  }

  "ArtifactCli#info" should "execute art command" in new context() {
    art.withConfig(conf).info("group-id-test", target, "latest")
    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, info, group-id-test, ${targetAbs}, latest]" -> true)
    log.logHistory mustBe List(
      sbt.Level.Info -> s"Executing: [art, --config, ${confAbs}, info, group-id-test, ${targetAbs}, latest]")
  }
  it should "skip when the target is empty" in new context() {
    art.withConfig(conf).info("group-id-test", new File(""), "latest")
    art.history mustBe List.empty
    log.logHistory mustBe List(sbt.Level.Info -> s"Key 'artTarget' is missing. Skipped.")
  }
  it should "throw exception when artifact-cli is not installed" in new context(false) {
    the[NotInstalledError] thrownBy art.info("group-id-test", target, "latest")
  }
  it should "throw exception when the exit code is non-zero" in new context(true, 2) {
    val t = the[CommandFailedError] thrownBy art.withConfig(conf).info("group-id-test", target, "latest")
    t.getMessage mustBe "Failed to execute command. (exitCode=2)"

    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, info, group-id-test, ${targetAbs}, latest]" -> true)
  }

  "ArtifactCli#upload" should "execute art command" in new context() {
    art.withConfig(conf).upload("group-id-test", target)
    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, --force, upload, group-id-test, ${targetAbs}]" -> true)
    log.logHistory mustBe List(
      sbt.Level.Info -> s"Executing: [art, --config, ${confAbs}, --force, upload, group-id-test, ${targetAbs}]")
  }
  it should "skip when the target is empty" in new context() {
    art.withConfig(conf).upload("group-id-test", new File(""))
    art.history mustBe List.empty
    log.logHistory mustBe List(sbt.Level.Info -> s"Key 'artTarget' is missing. Skipped.")
  }
  it should "throw exception when artifact-cli is not installed" in new context(false) {
    the[NotInstalledError] thrownBy art.upload("group-id-test", target)
  }
  it should "throw exception when the exit code is non-zero" in new context(true, 2) {
    val t = the[CommandFailedError] thrownBy art.withConfig(conf).upload("group-id-test", target)
    t.getMessage mustBe "Failed to execute command. (exitCode=2)"

    art.history mustBe List(
      "[art, --version]" -> false,
      s"[art, --config, ${confAbs}, --force, upload, group-id-test, ${targetAbs}]" -> true)
  }

  "ArtifactCli#withConfig" should "throw exception when the config is already set" in new context() {
    val t = the[IllegalArgumentException] thrownBy art.withConfig(conf).withConfig(conf)
    t.getMessage mustBe s"requirement failed: Already set config option. (prefix=List(art, --config, ${confAbs}))"
  }
}
