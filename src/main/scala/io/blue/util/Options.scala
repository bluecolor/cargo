package io.blue.util

import java.io.File
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.apache.commons.cli._

object Options {
  def init(args: Array[String]) = {
    val options = new Options
    val cli = CommandLineOptions.parseOptions(args)
    if(cli.hasOption("config")) {
      loadConfig(options, cli.getOptionValue("config"))
    }
    loadCliOptions(options, cli)
  }

  private def loadConfig(o: io.blue.util.Options, file: String) = {
    val configs = new Configurations
    val config = configs.properties(new File(file))
    o.config = Some(file)
    o.quiet = config.getBoolean("QUIET", false)
    o.sourceUrl = config.getString("SOURCE_URL", "")
    o.sourceDriverClassName = if (config.getString("SOURCE_DRIVER_CLASSNAME", "") == "")
      None
    else
      Some(config.getString("SOURCE_DRIVER_CLASSNAME"))

    o.sourceUsername = config.getString("SOURCE_USERNAME", "")
    o.sourcePassword = config.getString("SOURCE_PASSWORD", "")
    o.sourceTable = config.getString("SOURCE_TABLE", "")
    o.fetchSize = config.getInteger("FETCH_SIZE", 1000)
    o.targetUrl = config.getString("TARGET_URL", "")

    o.targetDriverClassName = if (config.getString("TARGET_DRIVER_CLASSNAME", "") == "")
      None
    else
      Some(config.getString("TARGET_DRIVER_CLASSNAME"))


    o.targetUsername = config.getString("TARGET_USERNAME", "")
    o.targetPassword = config.getString("TARGET_PASSWORD", "")

    o.targetTable = if (config.getString("TARGET_TABLE", "") == "")
      None
    else
      Some(config.getString("TARGET_TABLE"))

    o.insertBatchSize = config.getInteger("INSERT_BATCH_SIZE", 1000)
    o.create = config.getBoolean("CREATE", false)
    o.truncate = config.getBoolean("TRUNCATE", false)

    o.createOptions = if(config.getString("CREATE_OPTIONS", "") == "")
      None
    else
      Some(config.getString("CREATE_OPTIONS"))

    o.filter = if(config.getString("FILTER", "") == "")
      None
    else
      Some(config.getString("FILTER"))
    o
  }
  private def loadCliOptions(o: io.blue.util.Options, cli: CommandLine) = {
    o.config = if(cli.hasOption("config")) Some(cli.getOptionValue("config")) else None
    o.help = cli.hasOption("help")
    o.quiet = if (cli.hasOption("quiet")) true else o.quiet
    o.sourceUrl = if(cli.hasOption("source-url")) cli.getOptionValue("source-url") else o.sourceUrl
    o.sourceUsername = if(cli.hasOption("source-username")) cli.getOptionValue("source-username") else o.sourceUsername
    o.sourcePassword = if(cli.hasOption("source-password")) cli.getOptionValue("source-password") else o.sourcePassword
    o.sourceTable = if(cli.hasOption("source-table")) cli.getOptionValue("source-table") else o.sourceTable
    o.fetchSize = if(cli.hasOption("fetch-size")) Integer.parseInt(cli.getOptionValue("fetch-size")) else o.fetchSize
    o.targetUrl = if(cli.hasOption("target-url")) cli.getOptionValue("target-url") else o.targetUrl
    o.targetUsername = if(cli.hasOption("target-username")) cli.getOptionValue("target-username") else o.targetUsername
    o.targetPassword = if(cli.hasOption("target-password")) cli.getOptionValue("target-password") else o.targetPassword
    o.targetTable = if(cli.hasOption("target-table")) Some(cli.getOptionValue("target-table")) else o.targetTable
    o.insertBatchSize = if(cli.hasOption("batch-size")) Integer.parseInt(cli.getOptionValue("batch-size")) else o.insertBatchSize
    o.create = if(cli.hasOption("create")) true else o.create
    o.truncate = if(cli.hasOption("truncate")) true else o.truncate
    o.createOptions = if(cli.hasOption("create-options")) Some(cli.getOptionValue("create-options")) else o.createOptions
    o.filter = if(cli.hasOption("filter")) Some(cli.getOptionValue("filter")) else o.filter
    o
  }
}

class Options {
  var config: scala.Option[String] = _
  var help: Boolean = false
  var quiet: Boolean = false
  var sourceUrl: String = _
  var sourceDriverClassName: scala.Option[String] = None
  var sourceUsername: String = _
  var sourcePassword: String = _
  var sourceTable: String = _
  var fetchSize: Int = 1000
  var targetUrl: String = _
  var targetDriverClassName: scala.Option[String] = None
  var targetUsername: String = _
  var targetPassword: String = _
  var targetTable: scala.Option[String] = None
  var insertBatchSize: Int = 1000
  var create: Boolean = false
  var truncate: Boolean = false
  var createOptions: scala.Option[String] = None
  var filter: scala.Option[String] = None

  def printHelp {
    CommandLineOptions.help
  }
}