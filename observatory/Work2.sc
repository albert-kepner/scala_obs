package observatory

object Work2 {

  type s1 = List[String]

  type s2 = (Int, String)

  type Data1 = (Int, Iterable[(Location, Double)])

  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  val t: Double = 85.05112877980659               //> t  : Double = 85.05112877980659

  val t2: Double = t.round                        //> t2  : Double = 85.0
  println(s"t = $t , t2 = $t2, xxx")              //> t = 85.05112877980659 , t2 = 85.0, xxx

  val a1: Double = 88.5                           //> a1  : Double = 88.5
  val a2: Double = -88.5                          //> a2  : Double = -88.5

  val a3: Int = a2.floor.toInt                    //> a3  : Int = -89

  println(s"a2 = $a2; a3 = $a3")                  //> a2 = -88.5; a3 = -89

  val year1 = 1975                                //> year1  : Int = 1975
  val year2 = 1990                                //> year2  : Int = 1990

  for (year <- (year1 to year2)) {
    println(s"year = $year")                      //> year = 1975
                                                  //| year = 1976
                                                  //| year = 1977
                                                  //| year = 1978
                                                  //| year = 1979
                                                  //| year = 1980
                                                  //| year = 1981
                                                  //| year = 1982
                                                  //| year = 1983
                                                  //| year = 1984
                                                  //| year = 1985
                                                  //| year = 1986
                                                  //| year = 1987
                                                  //| year = 1988
                                                  //| year = 1989
                                                  //| year = 1990
  }

}