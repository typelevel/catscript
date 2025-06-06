# Catscript 😸
**Making scripts in pure Scala much easier!**

## Getting Started

You can use Catscript in a new or existing Scala 2.13.x or 3.x project by adding it to your `build.sbt` file:

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "catscript" % "@VERSION@"
)
```

## Example
Catscript is a library to perform common script operations such as working with processes and files while maintaining referential transparency! 

```scala 3 mdoc:reset
import cats.effect.{IO, IOApp, ExitCode}

import catscript.*
import catscript.syntax.path.*

object Main extends IOApp: 

  def run(args: List[String]): IO[ExitCode] = 
    for
      home   <- userHome
      config = home / ".catscript" / "config.conf"
      _         <- config.createFile
      _         <- config.write("scripting.made.easy = true")
      newconfig <- config.read
      _         <- IO.println(s"Loading config: $newconfig")
    yield ExitCode.Success
    
end Main
```