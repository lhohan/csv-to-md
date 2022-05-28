import $ivy.`org.scalatest::scalatest::3.2.9`

import org.scalatest._
import org.scalatest.matchers._
import java.io.File
import org.scalatest.exceptions.TestFailedException

import CsvToMarkdown._

class CliSpec extends featurespec.AnyFeatureSpec with should.Matchers {

  Feature("Argument parsing") {
    Scenario("No args") {
      val result = parseArgs(Array.empty[String], unexpectedCall)

      result match {
        case Left("unexpected call") =>
          fail("unexpect call to file finder function")
        case Left(expectedHelpMsg) => succeed
        case _                     => fail()
      }
    }

    Scenario("Single file passed") {
      val result = parseArgs(Array("-f", "abc.csv"), unexpectedCall)

      result match {
        case Left("unexpected call") =>
          fail("unexpect call to file finder function")
        case Right(files: List[File]) =>
          files should not be empty
          val List(found) = files
          found.getName shouldBe "abc.csv"
        case _ => fail()
      }
    }

    Scenario("Directory contained csv files passed") {
      val result = parseArgs(
        Array("-d", "abcDir"),
        String => Right(List(new File("abc.csv")))
      )

      result match {
        case Left("unexpected call") =>
          fail("unexpect call to file finder function")
        case Right(files: List[File]) =>
          files should not be empty
          val List(found) = files
          found.getName shouldBe "abc.csv"
        case _ => fail()
      }
    }

    Scenario("No files found in directory") {
      val result =
        parseArgs(Array("-d", "abcDir"), String => Right(List.empty[File]))

      result match {
        case Left("unexpected call") =>
          fail("unexpect call to file finder function")
        case Left(msg) if msg.startsWith("No csv files found in") =>
          succeed
        case _ => fail()
      }
    }
  }

  def unexpectedCall: String => Either[String, List[File]] = String =>
    Left("unexpected call")

  lazy val expectedHelpMsg: String =
    s"""|Usage:
                     |  -d <directory>  : convert all files in directory
                     |  -f <csv file>   : convert single file 
                     |  -h              : print this message
                    |""".stripMargin

}
