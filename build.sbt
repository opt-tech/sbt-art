enablePlugins(SbtPlugin)
crossSbtVersions := Seq("0.13.18", "1.3.0")

lazy val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1.4",
  organization := "jp.ne.opt",
  organizationName := "Opt, Inc.",
  startYear := Some(2015)
)

lazy val publishSettings: Seq[Setting[_]] = Seq(
  sonatypeProfileName := "jp.ne.opt",
  homepage := Some(url("https://github.com/opt-tech/sbt-art")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false},
  pomExtra := (
    <scm>
      <url>github.com:opt-tech/sbt-art.git</url>
      <connection>scm:git:git@github.com:opt-tech/sbt-art.git</connection>
    </scm>
      <developers>
        <developer>
          <id>arcizan</id>
          <name>arcizan</name>
          <url>https://www.opt.ne.jp/opttechnologies/</url>
        </developer>
      </developers>)
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "sbt-art",
    description := "artifact-cli plugin for sbt",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )
