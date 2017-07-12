package observatory

import com.sksamuel.scrimage.{ Image, Pixel }
/**
 * 4th milestone: value-added information
 */
object Manipulation {

  /**
   * @param temperatures Known temperatures
   * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
   *         returns the predicted temperature at this location
   */
  def makeGrid(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    val g: Grid = new Grid()

    for (
      lat <- (Grid.minLat to Grid.maxLat);
      lon <- (Grid.minLon to Grid.maxLon)
    ) {
      val temp = Visualization.predictTemperature(temperatures, Location(lat, lon))
      g.set(lat, lon, temp)
    }

    def gridLookup(lat: Int, lon: Int): Double = {
      g.get(lat, lon)
    }
    gridLookup
  }
  def makeGridFn(data: Grid): (Int, Int) => Double = {
    def gridLookup(lat: Int, lon: Int): Double = {
      data.get(lat, lon)
    }
    gridLookup
  }
  def makeGridFn4(data: Grid4): (Int, Int) => Double = {
    def gridLookup(lat: Int, lon: Int): Double = {
      data.get(lat, lon)
    }
    gridLookup
  }
  def makeDataGrid(temperatures: Iterable[(Location, Double)]): Grid = {
    val g: Grid = new Grid()

    for (
      lat <- (Grid.minLat to Grid.maxLat);
      lon <- (Grid.minLon to Grid.maxLon)
    ) {
      val temp = Visualization.predictTemperature(temperatures, Location(lat, lon))
      g.set(lat, lon, temp)
    }
    g
  }
  def makeDataGrid4(temperatures: Iterable[(Location, Double)]): Grid4 = {
    val g: Grid4 = new Grid4()

    for (
      lat <- (Grid.minLat4 to Grid.maxLat by 4);
      lon <- (Grid.minLon to Grid.maxLon by 4)
    ) {
      val temp = Visualization.predictTemperature(temperatures, Location(lat, lon))
      g.set(lat, lon, temp)
    }
    g
  }

  /**
   * @param temperaturess Sequence of known temperatures over the years (each element of the collection
   *                      is a collection of pairs of location and temperature)
   * @return A function that, given a latitude and a longitude, returns the average temperature at this location
   */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {
    val gridSequence = temperaturess.map(makeDataGrid)
    val n = gridSequence.size
    val sum = new Grid()
    def addToGrid(data: Grid): Unit = {
      for (
        lat <- (Grid.minLat to Grid.maxLat);
        lon <- (Grid.minLon to Grid.maxLon)
      ) {
        val s = sum.get(lat, lon) + data.get(lat, lon)
        sum.set(lat, lon, s)
      }
    }
    def divideByN(n: Int): Unit = {
      for (
        lat <- (Grid.minLat to Grid.maxLat);
        lon <- (Grid.minLon to Grid.maxLon)
      ) {
        val avg = sum.get(lat, lon) / n
        sum.set(lat, lon, avg)
      }
    }
    gridSequence.foreach {
      x => addToGrid(x)
    }
    divideByN(n)
    makeGridFn(sum)
  }

