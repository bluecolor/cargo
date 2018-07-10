package io.blue

import java.lang.invoke.MethodHandles
import org.slf4j.{Logger, LoggerFactory}
import me.tongfei.progressbar._
import org.apache.commons.io.IOUtils
import java.sql._

import org.apache.commons.lang3.StringUtils

import io.blue.connector._
import io.blue.model._
import io.blue.util.ConnectionConf

object Application extends App {

  private val logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  private val options= io.blue.util.Options.init(args)
  if(options.help) {
    options.printHelp
    System.exit(0)
  }

  val sourceConConf = ConnectionConf(
    options.sourceUrl,
    options.sourceUsername,
    options.sourcePassword,
    options.sourceDriverClassName
  )
  val targetConConf = ConnectionConf(
    options.targetUrl,
    options.targetUsername,
    options.targetPassword,
    options.targetDriverClassName
  )


  val sourceTable = MetaData.prepareTable(options.sourceTable,sourceConConf)
  sourceTable.count = MetaData.count(sourceTable, sourceConConf, options.filter)


  val vendor = Some(MetaData.findVendorByUrl(options.targetUrl))

  var targetTable = options.targetTable match {
    case Some(name: String) => new Table(name, vendor)
    case None => new Table(options.sourceTable, vendor)
  }

  options.create match {
    case true =>
      targetTable.columns ++= sourceTable.columns // set the target's columns equal to source columns
      MetaData.create(targetTable, targetConConf, options.createOptions)
    case _ =>
  }

  options.truncate match {
    case true => MetaData.truncate(targetTable,targetConConf)
    case _ =>
  }

  if (targetTable.columns.isEmpty) {
    targetTable.columns ++= MetaData.findColumns(targetTable, targetConConf)
  }

  val selectQuery = sourceTable.selectStatement(options.filter)
  val insertQuery = targetTable.insertStatement

  logger.debug(s"Select query:\n$selectQuery")
  logger.debug(s"Insert query:\n$insertQuery")

  val sourceConnection = Connector.connect(sourceConConf)

  val targetConnection = Connector.connect(targetConConf)

  val rs = {
    val stmt = sourceConnection.createStatement
    stmt.setFetchSize(options.fetchSize)
    stmt.executeQuery(selectQuery)
  }
  val stmt = targetConnection.prepareStatement(insertQuery)

  var count: Long = 0
  var total: Long = 0

  var progress: Option[ProgressBar] = if(!options.quiet) {
    val bar = new ProgressBar("Cargo", sourceTable.count)
    bar.start
    Some(bar)
  } else None


  while(rs.next) {
    for(i <- 1 to sourceTable.columns.length) {
      sourceTable.columns(i-1).columnType match {
        case Types.DATE       => stmt.setDate(i, rs.getDate(i))
        case Types.TIMESTAMP  => stmt.setTimestamp(i, rs.getTimestamp(i))
        case Types.FLOAT      => stmt.setFloat(i, rs.getFloat(i))
        case Types.INTEGER | Types.BIT | Types.BIGINT | Types.SMALLINT |
             Types.TINYINT    => stmt.setInt(i, rs.getInt(i))
        case Types.DECIMAL    => stmt.setBigDecimal(i, rs.getBigDecimal(i))
        case Types.DOUBLE     => stmt.setLong(i, rs.getLong(i))
        case Types.NUMERIC    => stmt.setLong(i, rs.getLong(i))
        case Types.CHAR | Types.VARCHAR => stmt.setString(i, StringUtils.replaceChars(rs.getString(i), "–", "-"))
        case Types.BINARY     => {
          val is = rs.getBinaryStream(i)
          val len = IOUtils.toByteArray(is).length
          stmt.setBinaryStream(i, is, len)
        }
      }
    }
    count += 1
    stmt.addBatch
    if(count == options.insertBatchSize) {
      stmt.executeBatch
      if (progress.isDefined) progress.get.stepBy(count)
      total += count
      count = 0
    }
  }

  if(count > 0) {
    stmt.executeBatch
    total += count
    if (progress.isDefined) progress.get.stepBy(count)
  }
  if (progress.isDefined) progress.get.stop

  sourceConnection.close
  targetConnection.close
  logger.info(s"Inserted ${total} records")

}

