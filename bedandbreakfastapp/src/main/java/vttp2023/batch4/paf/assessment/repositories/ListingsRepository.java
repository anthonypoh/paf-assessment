package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {

  // You may add additional dependency injections

  @Autowired
  private MongoTemplate template;

  /*
   * Write the native MongoDB query that you will be using for this method
   * inside this comment block
   *
     db.listings.aggregate([
			{
				$match: {
					"address.suburb": { $exists: true },
					"address.suburb": { $ne: "" },
				},
			},
			{
				$project: {
					_id: "$address.suburb",
				},
			},
			{
				$sort: {
					_id: 1,
				},
			},
		])
   *
   */
  public List<String> getSuburbs(String country) {
    MatchOperation matchStage = Aggregation.match(
      Criteria.where("address.suburb").ne("").exists(true)
    );

    ProjectionOperation projectStage = Aggregation
      .project()
      .and("address.suburb")
      .as("_id");

    SortOperation sortStage = Aggregation.sort(Sort.Direction.ASC, "_id");

    Aggregation pipeline = Aggregation.newAggregation(
      matchStage,
      projectStage,
      sortStage
    );

    AggregationResults<Document> results = template.aggregate(
      pipeline,
      "listings",
      Document.class
    );

    List<String> suburbs = new ArrayList<>();
    for (Document document : results) {
      String suburb = document.getString("_id");
      if (!suburbs.contains(suburb)) suburbs.add(suburb);
    }
    return suburbs;
  }

  /*
   * Write the native MongoDB query that you will be using for this method
   * inside this comment block
   * 
	 * [
			{
				$match: {
					"address.suburb": /suburb/i,
					price: { $lte: 100 },
					accommodates: { $gte: 2 },
					min_nights: { $lte: 2 },
				},
			},
			{
				$project: {
					_id: 1,
					name: 1,
					accommodates: 1,
					price: 1,
				},
			},
		]
   *
   *
   */
  public List<AccommodationSummary> findListings(
    String suburb,
    int persons,
    int duration,
    float priceRange
  ) {
    MatchOperation matchStage = Aggregation.match(
      Criteria
        .where("address.suburb")
        .regex(suburb, "i")
        .and("price")
        .lte(priceRange)
        .and("accommodates")
        .gte(persons)
        .and("min_nights")
        .lte(duration)
    );

    ProjectionOperation projectStage = Aggregation.project(
      "_id",
      "name",
      "accommodates",
      "price"
    );

    Aggregation pipeline = Aggregation.newAggregation(matchStage, projectStage);

    AggregationResults<Document> results = template.aggregate(
      pipeline,
      "listings",
      Document.class
    );

    List<AccommodationSummary> accoms = new ArrayList<>();
    for (Document doc : results) {
      AccommodationSummary accom = new AccommodationSummary();
      accom.setId(doc.getString("_id"));
      accom.setName(doc.getString("name"));
      accom.setAccomodates(doc.getInteger("accommodates"));
      accom.setPrice(doc.get("price", Number.class).floatValue());
      accoms.add(accom);
    }
    return accoms;
  }

  // IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
  // If this method is changed, any assessment task relying on this method will
  // not be marked
  public Optional<Accommodation> findAccommodatationById(String id) {
    Criteria criteria = Criteria.where("_id").is(id);
    Query query = Query.query(criteria);

    List<Document> result = template.find(query, Document.class, "listings");
    if (result.size() <= 0) return Optional.empty();

    return Optional.of(Utils.toAccommodation(result.getFirst()));
  }
}
