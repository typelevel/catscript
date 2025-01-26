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

object flag {
  sealed abstract class Flag
  case class FirstNameFlag(firstName: String)     extends Flag
  case class LastNameFlag(lastName: String)       extends Flag
  case class PhoneNumberFlag(phoneNumber: String) extends Flag
  case class EmailFlag(email: String)             extends Flag
  case class UnknownFlag(flag: String)            extends Flag
}
