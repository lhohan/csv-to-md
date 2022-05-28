import java.io.File

object CsvToMarkdown {
  def main(args: Array[String]): Unit =
    parseArgs(args) match {
      case Left(msg) => println(msg)
      case Right(files) =>
        files.foreach { csv =>
          csvToMdFile(csv) match {
            case Left(error) => println(error)
            case Right(mdFile) =>
              println(
                s"${csv.getName} converted to markdown: ${mdFile.getName}"
              )
          }
        }
    }

  // arg parsing is succesfull if a least one file is passed
  def parseArgs(args: Array[String]): Either[String, List[File]] =
    parseArgs(args, readFilesIn)

  // arg parsing is succesfull if a least one file is passed
  def parseArgs(
      args: Array[String],
      readFilesIn: String => Either[String, List[File]]
  ): Either[String, List[File]] =
    args match
      case Array("-h")       => Left(helpMsg)
      case Array("-f")       => Left("File argument is missing!\n" + helpMsg)
      case Array("-f", file) => Right(List(new File(file)))
      case Array("-d") => Left("Directory argument is missing!\n" + helpMsg)
      case Array("-d", maybeDir) =>
        readFilesIn(maybeDir).flatMap { files =>
          if (files.isEmpty)
            Left(s"No csv files found in $maybeDir")
          else
            Right(files)
        }
      case _ => Left(helpMsg)

  val helpMsg: String =
    s"""|Usage:
                     |  -d <directory>  : convert all files in directory
                     |  -f <csv file>   : convert single file 
                     |  -h              : print this message
                    |""".stripMargin

  def errorMsg(msg: String): String =
    s"$msg\n" + helpMsg

  def readFilesIn(maybeDir: String): Either[String, List[File]] =
    val dirFile = new File(maybeDir)
    if (!dirFile.isDirectory) Left(errorMsg(s"$maybeDir is not a directory!"))
    else
      val csvFiles =
        dirFile.listFiles.filter(f => f.getName.endsWith(".csv")).toList
      Right(csvFiles)

}
