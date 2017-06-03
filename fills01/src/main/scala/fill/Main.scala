package fill

import java.lang.System

object Main extends App {

  def readFills(path: String): Unit = {

    val start1: Long = System.currentTimeMillis()

    val stream = getClass.getResourceAsStream(path)
    val lines = scala.io.Source.fromInputStream(stream).getLines

    def filterFields(x: Array[String]): Boolean = {
      // ignore CSV file header line
      if (x(0) == "PATIENT_ID") {
        false
      } else {
        x.size == 10
      }
    }
    def fieldsToFill(x: Array[String]): RawFill = {
      RawFill(
        x(0),
        x(1),
        RawFill.dateParse(x(2)),
        x(3),
        x(4),
        x(5),
        x(6),
        RawFill.dateParse(x(7)),
        x(8),
        x(9))
    }

    val fields = lines.map(x => x.split(",").map(y => y.trim)).filter(filterFields)

    val fills = fields.map(fieldsToFill)
    val foo = fills.take(447000)
    fills.take(944).foreach(println(_))
    println(s"fills.size = ${fills.size}")

    val end1: Long = System.currentTimeMillis()

    val elapsedSeconds: Float = (end1 - start1) / 1000.0f

    println(s"elapsedSeconds = $elapsedSeconds")

  }

  readFills("/fill_qa_3drug_classes.csv")

}

