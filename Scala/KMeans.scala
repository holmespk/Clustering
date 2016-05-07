
object KMeans extends Serializable {

  def eucledianDistance(a: Array[Double], b: Array[Double]): Double = {
    var dist = 0d
    for (i <- 0 until a.length) {
      dist += Math.pow((a(i) - b(i)), 2)
    }
    return Math.sqrt(dist)
  }

  //point(movieId,ratings), clusterRdd = (dummyMovieId, ratings) : (movieId,rating,cluster)
  def addToCluster(movieId: (Int, Array[Double]), centroidsArray: Array[((Int, Array[Double]), Int)]): ((Int, Array[Double]), Int) = {
    var minDist = Double.MaxValue
    var minCluster = -1

    centroidsArray.map(centroid => {
      var dist = eucledianDistance(centroid._1._2, movieId._2)
      if (dist < minDist) {
        minDist = dist
        minCluster = centroid._2
      }
    })

    return (movieId, minCluster)
  }

  //moviePoints = ((movieId, ratings),clusterId) : centroids ((dummymovie_id,ratings),clusterId)
  def reCalculateCentroids(moviePoints: RDD[((Int, Array[Double]), Int)]): Array[((Int, Array[Double]), Int)] = {

    val clusterRDD = moviePoints.map(_.swap)

    val interCentroids = clusterRDD.reduceByKey((movieA, movieB) => {
      var newPoints = new Array[Double](movieA._2.length)

      for (i <- 0 until movieA._2.length) {
        newPoints.update(i, movieA._2(i) + movieB._2(i))
      }
      (-1, newPoints)
    })

    val newCentroids = interCentroids.map(moviePoint => {
      (moviePoint._1, (-1, moviePoint._2._2.map(value => value / moviePoint._2._2.length)))
    })

    return newCentroids.map(_.swap).collect
  }

  def main(args: Array[String])  {
   
    val testMatRDD = sc.textFile("itemusermat");

    val movieRatingPairRDD = testMatRDD.map { line =>
      {
        val fields = line.split(" ").map(_.toDouble);
        val movieId = fields(0).toInt;
        (movieId, fields.drop(1));

      }
    }
    val initialCentroidsArray = movieRatingPairRDD.takeSample(false, 10, 31).zipWithIndex

    var updatedMoviePoints = movieRatingPairRDD.map(movie => {
      addToCluster(movie, initialCentroidsArray)
    })

    var oldCentroids = new Array[((Int, Array[Double]), Int)](initialCentroidsArray.length)
    var updatedCentroids = initialCentroidsArray
    var i = 0
    while (i < 5 && !(oldCentroids.equals(updatedCentroids))) {
      oldCentroids = updatedCentroids
      updatedCentroids = reCalculateCentroids(updatedMoviePoints)

      updatedMoviePoints = movieRatingPairRDD.map(movie => {
        addToCluster(movie, updatedCentroids)
      })
      i = i + 1

    }

    val movieInfo = sc.textFile("movies.dat")
    
    val movieInfoPairRDD = movieInfo.map { line => {
        val fields = line.split("::")
        (fields(0).toInt,fields.drop(0).mkString(","))
        } }
    
   
    for( i <- 0 until 10){
      println("Cluster: "+i)
      
      val movieIdPairRDD = sc.parallelize(updatedMoviePoints.filter(x => x._2 == i).take(5)).map(x => (x._1._1,""))
      
      val joinedRDD = movieInfoPairRDD.join(movieIdPairRDD)      
      joinedRDD.foreach(x => println(x._2._1)) 
    	
    }
    
  }

}
