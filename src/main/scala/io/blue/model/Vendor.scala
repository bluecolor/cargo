package io.blue.model

object Vendor {
  val SQLSERVER = "SQLSERVER"
  val ORACLE = "ORACLE"
  val POSTGRE = "POSTGRE"
  def getCountFunctionByVendor(vendor: Option[String] = None) = {
    vendor match {
      case Some(v) => getCountFunction(v)
      case None => "count"
    }
  }
  private def getCountFunction(vendor: String) = {
    vendor match {
      case SQLSERVER => "count_big"
      case _ => "count"
    }
  }
}