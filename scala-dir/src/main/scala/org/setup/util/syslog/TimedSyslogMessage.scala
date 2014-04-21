package org.setup.util.syslog

import org.joda.time.DateTime

import org.setup.util.dates.{reDateTime, DateTimeExtractor}

//import PartialFunction.{condOpt => Match}

/**
 * Created by Alexy Khrabrov on 4/13/14.
 */

case class TimedSyslogMessage(timestamp: DateTime, body: SyslogMessage)

object TimedSyslogMessageExtractor {
  def parseDebug(s: String): Option[TimedSyslogMessage] = s match {
    case reDateTime(d, r) =>
      d match {
        case DateTimeExtractor(dt) => r match {
          case SyslogMessageExtractor(msg) => Some(TimedSyslogMessage(dt, msg))
          case _ => /* log it*/ None
        }
        case _ => None
      }
    case _ => None
  }

  def parse(s: String): Option[TimedSyslogMessage] =
      try {
        for {
          dr <- reDateTime.unapplySeq(s)
          d :: r :: _ = dr
          DateTimeExtractor(dt) = d
          SyslogMessageExtractor(msg) = r
        } yield TimedSyslogMessage(dt, msg)
      } catch {
        case _: MatchError => None
      }
  def unapply(s: String): Option[TimedSyslogMessage] = parse(s)
}
