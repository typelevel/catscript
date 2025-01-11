import laika.sbt.LaikaConfig
import laika.config.*

ThisBuild / tlBaseVersion := "0.0"
ThisBuild / startYear     := Some(2024)
ThisBuild / licenses      := Seq(License.Apache2)
ThisBuild / tlJdkRelease  := Some(11)

ThisBuild / developers := List(
  tlGitHubDev("ChristopherDavenport", "Christopher Davenport"),
  tlGitHubDev("TonioGela", "Antonio Gelameris"),
  tlGitHubDev("Hombre-x", "Gabriel Santana Paredes")
)

ThisBuild / tlSitePublishBranch        := Some("main")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))

ThisBuild / crossScalaVersions := Seq("2.13.15", "3.3.4")

lazy val root = tlCrossRootProject.aggregate(catscript, examples)

lazy val catscript = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("catscript"))
  .settings(
    name := "catscript",
    libraryDependencies ++= List(
      "org.typelevel" %%% "cats-core"      % "2.11.0",
      "org.typelevel" %%% "alleycats-core" % "2.11.0",
      "org.typelevel" %%% "cats-effect"    % "3.5.4",
      "co.fs2"        %%% "fs2-core"       % "3.10.2",
      "co.fs2"        %%% "fs2-io"         % "3.10.2",
      "co.fs2"        %%% "fs2-scodec"     % "3.10.2",
      "org.scodec"    %%% "scodec-bits"    % "1.1.38",
      "org.scodec" %%% "scodec-core" % (if (scalaVersion.value.startsWith("2."))
                                          "1.11.10"
                                        else "2.2.2"),
      // Testing
      "com.disneystreaming" %%% "weaver-cats"       % "0.8.4" % Test,
      "com.disneystreaming" %%% "weaver-scalacheck" % "0.8.4" % Test
    ),
    mimaPreviousArtifacts := Set()
  )

lazy val examples = project
  .in(file("examples"))
  .enablePlugins(NoPublishPlugin)
  .dependsOn(catscript.jvm)
  .settings(
    name                 := "catscript-examples",
    Compile / run / fork := true
  )

lazy val docs = project
  .in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  .dependsOn(catscript.jvm)
  .settings(
    laikaConfig := LaikaConfig.defaults
      .withConfigValue(
        Selections(
          SelectionConfig(
            "api-style",
            ChoiceConfig("syntax", "Syntax"),
            ChoiceConfig("static", "Static"),
            ChoiceConfig("fs2", "Fs2")
          )
        )
      )
  )
