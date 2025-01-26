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

package org.typelevel.catscript.contacts.app

import cats.syntax.applicative.*
import cats.effect.{ExitCode, IO, IOApp}
import fs2.io.file.Path

import org.typelevel.catscript.contacts.cli.{Cli, Prompt}
import org.typelevel.catscript.contacts.core.ContactManager
import org.typelevel.catscript.contacts.domain.argument.*
import org.typelevel.catscript.syntax.path.*

object App extends IOApp {

  private val getOrCreateBookPath: IO[Path] = for {
    home <- userHome
    dir  = home / ".catscript"
    path = dir / "contacts.data"
    exists <- path.exists
    _      <- dir.createDirectories.unlessA(exists)
    _      <- path.createFile.unlessA(exists)
  } yield path

  def run(args: List[String]): IO[ExitCode] = getOrCreateBookPath
    .map(ContactManager(_))
    .flatMap { implicit cm =>
      Prompt.parsePrompt(args) match {
        case Help                    => Cli.helpCommand
        case AddContact              => Cli.addCommand
        case RemoveContact(username) => Cli.removeCommand(username)
        case SearchId(username)      => Cli.searchUsernameCommand(username)
        case SearchName(name)        => Cli.searchNameCommand(name)
        case SearchEmail(email)      => Cli.searchEmailCommand(email)
        case SearchNumber(number)    => Cli.searchNumberCommand(number)
        case ViewAll                 => Cli.viewAllCommand
        case UpdateContact(username, flags) =>
          Cli.updateCommand(username, flags)
      }
    }
    .as(ExitCode.Success)
}
