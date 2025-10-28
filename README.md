# Richieste API e JSON: Chi vuol essere Maturato?
## Consegna
Creare un gioco in console in stile “Chi vuol essere milionario?”, utilizzando l’API Open Trivia DB per ottenere le domande.
L’utente potrà diventare maturato solo dopo aver affrontato 10 domande a risposta multipla, suddivise in: 5 facili, 3 medie, 2 difficili
Durante il gioco, il giocatore potrà usufruire di due aiuti: Guarda il bigliettino (50/50) elimina due risposte sbagliate; Suggerimento dei compagni (aiuto del pubblico) mostra le percentuali delle risposte suggerite dal “pubblico”
Al termine della partita, i dati di gioco verranno salvati in un file JSON, contenente: nome del giocatore, numero di domande risposte correttamente, se l’aiuto 50/50 è stato utilizzato, se l’aiuto del pubblico è stato utilizzato.

## Logica del gioco
Menu principale
1. Visualizza il titolo: Chi vuol essere Maturato?
2. Chiedi il nome del giocatore
   Preparazione delle domande
3. Il gioco prevede 10 domande a risposta multipla, con difficoltà crescente:
   a. 5 easy
   b. 3 medium
   c. 2 hard
   Suggerimento: fai 3 fetch separate dall’API, cambiando difficulty ogni volta.
   Le domande devono essere in ordine crescente di difficoltà.
   Loop di gioco - per ogni domanda:
4. Mostra domanda e opzioni
   Problema chiave: le opzioni devono essere presentate in ordine casuale ad ogni
   domanda.
   Suggerimento: creare una lista di oggetti AnswerOption (testo + flag corretto) e
   mescolare con Collections.shuffle() prima di mostrarle
5. Permetti input:
   a. Lettera (A/B/C/D) → risposta
   b. H → usare aiuto (Guarda il bigliettino o suggerimento dei compagni)
   c. R → ritirarsi
6. Controlla se la risposta è corretta:
   a. Se sì → avanza al livello successivo
   b. Se no → calcola safe point inserendolo nel JSON e termina il gioco