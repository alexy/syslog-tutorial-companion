package org.setup.util

import java.nio.charset.{MalformedInputException, CodingErrorAction}
import scala.io.{Source, Codec}
import java.io.{FileInputStream, BufferedInputStream, PrintWriter, File}
import java.util.zip.GZIPInputStream

package object files {

  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  def gzipInputStream(s: String) = new GZIPInputStream(new BufferedInputStream(new FileInputStream(s)))

  def dir(f: File, acc: List[File] = Nil): List[File] =
      if (!f.canRead) acc
      else
        f match {
          case x if x.isFile =>  x :: acc
          case x if x.isDirectory => x.listFiles.foldRight(acc)(dir(_,_))
          case _ => acc
        }

  def dir(root: String): List[File] = dir(new File(root))

  def lines(root: String): List[String] =
      dir(root) flatMap {
          case f => try {
            val source = f.getName match {
              case gz if gz.endsWith(".gz") => Source.fromInputStream(gzipInputStream(f.getAbsolutePath))
              case _ => Source.fromFile(f)
            }
            source.getLines()
          } catch {
            case _: MalformedInputException =>
              println(s"skipping binary file $f")
              Nil
          }
      }

  def write(filename: String, line: String) =
      Some(new PrintWriter(filename)) foreach {
          p => p.write(line)
          p.close
      }

  def write(filename: String, lines: List[String]) =
      Some(new PrintWriter(filename)) foreach { p =>
          lines foreach p.println
          p.close
      }
}