package vttp2023.batch4.paf.assessment.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ApiCallService {

  private final WebClient webClient;

  public ApiCallService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public String fetchDataFromApi(String url) {
    return webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
  }
}
