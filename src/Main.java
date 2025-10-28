import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ApiClient apiClient = new ApiClient();
        Gson gson = new Gson();

        System.out.println("--- CHI VUOL ESSERE MATURATO? ---");
        System.out.print("Benvenuto! Inserisci il tuo nome: ");
        String nomeGiocatore = input.nextLine();

        int risposteCorrette = 0;
        boolean usato5050 = false;
        boolean usatoPubblico = false;

        // Scarico 10 domande (5 easy, 3 medium, 2 hard)
        List<APIQuestion> domande = new ArrayList<>();
        domande.addAll(apiClient.fetchQuestions(5, "easy", "multiple"));
        domande.addAll(apiClient.fetchQuestions(3, "medium", "multiple"));
        domande.addAll(apiClient.fetchQuestions(2, "hard", "multiple"));

        // Gioco
        for (int i = 0; i < domande.size(); i++) {
            APIQuestion q = domande.get(i); // Prendo la domanda

            System.out.println("\nDomanda " + (i + 1) + " (" + q.difficulty + ")"); // La stampo con a fianco la difficoltà
            System.out.println(decodeHtml(q.question)); // Decodifico eventuali caratteri speciali con la funzione decodeHTML

            // Creo la lista delle opzioni
            List<String> opzioni = new ArrayList<>(q.incorrect_answers); // Aggiungo le risposte errare
            opzioni.add(q.correct_answer); // Aggiungo la risposta corretta tra le opzioni
            Collections.shuffle(opzioni); // Mescolo le opzioni con la funzione shuffle

            // Mostro le opzioni
            for (int j = 0; j < opzioni.size(); j++) {
                System.out.println((char)('A' + j) + ") " + decodeHtml(opzioni.get(j)));
            }

            // Mostro le azioni che può fare il giocatore
            System.out.print("\nRisposta (A/B/C/D), H = aiuti, R = ritirati: ");
            String scelta = input.nextLine().trim().toUpperCase(); // leggo, rimuovo eventuali spazi e trasformo in maiuscolo la lettera

            // Se il giocatore si ritira, esco dal ciclo
            if (scelta.equals("R")) {
                System.out.println("Hai deciso di ritirarti!");
                break;
            }

            // Se sceglie di farsi aiutare, mostro le opzioni
            if (scelta.equals("H")) {
                System.out.println("1) 50/50");
                System.out.println("2) Aiuto del pubblico");
                System.out.print("Scegli aiuto: ");
                String aiuto = input.nextLine().trim();

                // Se sceglie 50/50 (che non è ancora stato usato)
                if (aiuto.equals("1") && !usato5050) {
                    usato5050 = true;

                    // Scelgo una risposta sbagliata casuale da mantenere
                    List<String> sbagliate = new ArrayList<>(q.incorrect_answers);
                    Collections.shuffle(sbagliate);
                    String sbagliataDaTenere = sbagliate.get(0);

                    System.out.println("\nRimangono due opzioni:");

                    // Mostro solo la corretta e quella sbagliata scelta, con le loro lettere originali
                    for (int j = 0; j < opzioni.size(); j++) {
                        String risposta = opzioni.get(j);
                        if (risposta.equals(q.correct_answer) || risposta.equals(sbagliataDaTenere)) {
                            char lettera = (char) ('A' + j);
                            System.out.println(lettera + ") " + decodeHtml(risposta));
                        }
                    }
                } else if (aiuto.equals("2") && !usatoPubblico) { // Se sceglie l'aiuto del pubblico (che non è ancora stato usato)
                    usatoPubblico = true;
                    System.out.println("Il pubblico suggerisce che la risposta corretta è: " + decodeHtml(q.correct_answer));
                } else {
                    System.out.println("Aiuto già usato o non valido!");
                }

                i--; // Ripeto la domanda se l'utente scrive qualcosa che non conta come tentativo
                continue;
            }

            // Trasformo la lettera in un indice
            int indiceScelto = scelta.charAt(0) - 'A'; // Prendo il primo carattere della stringa e lo sottraggo ad A (sottrazione tra ASCII, A = 65, B = 66, C = 67, D = 68)
            if (indiceScelto < 0 || indiceScelto >= opzioni.size()) {
                System.out.println("Scelta non valida!"); // Se scrive qualcosa che non rientra tra le scelte, ripeto la domanda
                i--; // ripeti la stessa domanda
                continue;
            }

            String risposta = opzioni.get(indiceScelto); // Ricavo la risposta che il giocatore sceglie

            // Verifico se la risposta è corretta
            if (risposta.equals(q.correct_answer)) {
                System.out.println(" Risposta Corretta!");
                risposteCorrette++;
            } else {
                System.out.println("Risposta " +  "Sbagliata! La risposta giusta era: " + decodeHtml(q.correct_answer));
                break;
            }
        }

        // Fine gioco
        System.out.println("\nHai risposto correttamente a " + risposteCorrette + " domande.");

        // Salvo i dati
        PlayerStatistics stats = new PlayerStatistics(nomeGiocatore, risposteCorrette, usato5050, usatoPubblico);
        salvaDati(stats); // Funzione per salvare i dati nel file
    }

    // Funzione per decodificare i caratteri speciali tipo &quot;
    public static String decodeHtml(String testo) {
        return testo.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    // Funzione per salvare i dati nel file JSON (tutti i giocatori insieme)
    public static void salvaDati(PlayerStatistics stats) {
        Gson gson = new Gson();
        List<PlayerStatistics> lista = new ArrayList<>();

        try (FileReader reader = new FileReader("player_stats.json")) {
            lista = gson.fromJson(reader, new TypeToken<List<PlayerStatistics>>(){}.getType());
            if (lista == null) lista = new ArrayList<>();
        } catch (IOException e) {
            // Se il file non esiste, lo creo
        }

        lista.add(stats);

        try (FileWriter writer = new FileWriter("player_stats.json")) {
            gson.toJson(lista, writer);
            System.out.println("Dati salvati in player_stats.json");
        } catch (IOException e) {
            System.out.println("Errore nel salvataggio: " + e.getMessage());
        }
    }
}