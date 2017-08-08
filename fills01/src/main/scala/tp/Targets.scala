package tp

import java.text.SimpleDateFormat
import java.util.Date

object RawPat {
  
  val sdf: SimpleDateFormat = new SimpleDateFormat("M/d/yyyy")
  
  def dateParse(dateStr: String) : java.util.Date = {
    sdf.parse(dateStr)
  }
  
  def parseRx(rxStr: String) : String = {
    val dashIndex = rxStr.indexOf("-");
    if(dashIndex > -1) {
      rxStr.slice(0, dashIndex)
    } else {
      rxStr
    }
  }
  
}

case class RawPat(
    facilityId: String, 
    drugClass: String,
    patientName: String,
    rxNumber: String, // 4th
    pdc12: String,
    pdc6: String,
    planBin: String,
    planCode: String, // 8th
    planGroup: String,
    lastSoldDate: Date, // 10th
    lastDaysSupply: String, 
    patientPhone: String, // 12th
    birthDate: Date, // 13th
    daysSinceReview: String,
    note1: String,
    note2: String, // 16th
    note3: String) // 17th
    {

  def matchPerson(other: RawPat) : Boolean = {
    (this.patientName == other.patientName
        && this.drugClass == other.drugClass
    && this.patientPhone == other.patientPhone
    && this.birthDate == other.birthDate)
  }
  
  def matchDetail(other: RawPat) : Boolean = {
        (this.facilityId == other.facilityId &&
    this.drugClass == other.drugClass &&
    this.patientName == other.patientName &&
    this.rxNumber == other.rxNumber &&
    this.pdc12 == other.pdc12 &&
    this.pdc6 == other.pdc6 &&
    this.planBin == other.planBin &&
    this.planCode == other.planCode &&
    this.planGroup == other.planGroup &&
    this.lastSoldDate == other.lastSoldDate &&
    this.lastDaysSupply == other.lastDaysSupply &&
    this.patientPhone == other.patientPhone &&
    this.birthDate == other.birthDate &&
    this.daysSinceReview == other.daysSinceReview &&
    this.note1 == other.note1 &&
    this.note2 == other.note2 &&
    this.note3 == other.note3)
  }
  
}