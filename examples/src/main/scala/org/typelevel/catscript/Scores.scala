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

import cats.syntax.all.*
import cats.effect.{IO, IOApp}

import fs2.io.file.Path

import syntax.path.*

object Scores extends IOApp.Simple {

  case class Score(name: String, score: Int) {
    def show: String = s"$name:$score"
  }

  def parseScore(strScore: String): Either[Throwable, Score] =
    Either.catchNonFatal(
      strScore.split(':') match {
        case Array(name, score) => Score(name, score.toInt)
        case _                  => Score("Cant parse this score", -1)
      }
    )

  val path = Path("src/main/resources/scores.txt")
  override def run: IO[Unit] =
    for {
      lines  <- path.readLines
      scores <- lines.traverse(parseScore(_).liftTo[IO])
      _      <- IO(scores.foreach(score => println(score.show)))
      _      <- path.append(Score("daniela", 100).show)
    } yield ()
}
