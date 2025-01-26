/*
 * Copyright (c) 2024 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
