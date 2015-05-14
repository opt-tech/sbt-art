package jp.demandsidescience.sbtart

import sbt.{File, Process, ProcessBuilder, Logger, ProcessLogger}


case class ArtifactCli(prefix: List[String] = List("art"))(implicit val log: Logger) {

  def version(): Unit = runArtCommand("--version")

  def list(groupId: String): Unit = runArtCommand("list", groupId)

  def info(groupId: String, target: File, revision: String): Unit =
    withEmptyTargetSkipped(target)(runArtCommand("info", groupId, target.getAbsolutePath, revision))

  def upload(groupId: String, target: File): Unit =
    withEmptyTargetSkipped(target)(runArtCommand("--force", "upload", groupId, target.getAbsolutePath))

  def withConfig(config: File): ArtifactCli = {
    require(!prefix.contains("--config"), s"Already set config option. (prefix=${prefix})")
    copy(prefix = prefix ::: List("--config", config.getAbsolutePath))
  }


  private[sbtart] def runSystemCommand(command: ProcessBuilder, output: Boolean = true): Int =
    if (output) command.! else command ! NullLogger


  private def runArtCommand(args: String*): Unit = withInstallVerified {
    val command = Process(prefix ::: args.toList)
    log.info(s"Executing: ${command}")

    withExitCodeVerified(runSystemCommand(command))(x => throw new CommandFailedError(x))
  }

  private def withEmptyTargetSkipped(target: File)(thunk: => Unit): Unit = {
    if (target.getPath.isEmpty)
      log.info("Key 'artTarget' is missing. Skipped.")
    else
      thunk
  }

  private def withExitCodeVerified(thunk: => Int)(whenFailed: Int => Unit): Unit = {
    val ret = thunk
    if (ret != 0) whenFailed(ret)
  }

  private def withInstallVerified(thunk: => Unit): Unit = {
    withExitCodeVerified(runSystemCommand(Process("art --version"), output = false)) { _ =>
      log.error(Seq(
        "",
        "'artifact-cli' is not installed properly in your computer.",
        "To use this plugin, please run the following command ('Python' and 'pip' are required):",
        "",
        "    pip install artifact-cli",
        "",
        ""
      ).mkString("\n"))

      throw new NotInstalledError
    }
    thunk
  }

  private object NullLogger extends ProcessLogger {
    override def info(s: => String): Unit = {}

    override def error(s: => String): Unit = {}

    override def buffer[T](f: => T): T = f
  }

}

class NotInstalledError extends RuntimeException("Not installed artifact-cli.")

class CommandFailedError(exitCode: Int) extends RuntimeException(s"Failed to execute command. (exitCode=${exitCode})")
