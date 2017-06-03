package fill

import java.text.SimpleDateFormat
import java.util.Date

object RawFill {
  
  val sdf: SimpleDateFormat = new SimpleDateFormat("M/d/yyyy")
  
  def dateParse(dateStr: String) : java.util.Date = {
    sdf.parse(dateStr)
  }
  
}

case class RawFill(
    patientId: String, 
    facilityId: String, 
    soldDate: Date,
    daysSupply: String, 
    drugClass: String, 
    drugNDC: String,
    drugName: String,
    birthDate: Date,
    firstName: String, 
    lastName: String) {
  
//  def soldDate: java.util.Date = {
//    RawFill.dateParse(soldDateStr)
//  }
//  
//  def birthDate: java.util.Date = {
//    RawFill.dateParse(birthDateStr)
//  }
  
}