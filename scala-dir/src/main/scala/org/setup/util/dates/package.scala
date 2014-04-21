package org.setup.util

import org.setup.util.extractors.&&

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat,DateTimeFormatterBuilder}

package object dates {
    
    // NB submit to nscala-time

    val parsers = List(
        "MMM  d HH:mm:ss", "MMM dd HH:mm:ss", 
        "E MMM  d HH:mm:ss", "E MMM dd HH:mm:ss", 
        "E MMM  d HH:mm:ss.SSS", "E MMM dd HH:mm:ss.SSS")  map { case pat =>
      DateTimeFormat.forPattern(pat).getParser() } toArray
        
    val formatter = new DateTimeFormatterBuilder().append(null, parsers ).toFormatter()
                        .withDefaultYear(DateTime.now.getYear)

    def parseDateTime(s: String): Option[DateTime] = 
        try   { Some(formatter.parseDateTime(s)) } 
        catch { case _: IllegalArgumentException =>  None }

    object DateTimeExtractor {
      def unapply(s: String): Option[DateTime] = parseDateTime(s)
    }

    val reDateTime = """(^[A-Za-z0-9, ]+\d{2}:\d{2}:\d{2}(?:\.\d{3})?)(.*)""".r

    val parseDateTimeString: PartialFunction[String,(String,String)] = _ match { 
        case reDateTime(d,r) => (d,r)
    }

    val extractDateTime: PartialFunction[String,DateTime] = _ match {
        case reDateTime(d,_) && DateTimeExtractor(dt) => dt
    }
    
}