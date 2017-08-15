package fill

import java.text.SimpleDateFormat
import java.util.Date

object RawFill {
  
  val sdf: SimpleDateFormat = new SimpleDateFormat("M/d/yyyy")
  val sdf2: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

    def dateParse(dateStr: String): String = {
    try {
      sdf2.format(sdf.parse(dateStr))
    } catch {
      case e: Exception => "1800-01-01"
    }
  }
  
}

case class RawFill(
    patientId: String, 
    facilityId: String, 
    soldDate: String,
    daysSupply: String, 
    drugClass: String, 
    drugNDC: String,
    drugName: String,
    gpi: String,
    genericDrugCategory: String,
    rxNumber: String,
    birthDate: String,
    firstName: String, 
    lastName: String) {
    
}