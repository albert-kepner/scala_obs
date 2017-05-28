package observatory

object Work2 {

	type s1 = List[String]
	
	type s2 = (Int, String)
	
	type Data1 = (Int, Iterable[(Location, Double)])
	
	
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val t: Double = 85.05112877980659               //> t  : Double = 85.05112877980659
  
  val t2: Double = t.round                        //> t2  : Double = 85.0
  println(s"t = $t , t2 = $t2, xxx")              //> t = 85.05112877980659 , t2 = 85.0, xxx
}