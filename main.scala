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

  def parseArgs(args: Array[String]): Either[String, List[File]] =
    args match
      case Array("-h")       => Left(helpMsg)
      case Array("-f")       => Left("File argument is missing!\n" + helpMsg)
      case Array("-f", file) => Right(List(new File(file)))
      case Array("-d") => Left("Directory argument is missing!\n" + helpMsg)
      case Array("-d", maybeDir) => readFilesIn(maybeDir)
      case _                     => Left(helpMsg)

  def helpMsg: String =
    s"""|Usage:
                     |  -d <directory>  : convert all files in directory
                     |  -f <csv file>   : convert single file 
                     |  -h              : print this message
                    |""".stripMargin

  def errorMsg(msg: String): String =
    s"$msg\n" + helpMsg

  def readFilesIn(maybeDir: String): Either[String, List[File]] =
    val dirFile = new File(maybeDir)
    if (!dirFile.isDirectory)
      Left(errorMsg(s"$maybeDir is not a directory!"))
    else
      dirFile.listFiles.filter(f => f.getName.endsWith(".csv")) match
        case Array() => Left(s"No csv files found in $maybeDir")
        case csvs    => Right(csvs.toList)
}
