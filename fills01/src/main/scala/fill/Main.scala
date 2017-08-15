package fill

import java.lang.System
// This is version 1 just reading raw fills.
// Version 2 added fields GPI, drugIsGeneric, rxNumber
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
        x.size == 13
      }
    }
    def fieldsToFill(aa: Array[String]): RawFill = {
      def x(index: Int) : String = {
        aa(index).trim
      }
      RawFill(
        x(0),
        x(1),
        RawFill.dateParse(x(2)),
        x(3),
        x(4),
        x(5),
        x(6),
        x(7),
        x(8),
        x(9),
        RawFill.dateParse(x(10)),
        x(11),
        x(12))
    }

    val fields = lines.map(x => x.split(",").map(y => y.trim)).filter(filterFields)

    val fills = fields.map(fieldsToFill)
    def noop (x: Any):Unit = {}
//    fills.take(6000).foreach( noop(_))
    fills.take(100).foreach(println(_))
    println(s"fills.size = ${fills.size}")

    val end1: Long = System.currentTimeMillis()

    val elapsedSeconds: Float = (end1 - start1) / 1000.0f

    println(s"elapsedSeconds = $elapsedSeconds")

  }

  readFills("/QA_TEST_FILLS_AUG15.csv")

}

