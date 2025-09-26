import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public String fetchQuestions(int amount, String difficulty, String type){
        // https://opentdb.com/api.php?amount=5&difficulty=easy&type=multiple

        // Creo l'url parametrizzato
        String url = "https://opentdb.com/api.php?amount=" + amount + "&difficulty=" + difficulty + "&type=" +  type;

        // Creo la richiesta
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        // Creo un oggetto risposta e controllo se manda eccezioni
        HttpResponse<String> resp;
        try {
            resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch questions:" + e.getMessage(), e);
        }

        if(resp == null){
            throw new RuntimeException("No response received from the API");
        }

        return resp.body();
    }
}
