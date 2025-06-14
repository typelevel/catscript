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

package org.typelevel.catscript.contacts.cli

import cats.effect.IO
import cats.syntax.all.*

import org.typelevel.catscript.contacts.core.ContactManager
import org.typelevel.catscript.contacts.domain.flag.*
import org.typelevel.catscript.contacts.domain.contact.*

object Cli {

  def addCommand(implicit cm: ContactManager): IO[Unit] =
    for {
      username    <- IO.println("Enter the username: ") >> IO.readLine
      firstName   <- IO.println("Enter the first name: ") >> IO.readLine
      lastName    <- IO.println("Enter the last name: ") >> IO.readLine
      phoneNumber <- IO.println("Enter the phone number: ") >> IO.readLine
      email       <- IO.println("Enter the email: ") >> IO.readLine

      contact = Contact(username, firstName, lastName, phoneNumber, email)

      _ <- cm
        .addContact(contact)
        .flatMap(username => IO.println(s"Contact $username added"))
        .handleErrorWith {
          case ContactFound(username) =>
            IO.println(s"Contact $username already exists")
          case e =>
            IO.println(s"An error occurred: \n${e.printStackTrace()}")
        }
    } yield ()

  def removeCommand(username: Username)(implicit cm: ContactManager): IO[Unit] =
    cm.removeContact(username) >> IO.println(s"Contact $username removed")

  def searchUsernameCommand(
      username: Username
  )(implicit cm: ContactManager): IO[Unit] =
    cm.searchUsername(username).flatMap {
      case Some(c) => IO.println(c.show)
      case None    => IO.println(s"Contact $username not found")
    }

  def searchNameCommand(name: Name)(implicit cm: ContactManager): IO[Unit] =
    for {
      contacts <- cm.searchName(name)
      _        <- contacts.traverse_(c => IO.println(c.show))
    } yield ()

  def searchEmailCommand(email: Email)(implicit cm: ContactManager): IO[Unit] =
    for {
      contacts <- cm.searchEmail(email)
      _        <- contacts.traverse_(c => IO.println(c.show))
    } yield ()

  def searchNumberCommand(
      number: PhoneNumber
  )(implicit cm: ContactManager): IO[Unit] =
    for {
      contacts <- cm.searchNumber(number)
      _        <- contacts.traverse_(c => IO.println(c.show))
    } yield ()

  def viewAllCommand(implicit cm: ContactManager): IO[Unit] = for {
    contacts <- cm.getAll
    _        <- contacts.traverse_(c => IO.println(c.show))
  } yield ()

  def updateCommand(username: Username, options: List[Flag])(implicit
      cm: ContactManager
  ): IO[Unit] = cm
    .updateContact(username) { prev =>
      options.foldLeft(prev) { (acc, flag) =>
        flag match {
          case FirstNameFlag(name)     => acc.copy(firstName = name)
          case LastNameFlag(name)      => acc.copy(lastName = name)
          case PhoneNumberFlag(number) => acc.copy(phoneNumber = number)
          case EmailFlag(email)        => acc.copy(email = email)
          case UnknownFlag(_)          => acc
        }
      }
    }
    .flatMap(c => IO.println(s"Updated contact ${c.username}"))
    .handleErrorWith {
      case ContactNotFound(username) =>
        IO.println(s"Contact $username not found")
      case e =>
        IO.println(s"An error occurred: \n${e.printStackTrace()}")
    }

  def helpCommand: IO[Unit] = IO.println(
    s"""
        |Usage: contacts [command]
        |
        |Commands:
        |  add
        |  remove <username>
        |  search id <username>
        |  search name <name>
        |  search email <email>
        |  search number <number>
        |  list
        |  update <username> [flags]
        |  help
        |
        |Flags (for update command):
        |  --first-name <name>
        |  --last-name <name>
        |  --phone-number <number>
        |  --email <email>
        |
        |""".stripMargin
  )
}
