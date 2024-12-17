/*
 * Copyright 2024 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel.catscript
package syntax

import cats.effect.IO

import fs2.io.file.*

import scodec.bits.ByteVector
import scodec.Codec

import scala.concurrent.duration.FiniteDuration
import java.nio.charset.Charset

package object path {


  implicit class FileOps(private val path: Path) extends AnyVal {

    /**
     * Reads the contents of the file at the path using UTF-8 decoding. Returns
     * it as a String loaded in memory.
     *
     * @param path
     *   The path to read from
     * @return
     *   The file loaded in memory as a String
     */
    def read: IO[String] = Catscript.read(path)

    /**
     * Reads the contents of the file at the path using the provided charset.
     * Returns it as a String loaded in memory.
     *
     * @param charset
     *   The charset to use to decode the file
     * @param path
     *   The path to read from
     * @return
     *   The file loaded in memory as a String
     */
    def read(charset: Charset): IO[String] =
      Catscript.read(path, charset)

    /**
     * Reads the contents of the file at the path and returns it as a
     * ByteVector.
     * @param path
     *   The path to read from
     * @return
     *   The file loaded in memory as a ByteVector
     */
    def readBytes: IO[ByteVector] = Catscript.readBytes(path)

    /**
     * Reads the contents of the file at the path using UTF-8 decoding and
     * returns it line by line as a List of Strings. It will ignore any empty
     * characters after the last newline (similar to `wc -l`).
     *
     * @param path
     *   The path to read from
     * @return
     *   The file loaded in memory as a collection of lines of Strings
     */
    def readLines: IO[List[String]] = Catscript.readLines(path)

    /**
     * Reads the contents of the file and deserializes its contents as `A` using
     * the provided codec.
     * @tparam A
     *   The type to read the file as
     * @param path
     *   The path to read from
     * @param Codec[A]
     *   The codec that translates the file contents into the type `A`
     * @return
     *   The file loaded in memory as a type `A`
     */
    def readAs[A: Codec]: IO[A] = Catscript.readAs(path)

    // Write operations:

    /**
     * This function overwrites the contents of the file at the path using UTF-8
     * encoding with the contents provided in form of a entire string loaded in
     * memory.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def write(contents: String): IO[Unit] = Catscript.write(path, contents)

    /**
     * This function overwrites the contents of the file at the path using the
     * provided charset with the contents provided in form of a entire string
     * loaded in memory.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     * @param charset
     *   The charset to use to encode the file
     */
    def write(contents: String, charset: Charset): IO[Unit] =
      Catscript.write(path, contents, charset)

    /**
     * This function overwrites the contents of the file at the path with the
     * contents provided in form of bytes loaded in memory.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def writeBytes(contents: ByteVector): IO[Unit] =
      Catscript.writeBytes(path, contents)

    /**
     * This function overwrites the contents of the file at the path using UTF-8
     * encoding with the contents provided. Each content inside the list is
     * written as a line in the file.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def writeLines(contents: Seq[String]): IO[Unit] =
      Catscript.writeLines(path, contents)

    /**
     * The functions writes the contents of the file at the path with the
     * contents provided and returns the number of bytes written. The codec is
     * used to translate the type A into a ByteVector so it can be parsed into
     * the file.
     *
     * @tparam A
     *   The type of the contents to write
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     * @param Codec[A]
     *   The codec that translates the type A into a ByteVector
     */
    def writeAs[A: Codec](contents: A): IO[Unit] =
      Catscript.writeAs(path, contents)

    /**
     * Similar to `write`, but appends to the file instead of overwriting it.
     * Saves the content at the end of the file in form of a String.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def append(contents: String): IO[Unit] = Catscript.append(path, contents)

    /**
     * Similar to `write`, but appends to the file instead of overwriting it.
     * Saves the content at the end of the file in form of a String using the
     * provided charset.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     * @param charset
     *   The charset to use to encode the contents
     */
    def append(
        contents: String,
        charset: Charset
    ): IO[Unit] =
      Catscript.append(path, contents, charset)

    /**
     * Similar to `write`, but appends to the file instead of overwriting it.
     * Saves the content at the end of the file in form of a ByteVector.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def appendBytes(contents: ByteVector): IO[Unit] =
      Catscript.appendBytes(path, contents)

    /**
     * Similar to `write`, but appends to the file instead of overwriting it.
     * Saves each line of the content at the end of the file in form of a List
     * of Strings.
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def appendLines(contents: Seq[String]): IO[Unit] =
      Catscript.appendLines(path, contents)

    /**
     * Similar to append, but appends a single line to the end file as a newline
     * instead of overwriting it.
     *
     * Equivalent to `path.append('\n' + contents)`
     *
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     */
    def appendLine(contents: String): IO[Unit] =
      Catscript.appendLine(path, contents)

    /**
     * Similar to `write`, but appends to the file instead of overwriting it
     * using the given `Codec[A]`
     *
     * @tparam A
     *   The type of the contents to write
     * @param path
     *   The path to write to
     * @param contents
     *   The contents to write to the file
     * @param Codec[A]
     *   The codec that translates the type A into a ByteVector
     */
    def appendAs[A: Codec](contents: A): IO[Unit] =
      Catscript.appendAs[A](path, contents)

    // File operations:

    /**
     * Copies the source to the target, failing if source does not exist or the
     * target already exists. To replace the existing instead, use
     * `path.copy(target, CopyFlags(CopyFlag.ReplaceExisting))`.
     */
    def copy(target: Path): IO[Unit] =
      Catscript.copy(path, target, CopyFlags.empty)

    /**
     * Copies the source to the target, following any directives supplied in the
     * flags. By default, an error occurs if the target already exists, though
     * this can be overridden via CopyFlag.ReplaceExisting.
     */
    def copy(target: Path, flags: CopyFlags): IO[Unit] =
      Catscript.copy(path, target, flags)

    /**
     * Creates the specified directory with the permissions of "rwxrwxr-x" by
     * default. Fails if the parent path does not already exist.
     */
    def createDirectory: IO[Unit] = Catscript.createDirectory(path)

    /**
     * Creates the specified directory with the specified permissions. Fails if
     * the parent path does not already exist.
     */
    def createDirectory(permissions: Permissions): IO[Unit] =
      Catscript.createDirectory(path, permissions)

    /**
     * Creates the specified directory and any non-existent parent directories.
     */
    def createDirectories: IO[Unit] = Catscript.createDirectories(path)

    /**
     * Creates the specified directory and any parent directories, using the
     * supplied permissions for any directories that get created as a result of
     * this operation.
     */
    def createDirectories(permissions: Permissions): IO[Unit] =
      Catscript.createDirectories(path, permissions)

    /**
     * Creates the specified file with the permissions of "rw-rw-r--" by
     * default. Fails if the parent path does not already exist.
     */
    def createFile: IO[Unit] = Catscript.createFile(path)

    /**
     * Creates the specified file with the specified permissions. Fails if the
     * parent path does not already exist.
     */
    def createFile(permissions: Permissions): IO[Unit] =
      Catscript.createFile(path, permissions)

    /** Creates a hard link with an existing file. */
    def createLink(existing: Path): IO[Unit] =
      Catscript.createLink(path, existing)

    /** Creates a symbolic link which points to the supplied target. */
    def createSymbolicLink(target: Path): IO[Unit] =
      Catscript.createSymbolicLink(path, target)

    /**
     * Creates a symbolic link which points to the supplied target with optional
     * permissions.
     */
    def createSymbolicLink(target: Path, permissions: Permissions): IO[Unit] =
      Catscript.createSymbolicLink(path, target, permissions)

    // Deletion
    /**
     * Deletes the specified file or empty directory, failing if it does not
     * exist.
     */
    def delete: IO[Unit] = Catscript.delete(path)

    /**
     * Deletes the specified file or empty directory, passing if it does not
     * exist.
     */
    def deleteIfExists: IO[Boolean] = Catscript.deleteIfExists(path)

    /**
     * Deletes the specified file or directory. If the path is a directory and
     * is non-empty, its contents are recursively deleted. Symbolic links are
     * not followed (but are deleted).
     */
    def deleteRecursively: IO[Unit] = Catscript.deleteRecursively(path)

    /**
     * Deletes the specified file or directory. If the path is a directory and
     * is non-empty, its contents are recursively deleted. Symbolic links are
     * followed when `followLinks` is true.
     */
    def deleteRecursively(followLinks: Boolean): IO[Unit] =
      Catscript.deleteRecursively(path, followLinks)

    /**
     * Returns true if the specified path exists. Symbolic links are followed --
     * see the overload for more details on links.
     */
    def exists: IO[Boolean] = Catscript.exists(path)

    /**
     * Returns true if the specified path exists. Symbolic links are followed
     * when `followLinks` is true.
     */
    def exists(followLinks: Boolean): IO[Boolean] =
      Catscript.exists(path, followLinks)

    /**
     * Gets `BasicFileAttributes` for the supplied path. Symbolic links are not
     * followed.
     */
    def getBasicFileAttributes: IO[BasicFileAttributes] =
      Catscript.getBasicFileAttributes(path)

    /**
     * Gets `BasicFileAttributes` for the supplied path. Symbolic links are
     * followed when `followLinks` is true.
     */
    def getBasicFileAttributes(followLinks: Boolean): IO[BasicFileAttributes] =
      Catscript.getBasicFileAttributes(path, followLinks)

    /**
     * Gets the last modified time of the supplied path. The last modified time
     * is represented as a duration since the Unix epoch. Symbolic links are
     * followed.
     */
    def getLastModifiedTime: IO[FiniteDuration] =
      Catscript.getLastModifiedTime(path)

    /**
     * Gets the last modified time of the supplied path. The last modified time
     * is represented as a duration since the Unix epoch. Symbolic links are
     * followed when `followLinks` is true.
     */
    def getLastModifiedTime(followLinks: Boolean): IO[FiniteDuration] =
      Catscript.getLastModifiedTime(path, followLinks)

    /**
     * Gets the POSIX attributes for the supplied path. Symbolic links are not
     * followed.
     */
    def getPosixFileAttributes: IO[PosixFileAttributes] =
      Catscript.getPosixFileAttributes(path)

    /**
     * Gets the POSIX attributes for the supplied path. Symbolic links are
     * followed when `followLinks` is true.
     */
    def getPosixFileAttributes(followLinks: Boolean): IO[PosixFileAttributes] =
      Catscript.getPosixFileAttributes(path, followLinks)

    /**
     * Gets the POSIX permissions of the supplied path. Symbolic links are
     * followed.
     */
    def getPosixPermissions: IO[PosixPermissions] =
      Catscript.getPosixPermissions(path)

    /**
     * Gets the POSIX permissions of the supplied path. Symbolic links are
     * followed when `followLinks` is true.
     */
    def getPosixPermissions(followLinks: Boolean): IO[PosixPermissions] =
      Catscript.getPosixPermissions(path, followLinks)

    /**
     * Returns true if the supplied path exists and is a directory. Symbolic
     * links are followed.
     */
    def isDirectory: IO[Boolean] = Catscript.isDirectory(path)

    /**
     * Returns true if the supplied path exists and is a directory. Symbolic
     * links are followed when `followLinks` is true.
     */
    def isDirectory(followLinks: Boolean): IO[Boolean] =
      Catscript.isDirectory(path, followLinks)

    /** Returns true if the supplied path exists and is executable. */
    def isExecutable: IO[Boolean] = Catscript.isExecutable(path)

    /**
     * Returns true if the supplied path is a hidden file (note: may not check
     * for existence).
     */
    def isHidden: IO[Boolean] = Catscript.isHidden(path)

    /** Returns true if the supplied path exists and is readable. */
    def isReadable: IO[Boolean] = Catscript.isReadable(path)

    /**
     * Returns true if the supplied path is a regular file. Symbolic links are
     * followed.
     */
    def isRegularFile: IO[Boolean] = Catscript.isRegularFile(path)

    /**
     * Returns true if the supplied path is a regular file. Symbolic links are
     * followed when `followLinks` is true.
     */
    def isRegularFile(followLinks: Boolean): IO[Boolean] =
      Catscript.isRegularFile(path, followLinks)

    /** Returns true if the supplied path is a symbolic link. */
    def isSymbolicLink: IO[Boolean] = Catscript.isSymbolicLink(path)

    /** Returns true if the supplied path exists and is writable. */
    def isWritable: IO[Boolean] = Catscript.isWritable(path)

    /** Returns true if the supplied path reference the same file. */
    def isSameFile(path2: Path): IO[Boolean] = Catscript.isSameFile(path, path2)

    /** Gets the contents of the specified directory. */
    def list: IO[List[Path]] = Catscript.list(path)

    /**
     * Moves the source to the target, failing if source does not exist or the
     * target already exists. To replace the existing instead, use
     * `path.move(target, CopyFlags(CopyFlag.ReplaceExisting))`.
     */
    def move(target: Path): IO[Unit] = Catscript.move(path, target)

    /**
     * Moves the source to the target, following any directives supplied in the
     * flags. By default, an error occurs if the target already exists, though
     * this can be overridden via `CopyFlag.ReplaceExisting`.
     */
    def move(target: Path, flags: CopyFlags): IO[Unit] =
      Catscript.move(path, target, flags)

    // Real Path
    /** Returns the real path i.e. the actual location of `path`. */
    def realPath: IO[Path] = Catscript.realPath(path)

    /**
     * Sets the last modified, last access, and creation time fields of the
     * specified path. Times which are supplied as `None` are not modified.
     */
    def setFileTimes(
        lastModified: Option[FiniteDuration],
        lastAccess: Option[FiniteDuration],
        creationTime: Option[FiniteDuration],
        followLinks: Boolean
    ): IO[Unit] =
      Catscript.setFileTimes(
        path,
        lastModified,
        lastAccess,
        creationTime,
        followLinks
      )

    /**
     * Sets the POSIX permissions for the supplied path. Fails on non-POSIX file
     * systems.
     */
    def setPosixPermissions(permissions: PosixPermissions): IO[Unit] =
      Catscript.setPosixPermissions(path, permissions)

    /** Gets the size of the supplied path, failing if it does not exist. */
    def size: IO[Long] = Catscript.size(path)

  }

  // No path specific methods:

  /**
   * Creates a temporary file. The created file is not automatically deleted -
   * it is up to the operating system to decide when the file is deleted.
   * Alternatively, use `tempFile` to get a resource, which is deleted upon
   * resource finalization.
   */
  def createTempFile: IO[Path] = Catscript.createTempFile

  /**
   * Creates a temporary file. The created file is not automatically deleted -
   * it is up to the operating system to decide when the file is deleted.
   * Alternatively, use `tempFile` to get a resource which deletes upon resource
   * finalization.
   *
   * @param dir
   *   the directory which the temporary file will be created in. Pass none to
   *   use the default system temp directory
   * @param prefix
   *   the prefix string to be used in generating the file's name
   * @param suffix
   *   the suffix string to be used in generating the file's name
   * @param permissions
   *   permissions to set on the created file
   */
  def createTempFile(
      dir: Option[Path],
      prefix: String,
      suffix: String,
      permissions: Permissions
  ): IO[Path] =
    Catscript.createTempFile(dir, prefix, suffix, permissions)

  /**
   * Creates a temporary directory. The created directory is not automatically
   * deleted - it is up to the operating system to decide when the file is
   * deleted. Alternatively, use `tempDirectory` to get a resource which deletes
   * upon resource finalization.
   */
  def createTempDirectory: IO[Path] = Catscript.createTempDirectory

  /**
   * Creates a temporary directory. The created directory is not automatically
   * deleted - it is up to the operating system to decide when the file is
   * deleted. Alternatively, use `tempDirectory` to get a resource which deletes
   * upon resource finalization.
   *
   * @param dir
   *   the directory which the temporary directory will be created in. Pass none
   *   to use the default system temp directory
   * @param prefix
   *   the prefix string to be used in generating the directory's name
   * @param permissions
   *   permissions to set on the created directory
   */
  def createTempDirectory(
      dir: Option[Path],
      prefix: String,
      permissions: Permissions
  ): IO[Path] =
    Catscript.createTempDirectory(dir, prefix, permissions)

  /** User's current working directory */
  def currentWorkingDirectory: IO[Path] = Catscript.currentWorkingDirectory

  /** Returns the line separator for the specific OS */
  def lineSeparator: String = Catscript.lineSeparator

  /**
   * Creates a temporary file and deletes it at the end of the use of it.
   */
  def withTempFile[A](use: Path => IO[A]): IO[A] =
    Catscript.withTempFile(use)

  /**
   * Creates a temporary file and deletes it at the end of the use of it.
   *
   * @tparam A
   *   the type of the result computation
   *
   * @param dir
   *   the directory which the temporary file will be created in. Pass in None
   *   to use the default system temp directory
   * @param prefix
   *   the prefix string to be used in generating the file's name
   * @param suffix
   *   the suffix string to be used in generating the file's name
   * @param permissions
   *   permissions to set on the created file
   * @param use
   *   function describing the computation to be done with the temporary file
   * @return
   *   The result of the computation after using the temporary file
   */
  def withTempFile[A](
      dir: Option[Path],
      prefix: String,
      suffix: String,
      permissions: Permissions
  )(use: Path => IO[A]): IO[A] =
    Catscript.withTempFile(dir, prefix, suffix, permissions)(use)

  /**
   * Creates a temporary directory and deletes it at the end of the use of it.
   */
  def withTempDirectory[A](use: Path => IO[A]): IO[A] =
    Catscript.withTempDirectory(use)

  /**
   * Creates a temporary directory and deletes it at the end of the use of it.
   *
   * @tparam A
   *   the type of the result computation
   *
   * @param dir
   *   the directory which the temporary directory will be created in. Pass in
   *   None to use the default system temp directory
   * @param prefix
   *   the prefix string to be used in generating the directory's name
   * @param permissions
   *   permissions to set on the created file
   * @param use
   *   function describing the computation to be done with the temporary
   *   directory
   * @return
   *   the result of the computation after using the temporary directory
   */
  def withTempDirectory[A](
      dir: Option[Path],
      prefix: String,
      permissions: Permissions
  )(use: Path => IO[A]): IO[A] =
    Catscript.withTempDirectory(dir, prefix, permissions)(use)

  /** User's home directory */
  def userHome: IO[Path] = files.userHome

}
