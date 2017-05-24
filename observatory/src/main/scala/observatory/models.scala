package observatory

import java.time.LocalDate

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class StationKey(stn: String, wban: String)

case class Station(key: StationKey, loc: Location)

case class TemperatureRaw(key: StationKey, date: LocalDate, tempFahr: Double)

case class TemperatureDatum(date: LocalDate, loc: Location, tempCent: Double)

