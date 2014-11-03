package com.github.daiksy.typetalk4s.sample

import com.github.daiksy.typetalk4s.Typetalk4s
import scala.util.{Failure, Success}
import com.github.daiksy.typetalk4s.exception.TokenExpireException
import scala.annotation.tailrec

object PostSample {

  implicit val client = new Typetalk4s
  private var token = client.accessToken

  def main(args: Array[String]) {
    for (topicId <- client.topicIds) {
      post(topicId, "Matsuriだわっしょい (posted by Typetalk4s")
    }
  }

  @tailrec
  private def post(topicId: Long, message: String)(implicit client: Typetalk4s):Success[Int] = {
    client.post(token, topicId, message) match {
      case success@Success(_) => success
      case Failure(t: TokenExpireException) => {
        token = client.accessToken
        post(topicId, message)
      }
      case Failure(t) => throw t
    }
  }

}
