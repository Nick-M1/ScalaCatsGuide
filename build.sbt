
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaCatsProject"
  )


libraryDependencies ++= Seq(

  // CATS
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-kernel" % "2.7.0",

  // CATS-EFFECTS
  "org.typelevel" %% "cats-effect" % "3.3.12"


)


ThisBuild / scalacOptions ++= Seq(
  "-Ykind-projector"
)

