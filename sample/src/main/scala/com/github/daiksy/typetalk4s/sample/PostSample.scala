package com.github.daiksy.typetalk4s.sample

import com.github.daiksy.typetalk4s.Typetalk4s

object PostSample {
  def main(args: Array[String]) {

    val client = new Typetalk4s

    for (topicId <- client.topicIds) {
      client.post(topicId, "Matsuriだわっしょい(posted by Typetalk4s")
    }
  }

}
