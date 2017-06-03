object scratch1 {

  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  val start1: Long = System.currentTimeMillis()   //> start1  : Long = 1496523945799
  println(s"start1 = $start1")                    //> start1 = 1496523945799

  val end1: Long = System.currentTimeMillis()     //> end1  : Long = 1496523945803

  val elapsedSeconds: Float = (end1 - start1) / 1000.0f
                                                  //> elapsedSeconds  : Float = 0.004

  println(s"elapsedSeconds = $elapsedSeconds")    //> elapsedSeconds = 0.004

}