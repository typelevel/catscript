# Reading and writing

In this section, we will guide you through the process of opening and extracting information from files using our simple, intuitive API. Whether you are working with text, binary, or other formats, you will find the tools you need to seamlessly integrate file reading into your Scala project. Let us dive in and unlock the power of data stored in files!


## Reading and printing a file

Let's say you want to load the contents of a file in memory...

The first thing you need to do is import the `read` method and the `Path` type:

@:select(api-style)

@:choice(syntax)

```scala
import catscript.syntax.path.*
```

@:choice(static)

```scala
import catscript.Catscript
```

@:@

Next, define the `run` function to execute the `IO`. To do that, you need to extend your application with a `cats.effect.IOApp`, let us name it `App`:

```scala mdoc:compile-only
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  def run: IO[Unit] = ???

end App
```

Now we can start using the library! First, create a `Path` containing the path to the file you want to read:

```scala mdoc:compile-only
import fs2.io.file.Path 
val path = Path("testdata/readme.txt")
```

And use the `read` function to load the file in memory as a string:

@:select(api-style)

@:choice(syntax)

```scala mdoc:fail
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/readme.txt")

  def run: IO[Unit] = path.read

end App
```

@:choice(static)

```scala mdoc:fail
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/readme.txt")

  def run: IO[Unit] = Catscript.read(path)

end App
```

@:@

Oops! We got an error saying that the `run` function accepts an `IO[Unit]`, but the `read` function returns an `IO[String]`. This happens because we are not doing anything with the string and therefore not returning `IO[Unit]`. Let's fix that by sequencing the value obtained from `read` with another that prints it to the console (and thus returns `IO[Unit]`). This can be achieved using the `flatMap` function as follows:  

@:select(api-style)

@:choice(syntax)

```scala
path.read.flatMap(file => IO(println(file)))
```

@:choice(static)

```scala
Catscript.read(path).flatMap(file => IO(println(file)))
```

@:@

What is happening above is that we are calling the `flatMap` method and passing as parameter a function describing what we want to do with the `file` inside the `IO`, in this case, passing it to the computation `IO(println(file))`.  

Now pass the program to the `run` method and everything should go nicely:


@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
import org.typelevel.catscript.syntax.path.*
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/readme.txt")

  def run: IO[Unit] = path.read.flatMap(file => IO(println(file)))

end App
```

@:choice(static)

```scala mdoc:compile-only
import org.typelevel.catscript.Catscript
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/readme.txt")

  def run: IO[Unit] = Catscript.read(path).flatMap(file => IO(println(file)))

end App
```

@:@

Congratulations! You have just loaded the contents of a file in pure Scala 🎉.

### Exercise

You may not like to keep using the `flatMap` function over and over again to sequence computations. This is why there is the [`for` construct](https://docs.scala-lang.org/scala3/book/control-structures.html#for-expressions) to automatically let the compiler write the `flatMap`s for you. Why don't you try rewriting the program we just did using for-comprehensions? 

```scala
def run: IO[Unit] =
  for
    // Complete your code here!
    ...
  yield ()
```
[See solution](../examples/solutions.md#reading-and-printing-a-file)

## Writing and modifying the contents of a file

Now that you know how to load a file, you might also want to modify it and save it.

To write to a file, use the `write` function to save the contents to a new file. Here, we reverse the file so it reads backwards:  

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
import org.typelevel.catscript.syntax.path.*
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/change_me.txt")

  def run: IO[Unit] =
    for
      file <- path.read
      reversedFile = file.reverse
      _ <- path.write(reversedFile)
    yield ()

end App
```

@:choice(static)

```scala mdoc:compile-only
import org.typelevel.catscript.Catscript
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:

  val path = Path("testdata/change_me.txt")

  def run: IO[Unit] =
    for
      file <- Catscript.read(path)
      reversedFile = file.reverse
      _ <- Catscript.write(path, reversedFile)
    yield ()

end App
```

@:@

Be aware that this will overwrite the contents of the file. So be careful not to change important files while you are learning!

### Exercise

Try loading the contents of two different files, concatenating them, and saving the result to a third location. How would you do it?

[See possible solution](../examples/solutions.md#writing-and-modifying-the-contents-of-a-file)

## Working line by line

Catscript provides a method called `readLines`, which reads the file line by line and stores them on a `List[String]`. This comes handy when you are working with a list of things that you want to convert:  

`testdata/names.data`

```
Alex
Jamie
Morgan
Riley
Taylor
Casey
River
```

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
import org.typelevel.catscript.syntax.path.*
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object Names extends IOApp.Simple:

  case class Name(value: String)

  val namesPath = Path("testdata/names.data")

  def run: IO[Unit] =
    for
      lines <- namesPath.readLines      // (1)
      names = lines.map(Name(_))        // (2)
      _ <- IO(names.foreach(println))   // (3)
    yield ()

end Names
```

@:choice(static)

```scala mdoc:compile-only
import org.typelevel.catscript.Catscript
import fs2.io.file.Path
import cats.effect.{IO, IOApp}

object Names extends IOApp.Simple:

  case class Name(value: String)

  val namesPath = Path("testdata/names.data")

  def run: IO[Unit] =
    for
      lines <- Catscript.readLines(namesPath)   // (1)
      names = lines.map(Name(_))                // (2)
      _ <- IO(names.foreach(println))           // (3)
    yield ()

end Names
```

@:@

Here is what's happening:

**(1)** Load the list of names as `List`  

**(2)** Convert it to `Name`  

**(3)** Print the list to the console


### Exercise

Write a function that reads a file into lines, adds a blank line between the lines and saves the result in a different file. For example given the following input:  

_`testdata/edgar_allan_poe/no_spaced_dream.txt`_

```
Take this kiss upon the brow!
And, in parting from you now,
Thus much let me avow —
You are not wrong, who deem
That my days have been a dream;
Yet if hope has flown away
In a night, or in a day,
In a vision, or in none,
Is it therefore the less gone?
All that we see or seem
Is but a dream within a dream.
```

Your program should output the following:  

_`testdata/edgar_allan_poe/spaced_dream.txt`_

```
Take this kiss upon the brow!

And, in parting from you now,

Thus much let me avow —

You are not wrong, who deem

That my days have been a dream;

Yet if hope has flown away

In a night, or in a day,

In a vision, or in none,

Is it therefore the less gone?

All that we see or seem

Is but a dream within a dream.
```

How would you do this? (hint: use `writeLines`).

[See possible solution](../examples/solutions.md#working-line-by-line)
