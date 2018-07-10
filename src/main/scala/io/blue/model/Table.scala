package io.blue.model

import java.lang.RuntimeException

class Table(var expression: String, var vendor: Option[String] = None) {

  private val tokens = expression.split("\\.")
  var catalog: Option[String] = None
  var schema: Option[String] = None
  var name: String = null
  var columns: List[Column] = List()
  var count: Long = 0

  catalog = tokens.length match {
    case 3 => Some(tokens(0)) // catalog.schema.name
    case _ => None // schema.name or name
  }

  schema = tokens.length match {
    case 3 => Some(tokens(1)) // catalog.schema.name
    case 2 => Some(tokens(0)) // schema.name
    case _ => None // name
  }

  name = tokens.last


  def processNameExpression = {
    def upCase = name.contains("\"") match {
      case true => (
        expression.toUpperCase
        ,if(catalog.isDefined) Some(catalog.get.toUpperCase) else None
        ,if(schema.isDefined) Some(schema.get.toUpperCase) else None
        ,name)
      case _ => (
        expression.toUpperCase
        ,if(catalog.isDefined) Some(catalog.get.toUpperCase) else None
        ,if(schema.isDefined) Some(schema.get.toUpperCase) else None
        ,name.toUpperCase)
    }
    vendor match {
      case Some(v) => v match {
        case Vendor.ORACLE => upCase
        case _ => (expression, catalog, schema, name) }
      case _ => (expression, catalog, schema, name)
    }
  }

  def countStatement(filter: Option[String] = None) = {
    val statement = s"select ${Vendor.getCountFunctionByVendor(vendor)}(1) from $expression"

    filter match {
      case Some(expression) => s"$statement where $expression"
      case None => statement
    }
  }
  def selectStatement(filter: Option[String] = None) =  {
    val statement = s"select ${columns.map(_.name).sorted.mkString(", ")} from $expression"
    filter match {
      case Some(expression) => s"$statement where $expression"
      case None => statement
    }
  }
  def insertStatement = {
    s"""
      |insert into ${expression} (
        ${columns.map(_.name).sorted
          .map{name => if(('a' to 'z') union ('A' to 'Z') contains name(0)) name else "\""+ name +"\""}
          .mkString(", ")}
      |) values (
      |  ${columns.map(_ => "?").mkString(", ")}
      |)""".stripMargin
  }
  def createStatement(options: Option[String]) = {
    val columns = this.columns.map{column => s"\t${column.script}"}.mkString(",\n").trim
    val statement = s"""
      | create table ${expression} (
      |  ${columns}
      | )
    """.stripMargin
    options match {
      case Some(expression) => s"${statement} ${expression}"
      case None => statement
    }
  }
  def truncateStatement = s"""truncate table ${expression}"""
  def dropStatement = s"""drop table ${expression}"""

}