package org.setup.syslog.plain

import org.setup.util.files._
import org.setup.util.syslog._
import org.setup.syslog.show

object Syslog {

  def messages(root: String): List[TimedSyslogMessage] =
    lines(root) flatMap TimedSyslogMessageExtractor.parse

  def main(args: Array[String]) {
     val ls = messages("/var/log")

     val procs = ls groupBy { case TimedSyslogMessage(_, SyslogMessage(_,proc,_,_)) => proc }

     val nprocs = procs.toList.map{ case (k,vs) => (vs.size,k) }.sortBy(-_._1)

     show(nprocs)
  }
}


