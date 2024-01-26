package vttp2023.batch4.paf.assessment.services;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForexService {

  @Autowired
  private ApiCallService apiCallService;

  // TODO: Task 5
  public float convert(String from, String to, float amount) {
    String jsonRequest = apiCallService.fetchDataFromApi(
      String.format(
        "https://api.frankfurter.app/latest?amount=%f&from=%s&to=%s",
        amount,
        from,
        to
      )
    );
    try (
      JsonReader jsonReader = Json.createReader(new StringReader(jsonRequest));
    ) {
      JsonObject jsonObject = jsonReader.readObject();
      System.out.println(jsonObject.getJsonObject("rates"));
      Float sgd = Float.parseFloat(
        jsonObject.getJsonObject("rates").getJsonNumber("SGD").toString()
      );
      return sgd;
    } catch (Exception e) {
      return -1000f;
    }
  }
}
