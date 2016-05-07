def pearsonCorrelationCoefficient (movieObject1:(Int,Array[Int]),movieObject2:(Int,Array[Int])) : (Int,Double) = {
   var coefficient = 0d;
   val n = movieObject1._2.length;

   val x = movieObject1._2.sum;
   val xsquare = movieObject1._2.foldLeft(0.0)(_ + Math.pow(_, 2));
   

   val y = movieObject2._2.sum;
   val ysquare = movieObject2._2.foldLeft(0.0)(_ + Math.pow(_, 2));
   val xy =movieObject1._2.zip(movieObject2._2).map {a => a._1 * a._2 }.sum;

   var numerator = ( n * xy ) - (x*y);
   var denominator = (Math.sqrt((n * xsquare)  -  Math.pow(x, 2)) * Math.sqrt((n * ysquare)  - ( Math.pow(y, 2))));
   coefficient = (numerator / denominator);
   (movieObject2._1,coefficient);
}

object SimilarMovies extends Serializable{
   def main(args: Array[String]) {
     val movieId = args(0)
     
     val itemUserMat =  sc.textFile("itemusermat")
     
     val movieRatingPair = itemUserMat.map( line => { 
      val fields = line.split(" ").map(_.toInt);
      val movieId = fields(0).toInt;
      (movieId,fields.drop(1));
      }
     )
     val movieIdwithRatings = movieRatingPair.filter(x =>( x._1== movieId.toInt)).collect

     val similarMovies = movieRatingPair.map( line => {
	val moviewithCoefficient =pearsonCorrelationCoefficient((movieIdwithRatings(0)._1,movieIdwithRatings(0)._2),line)
        moviewithCoefficient
     })
     
    val fiveSimilar = sc.parallelize(similarMovies.sortBy(x =>x._2,false).take(6))
   
    val moviesFile = sc.textFile("movies.dat").map(line => {
      val moviesplit = line.split("::")
      (moviesplit(0).toInt,(moviesplit(1),moviesplit(2)))
      }
    )
   val similarMovieList = fiveSimilar.join(moviesFile).map(line =>{
	
	val finalAnswer = (line._1,line._2._2._1,line._2._2._2)
        finalAnswer
    })

    similarMovieList.foreach(println)
   }
    
}

