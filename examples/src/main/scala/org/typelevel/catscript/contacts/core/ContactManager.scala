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

package org.typelevel.catscript.contacts.core

import cats.syntax.all.*
import cats.effect.IO
import fs2.io.file.Path
import org.typelevel.catscript.syntax.path.*
import org.typelevel.catscript.contacts.domain.contact.*

trait ContactManager {
  def addContact(contact: Contact): IO[Username]
  def removeContact(username: Username): IO[Unit]
  def searchUsername(username: Username): IO[Option[Contact]]
  def searchName(name: Name): IO[List[Contact]]
  def searchEmail(email: Email): IO[List[Contact]]
  def searchNumber(number: PhoneNumber): IO[List[Contact]]
  def getAll: IO[List[Contact]]
  def updateContact(username: String)(modify: Contact => Contact): IO[Contact]
}

object ContactManager {
  def apply(bookPath: Path): ContactManager = new ContactManager {

    private def parseContact(contact: String): IO[Contact] =
      contact.split('|') match {
        case Array(id, firstName, lastName, phoneNumber, email) =>
          Contact(id, firstName, lastName, phoneNumber, email).pure[IO]
        case _ =>
          new Exception(s"Invalid contact format: $contact")
            .raiseError[IO, Contact]
      }

    private def encodeContact(contact: Contact): String =
      s"${contact.username}|${contact.firstName}|${contact.lastName}|${contact.phoneNumber}|${contact.email}"

    private def saveContacts(contacts: List[Contact]): IO[Unit] =
      bookPath.writeLines(contacts.map(encodeContact))

    override def addContact(contact: Contact): IO[Username] = for {
      contacts <- getAll
      _ <- IO(contacts.contains(contact)).ifM(
        ContactFound(contact.username).raiseError[IO, Unit],
        saveContacts(contact :: contacts)
      )
    } yield contact.username

    override def removeContact(username: Username): IO[Unit] =
      for {
        contacts <- getAll
        filteredContacts = contacts.filterNot(_.username === username)
        _ <- saveContacts(filteredContacts)
      } yield ()

    override def searchUsername(username: Username): IO[Option[Contact]] =
      getAll.map(contacts => contacts.find(_.username === username))

    override def searchName(name: Name): IO[List[Contact]] =
      getAll.map(contacts =>
        contacts.filter(c => c.firstName === name || c.lastName === name)
      )

    override def searchEmail(email: Email): IO[List[Contact]] =
      getAll.map(contacts => contacts.filter(_.email === email))

    override def searchNumber(number: PhoneNumber): IO[List[Contact]] =
      getAll.map(contacts => contacts.filter(_.phoneNumber === number))

    override def getAll: IO[List[Contact]] = for {
      lines    <- bookPath.readLines
      contacts <- lines.traverse(parseContact)
    } yield contacts

    override def updateContact(
        username: Username
    )(modify: Contact => Contact): IO[Contact] = for {
      contacts <- getAll
      oldContact <- contacts.find(_.username === username) match {
        case None          => ContactNotFound(username).raiseError[IO, Contact]
        case Some(contact) => contact.pure[IO]
      }
      updatedContact  = modify(oldContact)
      updatedContacts = updatedContact :: contacts.filterNot(_ == oldContact)
      _ <- saveContacts(updatedContacts)
    } yield updatedContact
  }
}
