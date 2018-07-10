package io.blue.connector

import java.lang.invoke.MethodHandles
import org.slf4j.{Logger, LoggerFactory}
import java.sql._
import io.blue.model.{Vendor, Table}
import io.blue.util.ConnectionConf

object Connector {

  private val logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)


  def connect(conf: ConnectionConf): java.sql.Connection =
    connect(conf.url, conf.username, conf.password, conf.driverClassName)

  def connect(url: String, username: String, password: String, driverClassName: Option[String] = None): java.sql.Connection = {
    var className = driverClassName match {
      case Some(name) => name
      case None => findClassNameByUrl(url)
    }
    Class.forName(className)
    DriverManager.setLoginTimeout(10) //seconds
    DriverManager.getConnection(url, username, password)
  }

  private def findClassNameByUrl(url: String) = {
    findVendorByUrl(url) match {
      case Vendor.ORACLE => "oracle.jdbc.driver.OracleDriver"
      case Vendor.SQLSERVER => "net.sourceforge.jtds.jdbc.Driver"
      case Vendor.POSTGRE => "org.postgresql.Driver"
      case _ => "oracle.jdbc.driver.OracleDriver"
    }
  }
  def findVendorByUrl(url: String) = {
    if (url.contains("oracle")) Vendor.ORACLE
    else if (url.contains("sqlserver") ) Vendor.SQLSERVER
    else if (url.contains("postgresql")) Vendor.POSTGRE
    else Vendor.ORACLE
  }
}