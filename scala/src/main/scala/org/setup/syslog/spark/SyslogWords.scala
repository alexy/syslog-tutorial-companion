package spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.util.Try
import scala.collection.immutable.HashMap
import org.apache.spark.rdd.RDD

object SyslogWords {

  val reSystemLog = """(^[A-Za-z0-9, ]+\d{2}:\d{2}:\d{2}(?:\.\d{3})?)\s+(\S+)\s+([^\[]+)\[(\d+)\]\s*:?\s*(.*)""".r

  case class SyslogMessage(timestamp: String, host: Option[String], process: String, pid: Int, message: String)

  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Spark Syslog", "$YOUR_SPARK_HOME", List("target/scala-2.10/spark-tutorial-syslog_2.10-1.0.jar"))

    val lines = sc.textFile("/var/log/system.log")

    val events = lines.flatMap {
      case reSystemLog(timestamp, hostname, proc, pidS, msg) =>
        for {pid <- Try(pidS.toInt).toOption} yield SyslogMessage(timestamp, Some(hostname), proc, pid, msg)
      case _ => None
    }

    val procWords: RDD[(String, HashMap[String,Int])] = events.map { case SyslogMessage(timestamp, Some(hostname), proc, pid, msg) =>
      val words = msg.split("\\s+")
      val wordCounts = words.filterNot(_.isEmpty).foldLeft(HashMap[String,Int]()){ case (wc, w) =>
        if (w==null) wc else
        wc.updated(w, wc.getOrElse(w, 0) + 1)
      }
      (proc, wordCounts)
    }

    val procs = procWords.reduceByKey { case (m1,m2) =>

      m1.merged(m2) {
        case ((w, c1), (_, c2)) => (w, c1 + c2)
        // TODO why are we getting nulls at all?
        case (null, null) => ("", 0)
        case (null, x) => x
        case (x, null) => x
      }
    }.map { case (proc, wc) =>
       val totalWords = wc.values.sum
       val topWords = wc.toList.sortBy(-_._2).take(3)
      (totalWords, (proc, topWords))
    }.sortByKey(false)

    procs.take(10).zipWithIndex.foreach {
      case ((n, (proc, words)), i) =>
        print(s"$i: $proc ($n) =>")
        words.foreach { case (word, count) =>
          print(s" $word ($count)")
        }
        println()
    }
  }
}


