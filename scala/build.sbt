name := "spark-tutorial-syslog"

version := "1.0"

scalaVersion := "2.10.4"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
	"com.github.nscala-time" %% "nscala-time" % "0.8.0",
  "org.apache.spark" %% "spark-core" % "1.0.0-SNAPSHOT"
)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")