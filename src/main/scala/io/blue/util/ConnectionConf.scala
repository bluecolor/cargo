package io.blue.util

case class ConnectionConf(
  var url: String,
  var username: String,
  var password: String,
  var driverClassName: Option[String] = None)