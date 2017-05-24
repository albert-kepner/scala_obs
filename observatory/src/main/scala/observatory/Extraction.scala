package observatory

import java.time.LocalDate

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD

import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

import scala.io.Source
import scala.collection.mutable.HashMap

import org.apache.log4j.{ Level, Logger }

/**
 * 1st milestone: data extraction
 */
object Extraction {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  /**
   * @param year             Year number
   * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
   * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
   * @return A sequence containing triplets (date, location, temperature)
   */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val stationsMap: HashMap[StationKey, Location] = getStationsMap(stationsFile)
    val rawTemps: List[TemperatureRaw] = readTemperatureRawList(year, temperaturesFile)
    val newTemps: List[TemperatureDatum] = {
      rawTemps.filter(x => stationsMap.contains(x.key)).map(y => TemperatureDatum(y.date, stationsMap(y.key), (y.tempFahr - 32.0) * 5.0 / 9.0))
    }
    val result: List[(LocalDate, Location, Double)] = {
      newTemps.map(x => (x.date, x.loc, x.tempCent))
    }
    result
  }

  def getStationsMap(stationsFile: String): HashMap[StationKey, Location] = {
    val stationsMap: HashMap[StationKey, Location] = new HashMap()
    val stationList: List[Station] = readStationList(stationsFile)
    stationList.foreach {
      _ match {
        case Station(key: StationKey, loc: Location) => { stationsMap += ((key, loc)) }
      }
    }
    stationsMap
  }
  /**
   * @param records A sequence containing triplets (date, location, temperature)
   * @return A sequence containing, for each location, the average temperature over the year.
   */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    def keyFn(tuple: (LocalDate, Location, Double)): Location = tuple._2
    val groupByLocation = records.groupBy(keyFn)
    def findGroupAverage(records: Iterable[(LocalDate, Location, Double)]): Double = {
      var count: Int = 0
      var sum: Double = 0.0
      records.foreach { x =>
        count += 1
        sum += x._3
      }
      sum / count
    }
    val locationAverages: Iterable[(Location, Double)] = groupByLocation.mapValues(findGroupAverage)
    locationAverages
  }

  def rawCsvRdd(fileString: String): RDD[String] = {
    Spark.sc.textFile(filePath(fileString))
  }

  def filePath(fileString: String) = {
    val resource = this.getClass.getClassLoader.getResource(fileString)
    if (resource == null) sys.error("Please download the dataset as explained in the assignment instructions")
    new File(resource.toURI).getPath
  }

  def rawCsvStrings(path: String): List[String] = {
    val resource: InputStream = this.getClass.getClassLoader.getResourceAsStream(path)
    val reader: InputStreamReader = new InputStreamReader(resource)
    val bufferedReader: BufferedReader = new BufferedReader(reader)
    var head: List[String] = Nil
    var line: java.lang.String = null
    line = bufferedReader.readLine()
    while (line != null) {
      head = line :: head
      line = bufferedReader.readLine()
    }
    head
  }

  def rawCsvList(path: String): List[String] = {
    val stream: InputStream = getClass.getResourceAsStream(path)
    val fileLines = Source.fromInputStream(stream).getLines.toList
    fileLines
  }

  def readTemperatureRawList(year: Int, fileString: String): List[TemperatureRaw] = {
    val csvList = rawCsvList(fileString)
    def yearFn = textLineToTemperatureRaw(year)(_)
    val stationList: List[TemperatureRaw] = csvList.map(yearFn).filter(x => x match {
      case Some(TemperatureRaw(_, _, _)) => true
      case _                             => false
    }).map(x => x.get)
    stationList
  }

  def textLineToTemperatureRaw(year: Int)(csvLine: String): Option[TemperatureRaw] = {
    val split: Array[String] = csvLine.split(",")
    if (split.length == 5) {
      val trim: Array[String] = for (x <- split) yield x.trim
      if ((trim(0).length > 0 || trim(1).length > 0) && (trim(2).length > 0) && (trim(3).length > 0) && (trim(4).length > 0)) {
        val key: StationKey = StationKey(trim(0), trim(1))
        val date: LocalDate = LocalDate.of(year, trim(2).toInt, trim(3).toInt)
        val temp: Double = trim(4).toDouble
        val result: TemperatureRaw = TemperatureRaw(key, date, temp)
        if (temp < 212.0d) { // Temperature of 9999.9 F indicates missing reading. Also realistic weather temperatures should not reach 212 degrees F.
          Some(result)
        } else {
          None
        }
      } else {
        None
      }
    } else {
      None
    }
  }
  def readStationList(fileString: String): List[Station] = {
    val csvList = rawCsvList(fileString)
    val stationList: List[Station] = csvList.map(textLineToStation).filter(x => x match {
      case Some(Station(_, _)) => true
      case _                   => false
    }).map(x => x.get)
    stationList
  }

  def textLineToStation(csvLine: String): Option[Station] = {
    val split: Array[String] = csvLine.split(",")
    if (split.length == 4) {
      val trim: Array[String] = for (x <- split) yield x.trim
      if ((trim(0).length > 0 || trim(1).length > 0) && (trim(2).length > 0) && (trim(3).length > 0)) {
        val key: StationKey = StationKey(trim(0), trim(1))
        val loc: Location = Location(java.lang.Double.valueOf(trim(2)), java.lang.Double.valueOf(trim(3)))
        val result: Station = Station(key, loc)
        Some(result)
      } else {
        None
      }
    } else {
      None
    }
  }

}
