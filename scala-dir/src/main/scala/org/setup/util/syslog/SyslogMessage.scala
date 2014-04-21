package org.setup.util.syslog

import org.setup.util._

/**
 * Created by a on 4/13/14.
 */
case class SyslogMessage(hostname: Option[String], process: String, pid: Int, message: String)

object SyslogMessageExtractor {

  val reKernel   = """\s*\<kernel\>(.*)""".r
  val reHost     = """\s*(\S+)\s+([^\[]+)\[(\d+)\]\s*:?\s*(.*)""".r
  val reHostless = """\s*\<?([^\[]+)\[(\d+)\]\>?\s*:?\s*(.*)""".r

  def parse(s: String): Option[SyslogMessage] = s match {
    case reKernel(msg)                    => Some(SyslogMessage(None, "kernel", 0, msg))
    case reHost(hostname, proc, pidS, msg) =>
      for { pid <- obtain(pidS.toInt) } yield SyslogMessage(Some(hostname), proc, pid, msg)
    case reHostless(proc, pidS, msg)      =>
      for { pid <- obtain(pidS.toInt) } yield SyslogMessage(None, proc, pid, msg)
    case _                                => None
  }

  def unapply(s: String): Option[SyslogMessage] = parse(s)
}