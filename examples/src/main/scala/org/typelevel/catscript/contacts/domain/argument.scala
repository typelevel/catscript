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

package org.typelevel.catscript.contacts.domain

import org.typelevel.catscript.contacts.domain.contact.*
import org.typelevel.catscript.contacts.domain.flag.Flag

object argument {
  sealed abstract class CliCommand

  case object AddContact extends CliCommand

  case class RemoveContact(username: Username) extends CliCommand

  case class SearchId(username: Username) extends CliCommand

  case class SearchName(name: Name) extends CliCommand

  case class SearchEmail(email: Email) extends CliCommand

  case class SearchNumber(number: PhoneNumber) extends CliCommand

  case class UpdateContact(
      username: Username,
      options: List[Flag]
  ) extends CliCommand

  case object ViewAll extends CliCommand

  case object Help extends CliCommand
}
