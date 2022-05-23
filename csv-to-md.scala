//> using lib "com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.3"
import java.io.File
import java.nio.file.Files
import scala.io.Source
import java.nio.file.Path
import java.nio.file.Paths
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser

def csvToMdFile(csvFile: File): Either[String, File] =
  if (!Files.exists(csvFile.toPath))
    Left(s"File does not exist: ${csvFile.toPath}")
  else
    for {
      mdFile <- deriveTargetFile(csvFile.toPath)
      csvLines <- readLines(csvFile)
      mdLines <- convertToMd(csvLines)
      _ <- writeLines(mdLines, mdFile.toFile)
    } yield mdFile.toFile

def deriveTargetFile(csvFile: Path): Either[String, Path] =
  val strPath = Right(csvFile.toAbsolutePath.toString)
  val mdString = strPath.flatMap { path =>
    path.split('.') match
      case Array()  => Left(s"file path empty: ${csvFile}")
      case Array(_) => Left(s"file extension missing")
      case x        => Right(x.init.mkString("", ".", ".md"))
  }
  mdString.map(Paths.get(_))

def readLines(file: File): Either[String, List[String]] =
  // TODO Encoding! Check RFC
  Right(Source.fromFile(file).getLines.toList)

def writeLines(lines: List[String], target: File): Either[String, Unit] =
  Right {
    writeToFile(target) { writer =>
      lines.foreach(writer.println)
    }
  }

def writeToFile(target: File)(f: java.io.PrintWriter => Unit) =
  val p = new java.io.PrintWriter(target)
  try { f(p) }
  finally { p.close() }

def convertToMd(csvLines: List[String]): Either[String, List[String]] =
  for {
    fields <- toFields(csvLines)
    md <- toMd(fields)
  } yield md

def toFields(csvLines: List[String]): Either[String, List[List[String]]] =
  import scala.jdk.CollectionConverters.*
  val csvFull = csvLines.mkString("\n")
  val mapper = new CsvMapper()
    .readerForListOf(classOf[String])
    .`with`(CsvParser.Feature.WRAP_AS_ARRAY)
  val all: java.util.List[java.util.List[String]] =
    mapper.readValues(csvFull).readAll
  val allScala = all.asScala.toList.map(_.asScala.toList)
  Right(allScala)

def toMd(fields: List[List[String]]): Either[String, List[String]] =
  val result = fields match
    case Nil => Nil
    case headers :: rest =>
      val numberOfFields = headers.size
      val headerSeperatorLine = List.fill(numberOfFields)("---")
      toMd(headers) :: toMd(headerSeperatorLine) :: rest.map(toMd(_))

  Right(result)

def toMd(line: List[String]): String =
  line.mkString("|", "|", "|")

enum AppError:
  case InputFileNotFound
