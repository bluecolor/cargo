package io.blue.model

// TABLE_CAT String => table catalog (may be null)
// TABLE_SCHEM String => table schema (may be null)
// TABLE_NAME String => table name
// COLUMN_NAME String => column name
// DATA_TYPE int => SQL type from java.sql.Types
// TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
// COLUMN_SIZE int => column size.
// DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
// NULLABLE int => is NULL allowed.


class Column {
  var name: String = _
  var columnType: Int = _
  var columnTypeName: String = _
  var size: java.lang.Integer = _
  var fraction: java.lang.Integer = _
  var nullable: Int = 1
  def script = {
    s"""${if(('a' to 'z') union ('A' to 'Z') contains name(0)) name else "\""+name +"\"" } ${columnTypeName}""" +
    (if (size != null && size != 0) {
      s"(${size}" + (if(fraction != null && fraction > 0) s", ${fraction})" else ")")
    }  else "")
  }
}