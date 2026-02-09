# Readify 📚

**Readify** è una piattaforma web per l’acquisto di libri online che consente agli utenti di navigare il catalogo, gestire un carrello, effettuare ordini e amministrare i contenuti del sistema.  
Il progetto è stato sviluppato nell’ambito del corso di **Ingegneria del Software** presso l’**Università degli Studi di Salerno**.

## Membri Partecipanti
- Mario Branca
- Paolo Visconti
- Simone Sammartano
- Gabriele De Luca

## Funzionalità principali

- Consultazione del **catalogo libri** con ricerca e filtri  
- **Gestione del carrello** e checkout  
- Registrazione e autenticazione utenti  
- Gestione ordini e indirizzi di spedizione  
- Sistema di **recensioni**  
- Area **amministratore** per la gestione del catalogo  

## Tecnologie utilizzate

- **Backend:** Java, Servlet, JSP  
- **Database:** MySQL  
- **Architettura:** Three-tier logica (Presentation, Application, Data)  

## Architettura del sistema

Il sistema adotta un’architettura **three-tier logica**:

- **Presentation Layer:** Servlet, Filter e JSP (in `WEB-INF`)  
- **Application Layer:** Service per la gestione dei casi d’uso  
- **Data Layer:** DAO e Model per l’accesso ai dati persistenti  

Dipendenze unidirezionali: `Controller → Service → DAO → Database`

## Documentazione del progetto

La documentazione completa è disponibile nella cartella dedicata e include:

- Statement of Work (SOW)  
- Requirement Analysis Document (RAD)  
- System Design Document (SDD)  
- Object Design Document (ODD)  
- Test Documentation:
  - Test Plan (TP)
  - Test Case Scenarios (TCS)

## Installazione ed esecuzione

### Prerequisiti
- IntelliJ IDEA  
- Java Development Kit (JDK)  
- Apache Tomcat  
- MySQL  

### Avvio del progetto
1. Clonare il repository:
   ```bash
   git clone <repository-url
2. Importare il progetto in **IntelliJ IDEA**:
   - `File > Open` e selezionare la cartella del progetto clonata
   - attendere l’indicizzazione e la risoluzione delle dipendenze

3. Configurare il **database MySQL**:
   - creare uno schema (es. `readify`)
   - eseguire lo script `readify.sql` presente in `src/main/resources/`

4. Configurare **Apache Tomcat** in IntelliJ:
   - `Run > Edit Configurations... > + > Tomcat Server (Local)`
   - selezionare l’installazione di Tomcat
   - nella sezione **Deployment** aggiungere l’artefatto dell’applicazione (WAR/Exploded)

5. Avviare il server e accedere all’applicazione:
   - avviare la configurazione Tomcat (`Run`)
   - aprire il browser all’URL locale mostrato da IntelliJ (tipicamente `http://localhost:8080/`)

## Sviluppo

Il progetto segue una chiara separazione dei ruoli:
- i **Controller** gestiscono le richieste HTTP  
- i **Service** implementano la logica applicativa  
- i **DAO** gestiscono l’accesso al database  

Le JSP sono collocate in `WEB-INF` e non sono direttamente accessibili.

## Sviluppi futuri

- Miglioramento del sistema di recensioni  
- Gestione avanzata degli ordini per l’amministratore  
- Estensione delle funzionalità di ricerca e filtraggio  

## Contributi

Il progetto è realizzato a **scopo didattico**.  
Eventuali suggerimenti o miglioramenti possono essere proposti tramite fork e pull request.
   