  def average4(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {
    val gridSequence = temperaturess.map(makeDataGrid4)
    val n = gridSequence.size
    val sum = new Grid4()
    def addToGrid(data: Grid4): Unit = {
      for (
        lat <- (Grid.minLat4 to Grid.maxLat by 4);
        lon <- (Grid.minLon4 to Grid.maxLon by 4)
      ) {
        val s = sum.get(lat, lon) + data.get(lat, lon)
        sum.set(lat, lon, s)
      }
    }
    def divideByN(n: Int): Unit = {
      for (
        lat <- (Grid.minLat4 to Grid.maxLat by 4);
        lon <- (Grid.minLon4 to Grid.maxLon by 4)
      ) {
        val avg = sum.get(lat, lon) / n
        sum.set(lat, lon, avg)
      }
    }
    gridSequence.foreach {
      x => addToGrid(x)
    }
    divideByN(n)
    makeGridFn4(sum)
  }

  /**
   * @param temperatures Known temperatures
   * @param normals A grid containing the “normal” temperatures
   * @return A grid containing the deviations compared to the normal temperatures
   */
  def deviation(temperatures: Iterable[(Location, Double)], normals: (Int, Int) => Double): (Int, Int) => Double = {
    val known: Grid = makeDataGrid(temperatures)
    def subtractNormals(): Unit = {
      for (
        lat <- (Grid.minLat to Grid.maxLat);
        lon <- (Grid.minLon to Grid.maxLon)
      ) {
        val s = known.get(lat, lon) - normals(lat, lon)
        known.set(lat, lon, s)
      }
    }
    subtractNormals()
    makeGridFn(known)
  }
  def deviation4(temperatures: Iterable[(Location, Double)], normals: (Int, Int) => Double): (Int, Int) => Double = {
    val known: Grid4 = makeDataGrid4(temperatures)
    def subtractNormals(): Unit = {
      for (
        lat <- (Grid.minLat4 to Grid.maxLat by 4);
        lon <- (Grid.minLon4 to Grid.maxLon by 4)
      ) {
        val s = known.get(lat, lon) - normals(lat, lon)
        known.set(lat, lon, s)
      }
    }
    subtractNormals()
    makeGridFn4(known)
  }

  /**
   * @param temperaturess Sequence of known temperatures over the years (each element of the collection
   *                      is a collection of pairs of location and temperature)
   * @return A function that, given a latitude and a longitude, returns the average temperature at this location
   */
  def gridForSingleYear(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    makeGridFn(makeDataGrid(temperatures))
  }

  def generateTilesFromGridForYear(year: Integer): Unit = {
    val start: Long = System.currentTimeMillis()
    val stationsFile = "/stations.csv"
    val temperaturesFile = s"/$year.csv"
    val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    println(s"start temperature grid for year = $year at time: " + new java.util.Date())
    val gridFn = gridForSingleYear(avgTempsForYear)
    val zoom = 0
    val x = 0
    val y = 0
    val colors = Visualization.standardColors
    val image: Image = Visualization2.visualizeGrid(gridFn, colors, zoom, x, y)
    println("ready to write image to file at time = " + new java.util.Date())
    Interaction.writeImageToFile("target/temp1", image, year, zoom, x, y)
    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }
  /**
   * @param temperaturess Sequence of known temperatures over the years (each element of the collection
   *                      is a collection of pairs of location and temperature)
   * @return A function that, given a latitude and a longitude, returns the average temperature at this location
   */
  def gridForSingleYear4(temperatures: Iterable[(Location, Double)]): (Int, Int) => Double = {
    makeGridFn4(makeDataGrid4(temperatures))
  }

  def generateTilesFromGridForYear4(year: Integer): Unit = {
    val start: Long = System.currentTimeMillis()
    val stationsFile = "/stations.csv"
    val temperaturesFile = s"/$year.csv"
    val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    println(s"start temperature grid for year = $year at time: " + new java.util.Date())
    val gridFn = gridForSingleYear4(avgTempsForYear)
    println(s"Finished grid for year = $year at time: " + new java.util.Date())
    val zoom = 0
    val x = 0
    val y = 0
    val colors = Visualization.standardColors
    val image: Image = Visualization2.visualizeGrid4(gridFn, colors, zoom, x, y)
    println("ready to write image to file at time = " + new java.util.Date())
    Interaction.writeImageToFile("target/temp1", image, year, zoom, x, y)
    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }

  def generateTilesFromGridForYear4Zoom(year: Integer): Unit = {
    val start: Long = System.currentTimeMillis()
    val stationsFile = "/stations.csv"
    val temperaturesFile = s"/$year.csv"
    val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    println(s"start temperature grid for year = $year at time: " + new java.util.Date())
    val gridFn = gridForSingleYear4(avgTempsForYear)
    println(s"Finished grid for year = $year at time: " + new java.util.Date())

    for (zoom <- (0 to 3)) {
      val twoPower: Int = 1 << zoom
      for (
        row <- 0 until twoPower;
        col <- 0 until twoPower
      ) {
        val x = col
        val y = row
        val colors = Visualization.standardColors
        val image: Image = Visualization2.visualizeGrid4(gridFn, colors, zoom, x, y)
        println("ready to write image to file at time = " + new java.util.Date())
        Interaction.writeImageToFile("target/temp1", image, year, zoom, x, y)
      }
    }

    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }

  def yearRangeAverageGrid4(year1: Int, year2: Int): (Int, Int) => Double = {
    val start: Long = System.currentTimeMillis()
    val stationsFile = "/stations.csv"
    val sum = new Grid4()
    for (year <- (year1 to year2)) {
      println(s"year = $year")
      val temperaturesFile = s"/$year.csv"
      val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
      val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
      println(s"temps for one year = $year at time: " + new java.util.Date())
      val grid: Grid4 = Manipulation.makeDataGrid4(avgTempsForYear)
      addToGrid(grid)
    }
    divideByN(year2 - year1 + 1)
    def addToGrid(data: Grid4): Unit = {
      for (
        lat <- (Grid.minLat4 to Grid.maxLat by 4);
        lon <- (Grid.minLon4 to Grid.maxLon by 4)
      ) {
        val s = sum.get(lat, lon) + data.get(lat, lon)
        sum.set(lat, lon, s)
      }
    }
    def divideByN(n: Int): Unit = {
      for (
        lat <- (Grid.minLat4 to Grid.maxLat by 4);
        lon <- (Grid.minLon4 to Grid.maxLon by 4)
      ) {
        val avg = sum.get(lat, lon) / n
        sum.set(lat, lon, avg)
      }
    }
    makeGridFn4(sum)
  }
  def generateTilesForAverageYearRange(year1: Int, year2: Int): Unit = {
    val start: Long = System.currentTimeMillis()
    val stationsFile = "/stations.csv"
    var yearRange: List[Iterable[(Location, Double)]] = Nil
    for (year <- (year1 to year2)) {
      println(s"year = $year")
      val temperaturesFile = s"/$year.csv"
      val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
      val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
      println(s"temps for one year = $year at time: " + new java.util.Date())
      yearRange = avgTempsForYear :: yearRange
    }
    val targetYear = 1991
    val temperaturesFile = s"/$targetYear.csv"
    val allTempsForYear = Extraction.locateTemperatures(targetYear, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    val normals = Manipulation.average4(yearRange)
    var deviations = Manipulation.deviation4(avgTempsForYear, normals)
    println("15 year deviations done at time: " + new java.util.Date())
    for (zoom <- (0 to 3)) {
      val twoPower: Int = 1 << zoom
      for (
        row <- 0 until twoPower;
        col <- 0 until twoPower
      ) {
        val x = col
        val y = row
        val colors = Visualization.standardColors
        val image: Image = Visualization2.visualizeGrid4(deviations, colors, zoom, x, y)
        println("ready to write image to file at time = " + new java.util.Date())
        Interaction.writeImageToFileDeviations("target", image, targetYear, zoom, x, y)
      }
    }

    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }
  def generateTilesForDeviations(targetYear: Int, year1: Int, year2: Int): Unit = {
    val start: Long = System.currentTimeMillis()
    val normals = Manipulation.yearRangeAverageGrid4(year1, year2)

    println(s"year = $targetYear")
    val stationsFile = "/stations.csv"
    val temperaturesFile = s"/$targetYear.csv"
    val allTempsForYear = Extraction.locateTemperatures(targetYear, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    println(s"temps for one year = $targetYear at time: " + new java.util.Date())

    var deviations = Manipulation.deviation4(avgTempsForYear, normals)
    println(s"15 year deviations for year $targetYear done at time: " + new java.util.Date())
    for (zoom <- (0 to 3)) {
      val twoPower: Int = 1 << zoom
      for (
        row <- 0 until twoPower;
        col <- 0 until twoPower
      ) {
        val x = col
        val y = row
        val colors = Visualization.deviationColors
        val image: Image = Visualization2.visualizeGrid4(deviations, colors, zoom, x, y)
        println("ready to write image to file at time = " + new java.util.Date())
        Interaction.writeImageToFileDeviations("target", image, targetYear, zoom, x, y)
      }
    }

    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }
  def generateTilesForDeviations(fromYear: Int, toYear: Int, year1: Int, year2: Int): Unit = {
    val start: Long = System.currentTimeMillis()
    val normals = Manipulation.yearRangeAverageGrid4(year1, year2)
    for (targetYear <- (fromYear to toYear)) {

      println(s"year = $targetYear")
      val stationsFile = "/stations.csv"
      val temperaturesFile = s"/$targetYear.csv"
      val allTempsForYear = Extraction.locateTemperatures(targetYear, stationsFile, temperaturesFile)
      val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
      println(s"temps for one year = $targetYear at time: " + new java.util.Date())

      var deviations = Manipulation.deviation4(avgTempsForYear, normals)
      println(s"15 year deviations for year $targetYear done at time: " + new java.util.Date())
      for (zoom <- (0 to 3)) {
        val twoPower: Int = 1 << zoom
        for (
          row <- 0 until twoPower;
          col <- 0 until twoPower
        ) {
          val x = col
          val y = row
          val colors = Visualization.deviationColors
          val image: Image = Visualization2.visualizeGrid4(deviations, colors, zoom, x, y)
          println("ready to write image to file at time = " + new java.util.Date())
          Interaction.writeImageToFileDeviations("target", image, targetYear, zoom, x, y)
        }
      }
    }
    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed time for run = $elapsed")
  }

}

class Grid() {

  val grid: Array[Array[Double]] = new Array(Grid.spanLat)

  val initGrid: Unit = {
    for (lat <- (Grid.minLat to Grid.maxLat)) {
      grid(lat - Grid.minLat) = new Array(Grid.spanLon)
    }
  }

  def get(lat: Int, lon: Int): Double = {
    val (lat2, lon2) = locInRange(lat, lon)
    grid(lat2 - Grid.minLat)(lon2 - Grid.minLon)
  }
  def locInRange(lat: Int, lon: Int): (Int, Int) = {
    val lat2: Int = lat match {
      case _ if (lat < Grid.minLat) => Grid.minLat
      case _ if (lat > Grid.maxLat) => Grid.maxLat
      case _                        => lat
    }
    val lon2: Int = lon match {
      case _ if (lon < Grid.minLon) => Grid.minLon
      case _ if (lon > Grid.maxLon) => Grid.maxLon
      case _                        => lon
    }
    (lat2, lon2)
  }

  def set(lat: Int, lon: Int, temp: Double): Unit = {
    val (lat2, lon2) = locInRange(lat, lon)
    grid(lat2 - Grid.minLat)(lon2 - Grid.minLon) = temp
  }

}

class Grid4() {

  val grid: Array[Array[Double]] = new Array(Grid4.spanLat)

  val initGrid: Unit = {
    for (lat <- (0 until Grid4.spanLat)) {
      grid(lat) = new Array(Grid4.spanLon)
    }
  }

  def get(lat: Int, lon: Int): Double = {
    val (lat2, lon2) = locInRange(lat, lon)
    grid((lat2 - Grid.minLat) / 4)((lon2 - Grid.minLon) / 4)
  }
  def locInRange(lat: Int, lon: Int): (Int, Int) = {
    val lat2: Int = lat match {
      case _ if (lat < Grid.minLat4) => Grid.minLat4
      case _ if (lat > Grid.maxLat)  => Grid.maxLat
      case _                         => lat
    }
    val lon2: Int = lon match {
      case _ if (lon < Grid.minLon4) => Grid.minLon4
      case _ if (lon > Grid.maxLon)  => Grid.maxLon
      case _                         => lon
    }
    (lat2, lon2)
  }

  def set(lat: Int, lon: Int, temp: Double): Unit = {
    val (lat2, lon2) = locInRange(lat, lon)
    grid((lat2 - Grid.minLat4) / 4)((lon2 - Grid.minLon4) / 4) = temp
  }

}

object Grid {

  val minLon: Int = -180
  val minLon4: Int = -180
  val maxLon: Int = 179
  val minLat: Int = -89
  val minLat4: Int = -88
  val maxLat: Int = 90
  val spanLat: Int = 180
  val spanLon: Int = 360

}
object Grid4 {

  val spanLat: Int = 45
  val spanLon: Int = 90

}

