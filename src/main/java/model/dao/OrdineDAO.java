package model.dao;

import model.DettaglioOrdine;
import model.Ordine;
import model.StatoOrdine;
import utils.DBManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrdineDAO {
    private static final Logger logger = Logger.getLogger(OrdineDAO.class.getName());

    public boolean salvaOrdine(Ordine ordine) {
        if (ordine == null) {
            return false;
        }

        if (ordine.getIdOrdine() > 0) {
            return aggiornaOrdine(ordine);
        } else {
            return inserisciNuovoOrdine(ordine);
        }
    }

    private boolean inserisciNuovoOrdine(Ordine ordine) {
        String queryOrdine = "INSERT INTO Ordine (id_utente, id_indirizzo, stato, totale) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            // Inserisco ordine
            try (PreparedStatement stmtOrdine = conn.prepareStatement(queryOrdine, Statement.RETURN_GENERATED_KEYS)) {
                // impost parametri
                stmtOrdine.setInt(1, ordine.getIdUtente());

                // gestisco l'indirizzo (può essere null)
                if (ordine.getIdIndirizzo() > 0) {
                    stmtOrdine.setInt(2, ordine.getIdIndirizzo());
                } else {
                    stmtOrdine.setNull(2, Types.INTEGER);
                }

                // Impostolo stato
                String stato = ordine.getStato() != null ?
                        ordine.getStato().name().toLowerCase() :
                        StatoOrdine.IN_ELABORAZIONE.name().toLowerCase();
                stmtOrdine.setString(3, stato);

                // Imposto il totale (non può essere null per il vincolo CHECK)
                stmtOrdine.setBigDecimal(4, ordine.getTotale() != null ?
                        ordine.getTotale() : BigDecimal.ZERO);

                int righeInserite = stmtOrdine.executeUpdate();
                if (righeInserite > 0) {
                    ResultSet rs = stmtOrdine.getGeneratedKeys();
                    if (rs.next()) {
                        int idOrdine = rs.getInt(1);
                        ordine.setIdOrdine(idOrdine);

                        // Inserisco i dettagli dell'ordine se presenti
                        if (ordine.getDettagli() != null && !ordine.getDettagli().isEmpty()) {
                            if (!inserisciDettagliOrdine(conn, ordine)) {
                                conn.rollback();
                                return false;
                            }
                        }

                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'inserimento dell'ordine", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Errore durante il rollback", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Errore durante la chiusura della connessione", e);
                }
            }
        }
    }

    private boolean aggiornaOrdine(Ordine ordine) {
        String query = "UPDATE Ordine SET id_utente = ?, id_indirizzo = ?, " +
                "stato = ?, totale = ? WHERE id_ordine = ?";

        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, ordine.getIdUtente());

                // Gestisco l'indirizzo (può essere null)
                if (ordine.getIdIndirizzo() > 0) {
                    stmt.setInt(2, ordine.getIdIndirizzo());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }

                // Impostolo stato
                String stato = ordine.getStato() != null ?
                        ordine.getStato().name().toLowerCase() :
                        StatoOrdine.IN_ELABORAZIONE.name().toLowerCase();
                stmt.setString(3, stato);

                // Imposto il totale (non può essere null per il vincolo CHECK)
                stmt.setBigDecimal(4, ordine.getTotale() != null ?
                        ordine.getTotale() : BigDecimal.ZERO);

                stmt.setInt(5, ordine.getIdOrdine());

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Aggiorno dettagli
                    if (ordine.getDettagli() != null && !ordine.getDettagli().isEmpty()) {
                        // Prima cancello i vecchi dettagli
                        String deleteQuery = "DELETE FROM Contiene WHERE id_ordine = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, ordine.getIdOrdine());
                            deleteStmt.executeUpdate();
                        }

                        // Poi inserisco i nuovi
                        if (!inserisciDettagliOrdine(conn, ordine)) {
                            conn.rollback();
                            return false;
                        }
                    }

                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'aggiornamento dell'ordine", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Errore durante il rollback", ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Errore durante la chiusura della connessione", e);
                }
            }
        }
    }

    private boolean inserisciDettagliOrdine(Connection conn, Ordine ordine) throws SQLException {
        String query = "INSERT INTO Contiene (id_ordine, id_libro, quantita, prezzo_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (DettaglioOrdine dettaglio : ordine.getDettagli()) {
                stmt.setInt(1, ordine.getIdOrdine());
                stmt.setInt(2, dettaglio.getIdLibro());
                stmt.setInt(3, dettaglio.getQuantita());
                stmt.setBigDecimal(4, dettaglio.getPrezzoUnitario());
                stmt.addBatch();

                // Aggiorno disponibilità dei libri
                if (!aggiornaDisponibilitaLibro(conn, dettaglio.getIdLibro(), -dettaglio.getQuantita())) {
                    return false;
                }
            }
            stmt.executeBatch();
            return true;
        }
    }


    public List<Ordine> trovaPerIdUtente(int idUtente) {
        List<Ordine> ordini = new ArrayList<>();
        String sql = "SELECT o.*, u.nome, u.cognome, u.email, u.id_utente, " +
                "i.id_indirizzo, i.via, i.citta, i.cap, i.provincia, i.paese " +
                "FROM Ordine o " +
                "JOIN Utente u ON o.id_utente = u.id_utente " +
                "LEFT JOIN Indirizzo i ON o.id_indirizzo = i.id_indirizzo " +
                "WHERE o.id_utente = ? " +
                "ORDER BY o.data_ordine DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ordine ordine = mappaOrdineDaResultSet(rs);
                caricaDettagliOrdine(ordine, conn);
                ordini.add(ordine);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero degli ordini dell'utente con ID: " + idUtente, e);
        }

        return ordini;
    }


    private Ordine mappaOrdineDaResultSet(ResultSet rs) throws SQLException {
        Ordine ordine = new Ordine();

        ordine.setIdOrdine(rs.getInt("id_ordine"));

        Timestamp timestamp = rs.getTimestamp("data_ordine");
        ordine.setDataOrdine(timestamp != null ? new Date(timestamp.getTime()) : null);

        try {
            String stato = rs.getString("stato").toUpperCase();
            ordine.setStato(StatoOrdine.valueOf(stato));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Stato ordine non valido, impostato a IN_ELABORAZIONE", e);
            ordine.setStato(StatoOrdine.IN_ELABORAZIONE);
        }

        // Imposto totale
        ordine.setTotale(rs.getBigDecimal("totale"));

        // Dettagli utente (se presenti neldb)
        try {
            if (hasColumn(rs, "nome") && hasColumn(rs, "cognome") && hasColumn(rs, "email")) {
                ordine.setIdUtente(rs.getInt("id_utente"));
            }
            if (hasColumn(rs, "id_indirizzo") && !rs.wasNull()) {
                ordine.setIdIndirizzo(rs.getInt("id_indirizzo"));
            }
        } catch (SQLException e) {
            logger.log(Level.FINE, "Errore durante il mapping dei dettagli aggiuntivi", e);
        }

        return ordine;
    }

    private DettaglioOrdine mappaRisultatoADettaglio(ResultSet rs) throws SQLException {
        DettaglioOrdine dettaglio = new DettaglioOrdine();
        // non esiste un id univoco per la riga nella tabella Contiene, creo combinazione di id_ordine e id_libro
        dettaglio.setId(rs.getInt("id_ordine") * 1000 + rs.getInt("id_libro")); //creoID univoco
        dettaglio.setIdOrdine(rs.getInt("id_ordine"));
        dettaglio.setIdLibro(rs.getInt("id_libro"));
        dettaglio.setQuantita(rs.getInt("quantita"));
        dettaglio.setPrezzoUnitario(rs.getBigDecimal("prezzo_unitario"));

        // Aggiungo  dettagli aggiuntivi se disponibili
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder columns = new StringBuilder("Colonne disponibili nel ResultSet: ");
            for (int i = 1; i <= columnCount; i++) {
                columns.append(metaData.getColumnName(i)).append(", ");
            }
            logger.log(Level.INFO, columns.toString());

            if (hasColumn(rs, "titolo")) {
                dettaglio.setTitoloLibro(rs.getString("titolo"));
            }
            if (hasColumn(rs, "autore")) {
                dettaglio.setAutoreLibro(rs.getString("autore"));
            }
            if (hasColumn(rs, "isbn")) {
                dettaglio.setIsbnLibro(rs.getString("isbn"));
            }

            // immagine copertina
            String copertina = null;
            if (hasColumn(rs, "immagine_copertina")) {
                copertina = rs.getString("immagine_copertina");
                if (copertina != null && !copertina.trim().isEmpty()) {
                    if (!copertina.startsWith("img/libri/copertine/")) {
                        copertina = "img/libri/copertine/" + copertina;
                    }
                    dettaglio.setImmagineCopertina(copertina);
                    logger.log(Level.INFO, "Impostata immagine copertina: " + copertina);
                }
            }

            logger.log(Level.INFO, "Dettaglio ordine mappato - ID Libro: {0}, Titolo: {1}, Copertina: {2}",
                    new Object[]{dettaglio.getIdLibro(), dettaglio.getTitoloLibro(), dettaglio.getImmagineCopertina()});

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Errore durante il mapping dei dettagli aggiuntivi", e);
        }

        return dettaglio;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(meta.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    private void caricaDettagliOrdine(Ordine ordine, Connection conn) throws SQLException {
        logger.log(Level.INFO, "Caricamento dettagli per ordine ID: " + ordine.getIdOrdine());

        String sql = "SELECT c.id_ordine, c.id_libro, c.quantita, c.prezzo_unitario, " +
                "l.titolo, l.autore, l.isbn, l.copertina as immagine_copertina " +
                "FROM Contiene c " +
                "JOIN Libro l ON c.id_libro = l.id_libro " +
                "WHERE c.id_ordine = ?";

        logger.log(Level.INFO, "Esecuzione query: " + sql);

        if (ordine.getIdIndirizzo() <= 0) {
            String indirizzoSql = "SELECT id_indirizzo FROM Ordine WHERE id_ordine = ?";
            try (PreparedStatement stmt = conn.prepareStatement(indirizzoSql)) {
                stmt.setInt(1, ordine.getIdOrdine());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && !rs.wasNull()) {
                        int idIndirizzo = rs.getInt("id_indirizzo");
                        if (!rs.wasNull()) {
                            ordine.setIdIndirizzo(idIndirizzo);
                            logger.log(Level.INFO, "Indirizzo trovato per ordine ID {0}: {1}",
                                    new Object[]{ordine.getIdOrdine(), idIndirizzo});
                        }
                    }
                }
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ordine.getIdOrdine());
            try (ResultSet rs = stmt.executeQuery()) {
                List<DettaglioOrdine> dettagli = new ArrayList<>();

                while (rs.next()) {
                    DettaglioOrdine dettaglio = mappaRisultatoADettaglio(rs);
                    logger.log(Level.INFO, "Dettaglio ordine mappato - ID Libro: {0}, Titolo: {1}, Copertina: {2}, Query SQL: {3}",
                            new Object[]{dettaglio.getIdLibro(), dettaglio.getTitoloLibro(),
                                    dettaglio.getImmagineCopertina(), sql});
                    logger.log(Level.INFO, "Valori colonne raw - id_libro: {0}, titolo: {1}, copertina: {2}",
                            new Object[]{rs.getInt("id_libro"), rs.getString("titolo"), rs.getString("immagine_copertina")});
                    dettagli.add(dettaglio);
                }

                ordine.setDettagli(dettagli);
                logger.log(Level.INFO, "Caricati {0} dettagli per l'ordine ID {1}",
                        new Object[]{dettagli.size(), ordine.getIdOrdine()});
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento dei dettagli dell'ordine ID " +
                    ordine.getIdOrdine(), e);
            throw e;
        }
    }


    private boolean aggiornaDisponibilitaLibro(Connection conn, int idLibro, int quantitaDaAggiornare) throws SQLException {

        String checkQuery = "SELECT disponibilita FROM Libro WHERE id_libro = ? FOR UPDATE";
        String updateQuery = "UPDATE Libro SET disponibilita = ? WHERE id_libro = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            checkStmt.setInt(1, idLibro);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                logger.log(Level.WARNING, "Libro con ID " + idLibro + " non trovato");
                return false;
            }

            int quantitaAttuale = rs.getInt("disponibilita");
            int nuovaQuantita = quantitaAttuale + quantitaDaAggiornare;

            if (nuovaQuantita < 0) {
                logger.log(Level.WARNING, "Quantità insufficiente per il libro ID: " + idLibro +
                        ". Disponibile: " + quantitaAttuale +
                        ", Richiesta: " + (-quantitaDaAggiornare));
                return false;
            }

            // Aggiorno  quantità
            updateStmt.setInt(1, nuovaQuantita);
            updateStmt.setInt(2, idLibro);

            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated == 0) {
                logger.log(Level.WARNING, "Nessun aggiornamento effettuato per il libro ID: " + idLibro);
                return false;
            }

            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'aggiornamento della disponibilità del libro ID: " + idLibro, e);
            throw e;
        }
    }
}
