package io.blue.util

import org.apache.commons.cli._

object CommandLineOptions {


  var cli: CommandLine = _
  val options = new org.apache.commons.cli.Options
  options.addOption("h", "help", false, "print help")
  options.addOption("cf", "config", true, "Configuration file")

  options.addOption("q", "quiet", false, "silent logs")
  options.addOption("s", "source-url", true, "source jdbc url")
  options.addOption("su", "source-username", true, "source connection username")
  options.addOption("sp", "source-password", true, "source connection password")
  options.addOption("st", "source-table", true, "source table")
  options.addOption("fs", "fetch-size", true, "select fetch size")

  options.addOption("t", "target-url", true, "target jdbc url")
  options.addOption("tu", "target-username", true, "target connection username")
  options.addOption("tp", "target-password", true, "target connection password")
  options.addOption("tt", "target-table", true, "target table")
  options.addOption("tx", "target-partition", true, "target partition to insert")
  options.addOption("bs", "batch-size", true, "insert batch size")
  options.addOption("c", "create", false, "create target table")
  options.addOption("d", "truncate", false, "truncate target table")
  options.addOption("co", "create-options", true, "create table options")
  options.addOption("f", "filter", true, "source filter")
  options.addOption("a", "async", false, "Asynchronous fetch & insert")
  options.addOption("to", "timeout", true, "Timeout in minutes (applicable in async mode)")

  def parseOptions(args: Array[String]) = {
    val parser = new DefaultParser
    cli = parser.parse(options, args)
    cli
  }

  def help {
    (new HelpFormatter).printHelp("cargo", options)
  }


}