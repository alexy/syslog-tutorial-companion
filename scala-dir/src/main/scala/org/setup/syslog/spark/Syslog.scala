package org.setup.syslog.spark

/**
 * Created by Alexy Khrabrov on 4/14/14.
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.setup.util.syslog._
import org.setup.syslog.show


object Syslog {
  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Spark Syslog", "YOUR_SPARK_HOME", List("target/scala-2.10/spark-tutorial-syslog_2.10-1.0.jar"))

    val lines = sc.textFile("/var/log/system.log")

    val parsed = lines flatMap TimedSyslogMessageExtractor.parse

    val procs = parsed groupBy {
      case TimedSyslogMessage(_, SyslogMessage(_, proc, _, _)) => proc
    }

    val nprocs = procs map {
      case (k, vs) => (vs.size, k)
    } sortByKey (false)

    show(nprocs.take(10))
  }
}