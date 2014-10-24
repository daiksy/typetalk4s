package com.github.daiksy.typetalk4s

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._
import skinny.http._
import skinny.util.JSONStringOps._
import com.github.daiksy.typetalk4s.exception.AuthenticationException

class Typetalk4s {
  private lazy val config = ConfigFactory.load()

  protected lazy val clientId = config.getString("typetalk.credentials.clientId")
  protected lazy val clientSecret = config.getString("typetalk.credentials.clientSecret")

  case class AccessToken(accessToken: String, expiresIn: Long, scope: String, refreshToken: String, tokenType: String)

  protected def accessToken: AccessToken = {
    val text = HTTP.post("https://typetalk.in/oauth2/access_token",
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "grant_type" -> "client_credentials",
      "scope" -> "topic.post,topic.read").asString

    fromJSONString[AccessToken](text).getOrElse(throw new AuthenticationException)
  }

  def topicIds: List[Long] = config.getLongList("typetalk.topicIds").asScala.map(Long2long).toList

  def post(topicId: Long, message: String): Unit = {
    val token = accessToken
    val request = Request(s"https://typetalk.in/api/v1/topics/${topicId}")
      .formParams("message" -> message)
      .header("Authorization", s"Bearer ${token.accessToken}")
    val response = HTTP.post(request)
  }
}
