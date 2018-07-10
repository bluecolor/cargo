package io.blue

import java.lang.invoke.MethodHandles
import org.slf4j.{Logger, LoggerFactory}
import java.sql._
import io.blue.model.{Table, Column}
import io.blue.util.ConnectionConf
import io.blue.connector._

import org.apache.commons.lang3.exception.ExceptionUtils

object MetaData {

  private val logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  def findColumns(table: Table, conf: ConnectionConf) = {
    val connection = Connector.connect(conf)
    var columns = List[Column]()
    val md = connection.getMetaData
    logger.debug(s"Table : catalog => ${table.catalog}, schema => ${table.schema}, name => ${table.name}")
    val rs = md.getColumns(table.catalog.getOrElse(null), table.schema.getOrElse(null), table.name, null)
    while(rs.next) {
      var column = new Column
      column.name = rs.getString("COLUMN_NAME")
      column.columnType = rs.getInt("DATA_TYPE")
      column.columnTypeName = rs.getString("TYPE_NAME")
      column.size = rs.getInt("COLUMN_SIZE")
      column.fraction = rs.getInt("DECIMAL_DIGITS")
      column.nullable = rs.getInt("NULLABLE")
      columns ::= column
    }
    connection.close
    columns.sortWith(_.name < _.name)
  }

  def count(table: Table, conf: ConnectionConf, filter: Option[String] = None) = {
    val query = table.countStatement(filter)
    logger.debug(query)
    val connection = Connector.connect(conf)
    val rs = connection.createStatement.executeQuery(query)
    var result: Long = 0
    while(rs.next) {
      result = rs.getLong(1)
    }
    result
  }

  def prepareTable(name: String, url: String, username: String, password: String, driverClassName: Option[String] = None): Table =
    prepareTable(name, ConnectionConf(url, username, password, driverClassName))

  def prepareTable(name: String, conf: ConnectionConf): Table = {
    var table = new Table(name)
    try {
      table.columns ++= findColumns(table, conf)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        table.columns ++= findColumnsWithSelect(table, conf)
    }
    table.columns.foreach { c =>
      logger.debug(s"""
        name: ${c.name}
        data_type: ${c.columnType}
        type_name: ${c.columnTypeName}""")}
    table.vendor = Some(findVendorByUrl(conf.url))
    table
  }

  def findVendorByUrl(url: String) = Connector.findVendorByUrl(url)

  def findColumnsWithSelect(table: Table, conf: ConnectionConf) = {
    val q = s"select * from $table.expression"
    val connection = Connector.connect(conf)
    val md = connection.createStatement.executeQuery(q).getMetaData

    val columns = (1 to md.getColumnCount).map{ i =>
      var column = new Column
      column.name = md.getColumnName(i)
      column.columnType = md.getColumnType(i)
      column.columnTypeName = md.getColumnTypeName(i)
      column.size = md.getPrecision(i)
      column.fraction = md.getScale(i)
      column
    }.toList.sortWith(_.name < _.name)
    connection.close
    columns
  }

  def create(table: Table, conf: ConnectionConf, options: Option[String]): Table =
    create(table, conf.url, conf.username, conf.password, conf.driverClassName, options)

  def create(table: Table, url: String, username: String, password: String, driverClassName: Option[String] = None, options: Option[String] = None): Table = {
    try {
      drop(table, url, username, password, driverClassName)
    } catch  { case e: Exception => logger.info(ExceptionUtils.getStackTrace(e)) }

    logger.info(s"Creating table ${table.expression} ...")
    val sql: String = table.createStatement(options)
    logger.debug(sql)
    val connection = Connector.connect(ConnectionConf(url, username, password, driverClassName))
    connection.createStatement.executeUpdate(sql)
    connection.close
    logger.info(s"Created table ${table.expression}")
    table
  }

  private def drop(table: Table, url: String, username: String, password: String, driverClassName: Option[String]) = {
    logger.info(s"Droping table ${table.expression} ...")
    val connection = Connector.connect(ConnectionConf(url, username, password, driverClassName))
    logger.debug(table.dropStatement)
    connection.createStatement.executeUpdate(table.dropStatement)
    connection.close
    logger.info(s"Dropped table ${table.expression}")
    table
  }

  def truncate(table: Table, conf: ConnectionConf): Table =
    truncate(table, conf.url, conf.username, conf.password, conf.driverClassName)

  def truncate(table: Table, url: String, username: String, password: String, driverClassName: Option[String]): Table = {
    logger.info(s"Truncating table ${table.expression} ...")
    val connection = Connector.connect(ConnectionConf(url, username, password, driverClassName))
    logger.debug(table.truncateStatement)
    connection.createStatement.executeUpdate(table.truncateStatement)
    connection.close
    logger.info(s"Truncated table ${table.expression}")
    table
  }

}