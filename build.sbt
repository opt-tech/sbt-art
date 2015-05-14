lazy val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1.0",
  organization := "jp.demandsidescience",
  organizationName := "Demand Side Science",
  startYear := Some(2015),
  homepage := Some(url("https://github.com/demand-side-science/sbt-art"))

)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    sbtPlugin := true,
    name := "sbt-art",
    description := "artifact-cli plugin for sbt",
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    scalacOptions ++= Seq("-deprecation", "-unchecked")
  )