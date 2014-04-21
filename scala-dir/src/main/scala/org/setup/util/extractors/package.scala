package org.setup.util

package object extractors {

  object && {
    def unapply[A](a: A) = Some((a, a))
  }

}