package observatory

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

  /**
   * @param temperaturess Sequence of known temperatures over the years (each element of the collection
   *                      is a collection of pairs of location and temperature)
   * @return A function that, given a latitude and a longitude, returns the average temperature at this location
   */
  def average(temperaturess: Iterable[Iterable[(Location, Double)]]): (Int, Int) => Double = {
    val gridSequence = temperaturess.map(makeDataGrid)
    val n = gridSequence.size
    val sum = new Grid()
    def addToGrid( data: Grid): Unit = {
      for (
        lat <- (Grid.minLat to Grid.maxLat);
        lon <- (Grid.minLon to Grid.maxLon)
      ) {
        val s = sum.get(lat,lon) + data.get(lat,lon)
        sum.set(lat,lon,s)
      }
    }
    def divideByN( n: Int) : Unit = {
      for (
        lat <- (Grid.minLat to Grid.maxLat);
        lon <- (Grid.minLon to Grid.maxLon)
      ) {
        val avg = sum.get(lat,lon) / n
        sum.set(lat,lon,avg)
      }
    }
    gridSequence.foreach {
      x => addToGrid(x)
    }
    divideByN(n)
    makeGridFn(sum)
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
        val s = known.get(lat,lon) - normals(lat,lon)
        known.set(lat,lon,s)
      }
    }
    subtractNormals()
    makeGridFn(known)
  }

}

class Grid() {

  val grid: Array[Array[Double]] = new Array(Grid.spanLat)

  val initGrid: Unit = {
    for (lat <- (Grid.minLat to Grid.maxLat)) {
      grid(lat - Grid.minLat) = new Array(Grid.spanLon)
    }
  }

  def get(lat: Int, lon: Int): Double = grid(lat - Grid.minLat)(lon - Grid.minLon)

  def set(lat: Int, lon: Int, temp: Double): Unit = {
    grid(lat - Grid.minLat)(lon - Grid.minLon) = temp
  }
}

object Grid {

  val minLon: Int = -180
  val maxLon: Int = 179
  val minLat: Int = -89
  val maxLat: Int = 90
  val spanLat: Int = 180
  val spanLon: Int = 360

}

