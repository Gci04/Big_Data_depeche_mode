//------------------------------------------------------------------------------------------------------------------------
// SORT EDGES BY RATING
// function for sorting edges by precomputed rating
def sort_neighbours_by_rating (neighbors_ratings : Array[((Int,Int),Double)]) : Array[((Int,Int),Double)]={
  // sort by the second element and reverse to let it be in the descending order
  var sorted_by_rating_neighbors = neighbors_ratings.sortBy(_._2).reverse
  return (sorted_by_rating_neighbors)
}
// usage example
// var neighbors_ratings : Array[((Int,Int),Double)] = Array(
//   ((0,1),15.3),
//   ((1,2),0.6),
//   ((1,3),-2.0),
//   ((2,3),8.9))
// var sorted_by_rating_neighbors = sort_neighbours_by_rating(neighbors_ratings)
// println(sorted_by_rating_neighbors.deep.mkString("\n"))
