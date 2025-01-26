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

import org.typelevel.catscript.contacts.domain.argument.*
import org.typelevel.catscript.contacts.domain.flag.*

import scala.annotation.tailrec

object Prompt {
  def parsePrompt(args: List[String]): CliCommand = args match {
    case "add" :: Nil                          => AddContact
    case "remove" :: username :: Nil           => RemoveContact(username)
    case "search" :: "id" :: username :: Nil   => SearchId(username)
    case "search" :: "name" :: name :: Nil     => SearchName(name)
    case "search" :: "email" :: email :: Nil   => SearchEmail(email)
    case "search" :: "number" :: number :: Nil => SearchNumber(number)
    case "list" :: _                           => ViewAll
    case "update" :: username :: options =>
      UpdateContact(username, parseUpdateFlags(options))
    case Nil => Help
    case _   => Help
  }

  private def parseUpdateFlags(options: List[String]): List[Flag] = {

    @tailrec
    def tailParse(remaining: List[String], acc: List[Flag]): List[Flag] =
      remaining match {
        case Nil => acc
        case "--first-name" :: firstName :: tail =>
          tailParse(tail, FirstNameFlag(firstName) :: acc)
        case "--last-name" :: lastName :: tail =>
          tailParse(tail, LastNameFlag(lastName) :: acc)
        case "--phone-number" :: phoneNumber :: tail =>
          tailParse(tail, PhoneNumberFlag(phoneNumber) :: acc)
        case "--email" :: email :: tail =>
          tailParse(tail, EmailFlag(email) :: acc)
        case flag :: _ => List(UnknownFlag(flag))
      }

    tailParse(options, Nil)
  }
}
