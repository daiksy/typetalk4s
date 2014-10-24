package com.github.daiksy.typetalk4s.sample

import com.github.daiksy.typetalk4s.Typetalk4s
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object PostSample {
  def main(args: Array[String]) {
    val config = ConfigFactory.load()

    lazy val topicIds = config.getLongList("typetalk.topicIds").asScala.toList

    val client = new Typetalk4s

    val token = client.getAccessToken()

    for (topicId <- topicIds) {
      client.post(token, topicId, "Matsuriだわっしょい(posted by Typetalk4s")
    }
  }

}
