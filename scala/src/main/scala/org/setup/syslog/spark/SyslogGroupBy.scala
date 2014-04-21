package spark

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatterBuilder, DateTimeFormat}
import scala.util.Try

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object Syslog {

  val reSystemLog = """(^[A-Za-z0-9, ]+\d{2}:\d{2}:\d{2}(?:\.\d{3})?)\s+(\S+)\s+([^\[]+)\[(\d+)\]\s*:?\s*(.*)""".r

  case class SyslogMessage(timestamp: String, host: Option[String], process: String, pid: Int, message: String)

  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Spark Syslog", "YOUR_SPARK_HOME", List("target/scala-2.10/spark-tutorial-syslog_2.10-1.0.jar"))

    val lines = sc.textFile("/var/log/system.log")

    val events = lines.flatMap {
      case reSystemLog(timestamp,hostname, proc, pidS, msg) =>
        for {pid <- Try(pidS.toInt).toOption} yield SyslogMessage(timestamp,Some(hostname), proc, pid, msg)
      case _ => None
    }

    val procs = events.groupBy { case SyslogMessage(_, _,proc,_,_) => proc }

    val nprocs = procs.map{ case (k,vs) => (vs.size,k) }.sortByKey(false)

    nprocs.take(10).zipWithIndex.foreach {
      case ((n, p), i) =>
        println(s"$i: $p ($n)")
    }
  }
}


