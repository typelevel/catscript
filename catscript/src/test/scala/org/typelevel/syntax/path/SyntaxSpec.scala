package org.typelevel.catscript
package syntax 

import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

import org.scalacheck.Gen

import syntax.path.*

object SyntaxSpec extends SimpleIOSuite with Checkers {

  test(
    "Extension methods for read, write and append should work the same as the normal ones"
  ) {
    forall(Gen.asciiStr) { contents =>
      withTempFile { path =>
        for {
          _  <- path.write(contents)
          _  <- path.append("Hi from the test!")
          s1 <- path.read
          _  <- Catscript.write(path, contents)
          _  <- Catscript.append(path, "Hi from the test!")
          s2 <- Catscript.read(path)
        } yield expect.same(s1, s2)
      }
    }
  }

  test(
    "Extension methods for creating and deleting a file should work the same as the normal ones"
  ) {
    withTempDirectory { dir =>
      val path = dir / "sample.txt"
      for {
        _        <- path.createFile
        exists   <- path.exists
        deleted  <- path.deleteIfExists
        _        <- Catscript.createFile(path)
        exists2  <- Catscript.exists(path)
        deleted2 <- Catscript.deleteIfExists(path)
      } yield expect(exists && deleted) and expect(exists2 && deleted2)
    }
  }

  test(
    "Extension methods for copying a file should work the same as the normal ones"
  ) {
    forall(Gen.asciiStr) { contents =>
      withTempDirectory { dir =>
        val original = dir / "sample.txt"
        val copy1    = dir / "sample-copy.txt"
        val copy2    = dir / "sample-copy-jo2.txt"
        for {
          _  <- original.write(contents)
          _  <- original.copy(copy1)
          _  <- Catscript.copy(original, copy2)
          s1 <- copy1.read
          s2 <- copy2.read
        } yield expect.same(s1, s2)
      }
    }
  }

  test(
    "Extension methods for moving a file should work the same as the normal ones"
  ) {

    forall(Gen.asciiStr) { contents =>
      withTempDirectory { dir =>
        val original = dir / "sample.txt"
        val moved    = dir / "sample-moved.txt"
        for {
          _      <- original.write(contents)
          _      <- original.move(moved)
          exists <- moved.exists

          _ <- moved.delete

          _       <- Catscript.write(original, contents)
          _       <- Catscript.move(original, moved)
          exists2 <- moved.exists
        } yield expect(exists && exists2)
      }
    }
  }

}
