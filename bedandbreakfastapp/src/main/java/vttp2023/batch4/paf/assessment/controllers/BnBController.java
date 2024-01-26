package vttp2023.batch4.paf.assessment.controllers;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.BookingException;
import vttp2023.batch4.paf.assessment.models.Bookings;
import vttp2023.batch4.paf.assessment.services.ListingsService;

@Controller
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class BnBController {

  // You may add additional dependency injections

  @Autowired
  private ListingsService listingsSvc;

  // IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
  // If this method is changed, any assessment task relying on this method will
  // not be marked
  @GetMapping("/suburbs")
  @ResponseBody
  public ResponseEntity<String> getSuburbs() {
    List<String> suburbs = listingsSvc.getAustralianSuburbs();
    JsonArray result = Json.createArrayBuilder(suburbs).build();
    return ResponseEntity.ok(result.toString());
  }

  // IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
  // If this method is changed, any assessment task relying on this method will
  // not be marked
  @GetMapping("/search")
  @ResponseBody
  public ResponseEntity<String> search(
    @RequestParam MultiValueMap<String, String> params
  ) {
    String suburb = params.getFirst("suburb");
    int persons = Integer.parseInt(params.getFirst("persons"));
    int duration = Integer.parseInt(params.getFirst("duration"));
    float priceRange = Float.parseFloat(params.getFirst("price_range"));

    JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
    listingsSvc
      .findAccommodatations(suburb, persons, duration, priceRange)
      .stream()
      .forEach(acc ->
        arrBuilder.add(
          Json
            .createObjectBuilder()
            .add("id", acc.getId())
            .add("name", acc.getName())
            .add("price", acc.getPrice())
            .add("accommodates", acc.getAccomodates())
            .build()
        )
      );

    return ResponseEntity.ok(arrBuilder.build().toString());
  }

  // IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
  // If this method is changed, any assessment task relying on this method will
  // not be marked
  @GetMapping("/accommodation/{id}")
  @ResponseBody
  public ResponseEntity<String> getAccommodationById(@PathVariable String id) {
    Optional<Accommodation> opt = listingsSvc.findAccommodatationById(id);
    if (opt.isEmpty()) return ResponseEntity.notFound().build();

    return ResponseEntity.ok(Utils.toJson(opt.get()).toString());
  }

  // TODO: Task 6
  @PostMapping(
    path = "/accommodation",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> insertReview(@RequestBody String req)
    throws BookingException {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    String message = "Something went wrong.";

    JsonObject jsonObject = JsonObject.EMPTY_JSON_OBJECT;

    try (JsonReader jsonReader = Json.createReader(new StringReader(req));) {
      jsonObject = jsonReader.readObject();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    if (jsonObject.isEmpty()) {
      message = "Input parameters error.";
      httpStatus = HttpStatus.BAD_REQUEST;
      return new ResponseEntity<>(message, httpStatus);
    }

    Bookings booking = new Bookings();
    booking.setListingId(jsonObject.getString("id"));
    booking.setName(jsonObject.getString("name"));
    booking.setEmail(jsonObject.getString("email"));
    booking.setDuration(jsonObject.getInt("nights"));

    try {
      listingsSvc.createBooking(booking);
    } catch (BookingException e) {
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      return new ResponseEntity<>(e.getMessage(), httpStatus);
    }

    message = "{}";
    httpStatus = HttpStatus.OK;
    return new ResponseEntity<>(message, httpStatus);
  }
}
