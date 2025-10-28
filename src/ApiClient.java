import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public List<APIQuestion> fetchQuestions(int amount, String difficulty, String type){
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
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(resp.body(), APIResponse.class);

            if (apiResponse != null && apiResponse.results != null) {
                return apiResponse.results;
            } else {
                System.out.println("⚠️ Nessuna domanda trovata per " + difficulty);
                return new ArrayList<>(); // Restituisco una lista vuota
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Errore nella richiesta API: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
