# Handling files and doing operations

Beyond simple file reading and writing operations, Catscript allows you to interact directly with the file system. There are functions for creating and deleting files and directories, creating temporary files for short-term use and managing file and directory permissions for added control, among other useful methods.

## Creating files and directories

In this section, you will see how to create new files and directories, as well as delete existing ones.

### `createFile`    

Creates a new file in the specified path, failing if the parent directory does not exist. It optionally accepts file permissions. To see what the `exists` function does, see [the reference](#exists):  

```scala mdoc:invisible
// This section adds every import to the code snippets

import cats.effect.IO
import cats.syntax.all.*

import fs2.Stream
import fs2.io.file.{Path, Files}

import org.typelevel.catscript
import catscript.syntax.path.*
import catscript.Catscript

val path = Path("testdata/dummy.something")
```

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
import catscript.syntax.path.*

val path = Path("path/to/create/file.txt")

path.createFile >> path.exists // Should return true
```

@:choice(static)

```scala mdoc:compile-only
import catscript.Catscript

val path = Path("path/to/create/file.txt")

Catscript.createFile(path) >> Catscript.exists(path) // Should return true
```

@:choice(fs2)

```scala mdoc:compile-only
import fs2.io.file.Files

val path = Path("path/to/create/file.txt")

Files[IO].createFile(path) >> Files[IO].exists(path) // Should return true
```

@:@

### `createDirectories`

Creates all the directories in the path, with the default permissions or with the supplied ones:

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
val directories = Path("here/are/some/dirs")
val path = directories / Path("file.txt")

directories.createDirectories >> path.createFile
```

@:choice(static)

```scala mdoc:compile-only
val directories = Path("here/are/some/dirs")
val path = directories / Path("file.txt")

Catscript.createDirectories(directories) >> Catscript.createFile(path)
```

@:choice(fs2)

```scala mdoc:compile-only
val directories = Path("here/are/some/dirs")
val path = directories / Path("file.txt")

Files[IO].createDirectories(directories) >> Files[IO].createFile(path)
```

@:@

### `createTempFile`

This function creates a temporary file that gets automatically deleted. It optionally accepts multiple parameters such as a directory, a prefix (to the name of the file), a suffix (like the extension of the file) and permissions. It returns the path of the newly created file:  


@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
for 
  path <- createTempFile

  // It's going to be deleted eventually!
  _ <- path.write("I don't wanna go!") 
yield ()
```

@:choice(static)

```scala mdoc:compile-only
for 
  path <- Catscript.createTempFile
  
  // It's going to be deleted eventually!
  _ <- Catscript.write(path, "I don't wanna go!") 
yield ()
```

@:choice(fs2)

```scala mdoc:compile-only
Stream.eval(Files[IO].createTempFile)
  .flatMap( path => 
    Stream.emit("I don't wanna go!")
      .through(Files[IO].writeUtf8(path))
  )
  .compile
  .drain
```

@:@

### `withTempFile`

Very similar to `createTempFile`, but Cats Effect handles the deletion of the file by itself. Accepts the same parameters as a custom directory, a prefix, a suffix, and some permissions but takes a `use` function as well. This function is a description of how the path will be used and what will be computed after that:

@:select(api-style)

@:choice(syntax)

```scala 3 mdoc:compile-only
withTempFile: path =>
  path.write("I have accepted my fate...")
```

@:choice(static)

```scala 3 mdoc:compile-only
Catscript.withTempFile: path =>
  Catscript.write(path, "I have accepted my fate...")
```

@:choice(fs2)

```scala mdoc:compile-only
Files[IO].tempFile.use: path =>
  Stream.emit("I have accepted my fate...")
   .through(Files[IO].writeUtf8(path))
   .compile
   .drain
```

@:@

### `createTempDirectory`

Creates a temporary directory that will eventually be deleted by the operating system. It accepts a few optional parameters like a custom parent directory, a prefix, and some permissions. It returns the path to the newly created directory:

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
for 
  dir <- createTempDirectory
  _ <- (dir / "tempfile.tmp").createFile
yield ()
```

@:choice(static)

```scala mdoc:compile-only
for 
  dir <- Catscript.createTempDirectory
  _ <- Catscript.createFile(dir / "tempfile.tmp")
yield ()

```

@:choice(fs2)

```scala mdoc:compile-only
for 
  dir <- Files[IO].createTempDirectory
  _   <- Files[IO].createFile(dir / "tempfile.tmp")
yield ()

```

@:@

### `withTempDirectory`

Similar to `createTempDirectory`, but the deletion of the directory is managed by Cats Effect. Takes the same arguments as a custom directory, a prefix and some permissions and most importantly, a `use` function that describes how the directory will be used and computed:

@:select(api-style)

@:choice(syntax)

```scala 3 mdoc:compile-only
withTempDirectory: dir => 
  (dir / "its_going_to_go_soon.mp3").createFile
```

@:choice(static)

```scala 3 mdoc:compile-only
Catscript.withTempDirectory: dir =>
  Catscript.createFile(dir / "its_going_to_go_soon.mp3")
```

@:choice(fs2)

```scala mdoc:compile-only
Files[IO].tempDirectory.use: dir => 
  Files[IO].createFile(dir / "its_going_to_go_soon.mp3")

```

@:@

### `createSymbolicLink`

Creates a [Symbolic Link](https://en.wikipedia.org/wiki/Symbolic_link) to a file. Requires the destination of the symlink and the path of the target file to link, and optionally some permissions:

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
val linkPath   = Path("store/the/link/here/symlink")
val targetPath = Path("path/to/file/to/target.sh")

linkPath.createSymbolicLink(targetPath)
```

@:choice(static)

```scala mdoc:compile-only
val linkPath   = Path("store/the/link/here/symlink")
val targetPath = Path("path/to/file/to/target.sh")

Catscript.createSymbolicLink(linkPath, targetPath)
```

@:choice(fs2)

```scala mdoc:compile-only
val linkPath   = Path("store/the/link/here/symlink")
val targetPath = Path("path/to/file/to/target.sh")

Files[IO].createSymbolicLink(linkPath, targetPath)
```

@:@

## Deleting files and directories

### `delete`

Deletes a file or empty directory that must exist (otherwise it will fail). 

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
path.createFile >> 
  path.write("TOP SECRET 🚫, MUST DELETE") >>
    path.delete
```

@:choice(static)

```scala mdoc:compile-only
Catscript.createFile(path) >>
  Catscript.write(path, "TOP SECRET 🚫, MUST DELETE") >>
    Catscript.delete(path)
```

@:choice(fs2)

```scala mdoc:compile-only
for
  _ <- Files[IO].createFile(path)

  _ <- Stream.emit("TOP SECRET 🚫, MUST DELETE")
        .through(Files[IO].writeUtf8(path))
        .compile
        .drain

  _ <- Files[IO].delete(path)
yield ()
```

@:@

### `deleteIfExists`

Similar to `delete`, but returns `true` if the deletion was successful instead of failing: 

@:select(api-style)

@:choice(syntax)

```scala mdoc:compile-only
path.deleteIfExists >>= 
  (deleted => IO.println(s"Was the file deleted? $deleted"))
```

@:choice(static)

```scala mdoc:compile-only
Catscript.deleteIfExists(path) >>= 
  (deleted => IO.println(s"Was the file deleted? $deleted"))
```

@:choice(fs2)

```scala mdoc:compile-only
Files[IO].deleteIfExists(path) >>= 
  (deleted => IO.println(s"Was the file deleted? $deleted"))
```

@:@

### `deleteRecursively`

With the previous functions, the directory had to be empty to be deleted. The difference with this method is that it recursively deletes all files or folders contained inside it, optionally following symbolic links if specified.

Note that, unlike the previous functions, this one will not fail if the directories are empty or do not exist:

@:select(api-style)

@:choice(syntax)

```scala 3
val dirs = Path("this/folders/will/be/created/and/deleted")

for 
  _ <- dirs.createDirectories
  _ <- dirs.deleteRecursively // Will delete all of them!
yield ()
```

@:choice(static)

```scala 3
val dirs = Path("this/folders/will/be/created/and/deleted")

for 
  _ <- Catscript.createDirectories(dirs)
  _ <- Catscript.deleteRecursively(dirs) // Will delete all of them!
yield ()
```

@:choice(fs2)

```scala 3

val dirs = Path("this/folders/will/be/created/and/deleted")

for 
  _ <- Files[IO].createDirectories(dirs)
  _ <- Files[IO].deleteRecursively(dirs) // Will delete all of them!
yield ()
```

@:@


## File operations

catscript provides essential functions for renaming, moving, and copying files, allowing you to efficiently manage your data. These are especially useful in scripting scenarios.  

### `copy`

Copies a file from a source path to a target path. The method will fail if the destination path already exists; to avoid this behaviour, you can, for example, pass flags to replace the contents at destination:  


@:select(api-style)

@:choice(syntax)

```scala 3
val source = Path("source/file/secret.txt")
val target = Path("target/dir/not_so_secret.txt")

for 
  _ <- source.write("The no-cloning theorem says you can't copy me!")
  _ <- source.copy(target)
yield ()
```

@:choice(static)

```scala 3
val source = Path("source/file/secret.txt")
val target = Path("target/dir/not_so_secret.txt")

for 
  _ <- Catscript.write(source, "The no-cloning theorem says you can't copy me!")
  _ <- Catscript.copy(source, target)
yield ()
```

@:choice(fs2)

```scala 3
val source = Path("source/file/secret.txt")
val target = Path("target/dir/not_so_secret.txt")

Stream.emit("The no-cloning theorem says you can't copy me!")
  .through(Files[IO].writeUtf8(source))
  .evalTap(_ => Files[IO].copy(source, target))
  .compile
  .drain
```

@:@

### `move`

Very similar to `copy`, but deletes the file in the original destination path. Optionally takes flags as arguments to define its move behaviour:

@:select(api-style)

@:choice(syntax)

```scala 3
val source = Path("i/cant/move.mp4")
val target = Path("teleporting/around/movie.mp4")

source.move(target)
```

@:choice(static)

```scala 3
val source = Path("i/cant/move.mp4")
val target = Path("teleporting/around/movie.mp4")

Catscript.move(source, target)
```

@:choice(fs2)

```scala 3
val source = Path("i/cant/move.mp4")
val target = Path("teleporting/around/movie.mp4")

Files[IO].move(source, target)
```

@:@

### `exists`

This function checks whether a file exists at a specified path:

@:select(api-style)

@:choice(syntax)

```scala 3
import cats.syntax.all.* // for the whenA method

val source = Path("need/to/ve/copied/bin.sha256")
val target = Path("need/to/be/deleted/bin.sha254")

for 
  _ <- target.delete // Delete before copying to avoid errors (and flags)
  exists <- target.exists
  _ <- source.copy(target).whenA(exists)
yield ()
```

@:choice(static)

```scala 3
import cats.syntax.all.* // for the whenA method

val source = Path("need/to/ve/copied/bin.sha256")
val target = Path("need/to/be/deleted/bin.sha254")

for 
  _ <- Catscript.delete(target) // Delete before copying to avoid errors (and flags)
  exists <- Catscript.exists(target)
  _ <- Catscript.copy(source, target).whenA(exists)
yield ()
```

@:choice(fs2)

```scala 3
import cats.syntax.all.* // for the whenA method

val source = Path("need/to/ve/copied/bin.sha256")
val target = Path("need/to/be/deleted/bin.sha254")

for 
  _ <- Files[IO].delete(target) // Delete before copying to avoid errors (and flags)
  exists <- Files[IO].exists(target)
  _ <- Files[IO].copy(source, target).whenA(exists)
yield ()
```

@:@


