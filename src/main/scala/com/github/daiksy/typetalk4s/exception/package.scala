package com.github.daiksy.typetalk4s

package object exception {
  class AuthenticationException extends RuntimeException("fail to auth.")
  class TokenExpireException extends RuntimeException(s"AccessToken Expired")
  class InvalidTokenException extends RuntimeException(s"AccessToken is Invalid")
}
