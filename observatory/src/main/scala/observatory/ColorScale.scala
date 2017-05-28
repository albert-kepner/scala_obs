package observatory
case class CS(temp: Double, red: Int, green: Int, blue: Int) {

}

object ColorScale {

  val listCS: Array[CS] = 
    Array(CS(60, 255, 255, 255),
    CS(32, 255, 0, 0),
    CS(12, 255, 255, 0),
    CS(0, 0, 255, 255),
    CS(-15, 0, 0, 255),
    CS(-27, 255, 0, 255),
    CS(-50, 33, 0, 107),
    CS(-60, 0, 0, 0))

  val cs: Iterable[(Double, Color)] = {
    listCS.map( x => (x.temp, Color(x.red, x.green, x.blue) ) )
  }

}
