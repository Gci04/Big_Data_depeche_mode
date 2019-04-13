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

//------------------------------------------------------------------------------------------------------------------------
// FILTER EXISTING EDGES
// function for filtering processing_edges from already existed eedges
def filter_existing_edges (existing_edges : Array[(Int,Int)], processing_edges_ratings : Array[((Int,Int),Double)]) : Array[((Int,Int),Double)] = {
  // initializing of result
  var result_array : Array[((Int,Int),Double)] = Array()
  // iterating over processing_edges
  for (edge_n_rating <- processing_edges_ratings) {
    // check that edge did not exist early
    if (!existing_edges.contains(edge_n_rating._1)) {
      // append edge to result
      result_array = result_array:+((edge_n_rating))
    }
  }

   return (result_array)
}
// usage example
// var existing_edges = Array((0,1),(0,2),(1,4),(2,3))
// var processing_edges_ratings : Array[((Int,Int),Double)] = Array(
//   ((0,1),12.6),
//   ((1,2),0.0),
//   ((1,3),-3.43),
//   ((2,3),23.1),
//   ((3,4),0.001))
// var processing_edges_ratings_filtered = filter_existing_edges(
//   existing_edges, processing_edges_ratings)
// println(processing_edges_ratings_filtered.deep.mkString("\n"))
