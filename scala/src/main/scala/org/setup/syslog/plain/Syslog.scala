package plain

import scala.util.Try

object Syslog {

  val reSystemLog = """(^[A-Za-z0-9, ]+\d{2}:\d{2}:\d{2}(?:\.\d{3})?)\s+(\S+)\s+([^\[]+)\[(\d+)\]\s*:?\s*(.*)""".r

  case class SyslogMessage(timestamp: String, host: Option[String], process: String, pid: Int, message: String)

  def main(args: Array[String]) {
    val lines = scala.io.Source.fromFile("/var/log/system.log").getLines().toSeq

    val events = lines.flatMap {
      case reSystemLog(timestamp,hostname, proc, pidS, msg) =>
        for {pid <- Try(pidS.toInt).toOption} yield SyslogMessage(timestamp,Some(hostname), proc, pid, msg)
      case _ => None
    }

    val procs = events.groupBy { case SyslogMessage(_, _,proc,_,_) => proc }

    val nprocs = procs.toList.map{ case (k,vs) => (vs.size,k) }.sortBy(-_._1)

    nprocs.take(10).zipWithIndex.foreach {
      case ((n, p), i) =>
        println(s"$i: $p ($n)")
    }
  }
}


