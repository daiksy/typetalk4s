package com.github.daiksy.typetalk4s

import com.typesafe.config.ConfigFactory
import skinny.http._
import skinny.util.JSONStringOps._
import com.github.daiksy.typetalk4s.exception.{ InvalidTokenException, TokenExpireException, AuthenticationException }
import scala.collection.JavaConverters._
import scala.util.control.Exception._
import java.util.Date
import Typetalk4s._
import scala.util.Try

class Typetalk4s {
  private lazy val config = ConfigFactory.load()

  protected lazy val clientId = config.getString("typetalk.credentials.clientId")
  protected lazy val clientSecret = config.getString("typetalk.credentials.clientSecret")
  lazy val scope: String = allCatch opt (config.getStringList("typetalk.scopes").asScala.mkString(",")) getOrElse "topic.read"

  lazy val typetalkApiUrl: String = "https://typetalk.in/api/v1/"

  def accessToken: AccessToken = {
    val text = HTTP.post("https://typetalk.in/oauth2/access_token",
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "grant_type" -> "client_credentials",
      "scope" -> scope).asString

    fromJSONString[AccessToken](text).getOrElse(throw new AuthenticationException)
  }

  def topicIds: List[Long] = config.getLongList("typetalk.topicIds").asScala.map(Long2long).toList

  def post(token: AccessToken, topicId: Long, message: String): Try[Int] = {
    val postMessageRequest = request(s"/topics/${topicId}", token).formParams("message" -> message)
    handleResponse(HTTP.post(postMessageRequest))(_.status)
  }

  def messages(token: AccessToken, topicId: Long, count: Option[Int] = None, from: Option[Long] = None, direction: String = "forward"): Try[Messages] = {
    val options: List[(String, Any)] = List(Some("direction" -> direction), count.map(c => "count" -> c), from.map(f => "from" -> f)).flatten

    val req = request(s"/topics/${topicId}", token).queryParams(options: _*)
    val response = HTTP.get(req)

    handleResponse(response) { res =>
      fromJSONString[Messages](res.asString).getOrElse(throw new RuntimeException(s"failed to parse result json."))
    }
  }

  protected def request(apiUrl: String, token: AccessToken): Request = {
    val safeApiUrl = apiUrl.replaceFirst("^\\/", "")
    Request(s"${typetalkApiUrl}${safeApiUrl}").header("Authorization", s"Bearer ${token.accessToken}")
  }

  protected def handleResponse[A](response: Response)(block: Response => A): Try[A] = Try {
    if (response.status >= 400) {
      response.header("WWW-Authenticate").map { msg =>
        msg match {
          case AuthenticateError(error, description) if error.contains("invalid_token") && description.contains("The access token is not found") => throw new InvalidTokenException
          case AuthenticateError(error, description) if error.contains("invalid_token") && description.contains("The access token expired") => throw new TokenExpireException
          case _ => throw new RuntimeException(s"Api request is failed because [${msg}]. response status: ${response.status}")
        }
      }.getOrElse(throw new RuntimeException(s"Api request is failed. response status: ${response.status}"))
    } else {
      block(response)
    }
  }
}
object Typetalk4s {

  case class AccessToken(accessToken: String, expiresIn: Long, scope: String, refreshToken: String, tokenType: String)

  case class Messages(posts: Seq[Post], hasNext: Boolean)

  case class Post(id: Long, account: Account, message: String, createdAt: Date, updatedAt: Date)

  case class Account(id: Long, name: String, fullName: String)

  object AuthenticateError {
    def unapply(header: String): Option[(String, String)] = {
      val headerFields = header.split(",")
      for (
        error <- headerFields find (_ contains "Bearer error=");
        description <- headerFields find (_ contains "error_description")
      ) yield (error -> description)
    }
  }

}
