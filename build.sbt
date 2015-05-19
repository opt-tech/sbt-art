lazy val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1.0",
  organization := "jp.demand-side-science",
  organizationName := "Demand Side Science Inc.",
  startYear := Some(2015)
)

lazy val publishSettings: Seq[Setting[_]] = Seq(
  sonatypeProfileName := "jp.demand-side-science",
  homepage := Some(url("https://github.com/demand-side-science/sbt-art")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  useGpg := true,
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
      <url>github.com:demand-side-science/sbt-art.git</url>
      <connection>scm:git:git@github.com:demand-side-science/sbt-art.git</connection>
    </scm>
      <developers>
        <developer>
          <id>mogproject</id>
          <name>Yosuke Mizutani</name>
          <url>http://demand-side-science.jp</url>
        </developer>
      </developers>)
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    sbtPlugin := true,
    name := "sbt-art",
    description := "artifact-cli plugin for sbt",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )
