package tp

import java.lang.System
import java.io.PrintWriter
import java.io.File

// This is version 1 just reading raw fills.
// Version 2 added fields GPI, drugIsGeneric, rxNumber
object MainTarg extends App {
  val CSV_HEADER: String = "Facility Id,Drug Class,Patient Name,Rx Number,PDC 12-Mo,PDC 6-Mo,Plan Bin,Plan Code,Plan Group,Last Sold Date,Last Days Supply,Patient Phone,Birth Date,Days Since Review,Note_1,Note_2,Note_3"

  def readCSV(path: String): List[(String, RawPat)] = {

    val start1: Long = System.currentTimeMillis()

    val stream = getClass.getResourceAsStream(path)
    println(s"readCSV path = $path")

    val lines = scala.io.Source.fromInputStream(stream).getLines
    def filterFields(x: Array[String]): Boolean = {
      // ignore CSV file header line
      if (x(0) == "Facility Id") {
        false
      } else {
        x.size >= 15
      }
    }
    def fieldsToRawPat(aa: Array[String]): RawPat = {
      def x(index: Int): String = {
        if (aa.size > index) {
          aa(index).trim
        } else {
          ""
        }
      }
      // println(x(2)); // print name only for debugging.
      RawPat(
        x(0),
        x(1),
        x(2),
        RawPat.parseRx(x(3)),
        x(4),
        x(5),
        x(6),
        x(7),
        x(8),
        RawPat.dateParse(x(9)),
        x(10),
        x(11),
        RawPat.dateParse(x(12)),
        x(13),
        x(14),
        x(15),
        x(16))
    }

    // val fields = lines.map(x => x.split(",").map(y => y.trim)).filter(filterFields)
    def fieldsWithLine(line: String): (String, Array[String]) = {
      val fields = line.split(",").map(y => y.trim)
      (line, fields)
    }
    def filterFieldsWithLines(pair: (String, Array[String])): Boolean = {
      filterFields(pair._2)
    }
    val fieldsWithLines = lines.map(fieldsWithLine).filter(filterFieldsWithLines);

    def toPatWithLine(pair: (String, Array[String])): (String, RawPat) = {
      (pair._1, fieldsToRawPat(pair._2))
    }

    // val pats: Iterator[RawPat] = fields.map(fieldsToFill)
    val pats: Iterator[(String, RawPat)] = fieldsWithLines.map(toPatWithLine)

    val end1: Long = System.currentTimeMillis()

    val elapsedSeconds: Float = (end1 - start1) / 1000.0f
    val ret = pats.toList;
    println(s"Input File $path read ${ret.size} rows in elapsedSeconds = $elapsedSeconds")
    ret;
  }

  def pairsMatchingReferencePair(ref: (String, RawPat), pairs: List[(String, RawPat)]): List[(String, RawPat)] = {
    val matchPairs = for (pair <- pairs if (ref._2.matchPerson(pair._2))) yield { pair }
    matchPairs
  }
  def compareCSV(file1: String, file2: String): Unit = {
    val afterPats = readCSV("/" + file2 + ".csv");
    val beforePats = readCSV("/" + file1 + ".csv");

    def beforePatsDropped(): List[(String, RawPat)] = {
      val notInAfterList = for (pair <- beforePats if (pairsMatchingReferencePair(pair, afterPats).isEmpty)) yield { pair }
      notInAfterList
    }

    def afterPatsAdded(): List[(String, RawPat)] = {
      val notInList = for (pair <- afterPats if (pairsMatchingReferencePair(pair, beforePats).isEmpty)) yield { pair }
      notInList
    }
    
    def changePatientDetails(): List[(String, String)] = {
      val rowsBeforeAfter: List[List[((String, RawPat), (String, RawPat))]] = for (pair <- beforePats) yield { 
        pairsMatchingReferencePair(pair, afterPats).map( pair2 => (pair, pair2 ) ).toList
      }

      val listOfPairs: List[(String, String)] = 
        for( l1 <- rowsBeforeAfter; l2 <- l1 if(!(l2._1._2.matchDetail(l2._2._2) ) ) ) yield { 
          (l2._1._1, l2._2._1) 
          }
      listOfPairs
    }
    def changePatientRx(): List[(String, String)] = {
      val rowsBeforeAfter: List[List[((String, RawPat), (String, RawPat))]] = for (pair <- beforePats) yield { 
        pairsMatchingReferencePair(pair, afterPats).map( pair2 => (pair, pair2 ) ).toList
      }

      val listOfPairs: List[(String, String)] = 
        for( l1 <- rowsBeforeAfter; l2 <- l1 if(!(l2._1._2.matchRx(l2._2._2) ) ) ) yield { 
          (l2._1._1, l2._2._1) 
          }
      listOfPairs
    }
    def changePatientPlan(): List[(String, String)] = {
      val rowsBeforeAfter: List[List[((String, RawPat), (String, RawPat))]] = for (pair <- beforePats) yield { 
        pairsMatchingReferencePair(pair, afterPats).map( pair2 => (pair, pair2 ) ).toList
      }

      val listOfPairs: List[(String, String)] = 
        for( l1 <- rowsBeforeAfter; l2 <- l1 if(!(l2._1._2.matchPlan(l2._2._2) ) ) ) yield { 
          (l2._1._1, l2._2._1) 
          }
      listOfPairs
    }
    def changePatientPDC(): List[(String, String)] = {
      val rowsBeforeAfter: List[List[((String, RawPat), (String, RawPat))]] = for (pair <- beforePats) yield { 
        pairsMatchingReferencePair(pair, afterPats).map( pair2 => (pair, pair2 ) ).toList
      }

      val listOfPairs: List[(String, String)] = 
        for( l1 <- rowsBeforeAfter; l2 <- l1 if(!(l2._1._2.matchPDC(l2._2._2) ) ) ) yield { 
          (l2._1._1, l2._2._1) 
          }
      listOfPairs
    }

    val beforeDropped = beforePatsDropped()
    println(s"beforeDropped.size = ${beforeDropped.size} ");

    val afterAdded = afterPatsAdded()
    println(s"afterAdded.size = ${afterAdded.size} ");
    
    val changedDetails = changePatientDetails();
    println(s"changedDetails.size = ${changedDetails.size} ")

    val changedRx = changePatientRx();
    println(s"changedRx.size = ${changedRx.size} ")

    val changedPlan = changePatientPlan();
    println(s"changedPlan.size = ${changedPlan.size} ")

    val changedPDC = changePatientPDC();
    println(s"changedPDC.size = ${changedPDC.size} ")

    def writePatientCSVFile(filepath: String, patients: List[(String, RawPat)]): Unit = {
      val pw = new PrintWriter(new File(filepath))
      pw.println(CSV_HEADER)
      patients.foreach {
        pat => pw.println(pat._1)
      }
      pw.close()
    }
    def writeDetailsCSVFile(filepath: String, patients: List[(String, String)]): Unit = {
      val pw = new PrintWriter(new File(filepath))
      pw.println(CSV_HEADER)
      patients.foreach {
        pat => {
          pw.println(pat._1)
          pw.println(pat._2)
        }
      }
      pw.close()
    }

    writePatientCSVFile("output/" + file2 + "_ADDED.csv", afterAdded);

    writePatientCSVFile("output/" + file2 + "_DROPPED.csv", beforeDropped);
    
    writeDetailsCSVFile("output2/" + file2 + "_CHANGED.csv", changedDetails);

    writeDetailsCSVFile("output2/" + file2 + "_CHANGED_RX.csv", changedRx);

    writeDetailsCSVFile("output2/" + file2 + "_CHANGED_PLAN.csv", changedPlan);

    writeDetailsCSVFile("output2/" + file2 + "_CHANGED_PDC.csv", changedPDC);

  }
  
  compareCSV("CHOL_BEFORE_table_export", "CHOL_AFTER_table_export");
  compareCSV("DIAB_BEFORE_table_export", "DIAB_AFTER_table_export");
  compareCSV("HRM_BEFORE_table_export", "HRM_AFTER_table_export");
  compareCSV("HYP_BEFORE_table_export", "HYP_AFTER_table_export");
  compareCSV("SUPD_BEFORE_table_export", "SUPD_AFTER_table_export");
}

