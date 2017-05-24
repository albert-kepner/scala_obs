package observatory

import org.scalatest.{ FunSuite, BeforeAndAfterAll }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import java.time.LocalDate

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD
import org.apache.log4j.{ Level, Logger }

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite with BeforeAndAfterAll {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  def initializeSpark(): Boolean =
    try {
      Spark
      true
    } catch {
      case ex: Throwable =>
        println(ex.getMessage)
        ex.printStackTrace()
        false
    }

  override def afterAll(): Unit = {
    assert(initializeSpark(), " -- did you fill in all the values in Spark (conf, sc)?")
    import Spark._
    sc.stop()
  }

  test("initial test") {
    assert(initializeSpark(), " -- did you fill in all the values in Spark (conf, sc)?")
  }

  test("count station.csv lines") {
    val stationRawRDD: RDD[String] = Extraction.rawCsvRdd("stations.csv")
    val count: Long = stationRawRDD.count()
    println("stationRawRDD station line count = " + count)
  }

  test("count /1975.csv lines") {
    val stationRawRDD: RDD[String] = Extraction.rawCsvRdd("1975.csv")
    val count: Long = stationRawRDD.count()
    println("/1975.csv line count = " + count)
  }

  test("rawCsvList /stations.csv") {
    val theList = Extraction.rawCsvList("/stations.csv")
    println("/stations.csv list length = " + theList.length)
    theList.take(5).foreach { println _ }
  }

  test("readStationList /stations.csv") {
    val theList = Extraction.readStationList("/stations.csv")
    println("/stations.csv list length = " + theList.length)
    theList.take(5).foreach { println(_) }
  }

  test("readTemperatureRawList /stations.csv") {
    val theList = Extraction.readTemperatureRawList(1975, "/1975.csv")
    println("/1975.csv TemperatureRaw list length = " + theList.length)
    theList.take(10).foreach { println(_) }
  }
  
  test("locateTemperatures for 1975 " ) {
    val theList = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
    println("1975 TemperatureDatum list sample take(10) = " )
    def predFn(tuple: (LocalDate, Location, Double)) : Boolean = {
      tuple._2 === Location(70.933,-8.667)
    }
    theList.filter(predFn).take(10).foreach { println _ }
  }
  
  test("getStationsMap find example Location") {
    val key = StationKey("010010","")
    val theMap = Extraction.getStationsMap("/stations.csv")
    val loc = theMap(key)
    println("Location = "+loc)
  }
  
  test("locationYearlyAverageRecords...") {
        val theList = Extraction.locateTemperatures(1975, "/stations.csv", "/1975.csv")
    println("1975 TemperatureDatum list sample take(10) = " )
    def predFn(tuple: (LocalDate, Location, Double)) : Boolean = {
      tuple._2 === Location(70.933,-8.667)
    }
    theList.filter(predFn).take(10).foreach { println _ }
    
    val anAverageList = Extraction.locationYearlyAverageRecords(theList.filter(predFn).take(10))
    println("Average for selected location: "+Location(70.933,-8.667) )
    anAverageList.foreach {println _ }

  }

}