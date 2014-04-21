package org.setup

package object util {
    def obtain[T](c: => T): Option[T] = scala.util.Try(c).toOption
    def flatObtain[T](c: => Option[T]): Option[T] = scala.util.Try(c).toOption.flatten
}