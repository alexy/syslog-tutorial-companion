package org.setup

/**
 * Created by a on 4/14/14.
 */
package object syslog {
  def show(winners: Seq[(Int, String)], n: Int = 10): Unit = {
    winners.take(n).zipWithIndex.foreach { case ((n, p), i) =>
      println(s"$i: $p ($n)")
    }
  }
}
