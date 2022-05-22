import $ivy.`org.scalatest::scalatest::3.2.9`

import org.scalatest._
import org.scalatest.matchers._
import java.io.File
import java.nio.file.Paths

// high level specification
class CsvToMdSpec extends featurespec.AnyFeatureSpec with should.Matchers {
  Feature("parse csv file to markdown file") {
    Scenario("a simple csv file") {
      val result = csvToMdFile(new File("test-data/csv-1.csv"))
      result.isRight shouldBe true
      val Right(mdFile) = result
      val mdContent = io.Source.fromFile(mdFile).getLines.toList.mkString("\n")
      mdContent shouldBe """|Field 1|Field 2|Field 3|
                            ~|---|---|---|
                            ~|a|b|3|
                            ~|c|d|4|""".stripMargin('~')

    }

    Scenario("file not found") {
      val result = csvToMdFile(new File("i-do-not-exist"))
      result.isLeft shouldBe true
      val Left(msg) = result
      assert(msg == "File does not exist: i-do-not-exist")
    }
  }
}

class CsvToMdTest
    extends funsuite.AnyFunSuite
    with EitherValues
    with should.Matchers {
  test("derive target file") {
    deriveTargetFile(
      Paths.get("abc.csv")
    ).right.value.getFileName.toString shouldBe "abc.md"

    deriveTargetFile(
      Paths.get("abc")
    ).left.value shouldBe "file extension missing"
  }

  test("csv line to fields") {
    toFields(List("h1,h2,h3", "f1,f2,f3", "f4,f5,f6")).right.value shouldBe
      List(
        List("h1", "h2", "h3"),
        List("f1", "f2", "f3"),
        List("f4", "f5", "f6")
      )
  }

  test("csv line to fields -- quoted fields") {
    toFields(List("h1,h2,h3", """"f1,with comma","f2,with comma","f3,with comma"""", "f4,f5,f6")).right.value shouldBe
      List(
        List("h1", "h2", "h3"),
        List("f1,with comma", "f2,with comma", "f3,with comma"),
      )
  }

  test("fields to md line") {
    toMd(
      List(
        List("h1", "h2", "h3"),
        List("f1", "f2", "f3"),
        List("f4", "f5", "f6")
      )
    ).right.value shouldBe
      List("|h1|h2|h3|", "|---|---|---|", "|f1|f2|f3|", "|f4|f5|f6|")

  }
}
