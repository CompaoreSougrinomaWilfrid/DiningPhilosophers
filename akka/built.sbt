name := "DiningPhilosophers"

version := "0.1"
ThisBuild / scalaVersion := "2.13.12"

// Library dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19" // Akka Typed actors:contentReference[oaicite:5]{index=5}
  // If you want to use classic Akka actors instead, use "akka-actor" module:
  // "com.typesafe.akka" %% "akka-actor" % "2.6.19"
)




// Enable logging with SLF4J backend 
//enablePlugins(JavaAppPackaging)
