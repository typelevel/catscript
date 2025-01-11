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

import cats.syntax.applicative.*
import cats.effect.{IO, IOApp}

import scodec.codecs.*
import scodec.Codec
import fs2.io.file.Path

import syntax.path.*

object Place extends IOApp.Simple {

  case class Place(number: Int, name: String)

  implicit val placeCodec: Codec[Place] = (int32 :: utf8).as[Place]

  val path = Path("src/main/resources/place.data")

  def run: IO[Unit] =
    for {
      exists <- path.exists
      // Equivalent of doing `if (exists) IO.unit else path.createFile`
      _ <- path.createFile.whenA(exists)
      _ <- path.writeAs[Place](Place(1, "Michael Phelps"))
      _ <- path.readAs[Place].flatMap(IO.println)
    } yield ()

}
