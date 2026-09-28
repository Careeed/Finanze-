# Entrate & Spese — prima APK

Progetto Android generato a partire da `Income and expenses 2.0.xlsx`.

## Cosa contiene
- dati storici importati dal workbook come asset locale;
- schermate per Entrate, Spese Casa, Spese Simo e Spese Paola;
- inserimento rapido di nuove entrate e spese;
- Resoconto Simo e Resoconto Paola protetti con password separate;
- impostazione/cambio password direttamente dall'app;
- workflow GitHub Actions per creare automaticamente `app-debug.apk`.

## Build automatica
1. Carica questa cartella in un repository GitHub.
2. Apri **Actions → Build APK → Run workflow**.
3. Al termine scarica l'artifact **Entrate-Spese-debug**.

La versione attuale è una prima build funzionale/offline. La sincronizzazione realtime tra due telefoni e la sincronizzazione bidirezionale con Google Sheets richiedono il collegamento a un backend cloud (da configurare nella fase successiva).
