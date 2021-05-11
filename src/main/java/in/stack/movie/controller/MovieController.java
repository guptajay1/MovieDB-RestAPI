package in.stack.movie.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import in.stack.movie.entity.Movie;
import in.stack.movie.entity.MovieList;
import in.stack.movie.entity.MovieSummary;
import in.stack.movie.service.MovieService;

@RestController
@RequestMapping("api/movies")
public class MovieController {

	@Value("${api.key}")
	private String apiKey;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MovieService movieService;

	@GetMapping("/{movieId}")
	public ResponseEntity<?> getMovie(@PathVariable("movieId") String movieId) {

		ResponseEntity<?> responseEntity = null;
		try {

			MovieSummary movieSummary = restTemplate.getForObject(
					"https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey, MovieSummary.class);
			if (movieSummary != null) {
				Movie movie = new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
				responseEntity = new ResponseEntity<Movie>(movie, HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<String>("Conflict Data", HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;
	}

	@GetMapping("/latest")
	public Movie getLatestMovie() {

		MovieSummary movieSummary = restTemplate
				.getForObject("https://api.themoviedb.org/3/movie/latest?api_key=" + apiKey, MovieSummary.class);

		return new Movie(movieSummary.getId(), movieSummary.getTitle(), movieSummary.getOverview());
	}

	@GetMapping("/toprated/{pageNo}")
	public MovieList getTopRatedMovies(@PathVariable("pageNo") int pageNo) {

		MovieList movieSummary = restTemplate.getForObject(
				"https://api.themoviedb.org/3/movie/top_rated?api_key=" + apiKey + "&language=en-US&page=" + pageNo,
				MovieList.class);

		return movieSummary;
	}

	@GetMapping("/popular/{pageNo}")
	public MovieList getPopularMovies(@PathVariable("pageNo") int pageNo) throws IOException {

		MovieList movie = restTemplate.getForObject(
				"https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey + "&language=en-US&page=" + pageNo,
				MovieList.class);

		return movie;
	}

	@GetMapping("/all")
	public List<Movie> getAllFavouriteMovies() {

		return movieService.getAllFavouriteMovies();
	}
}