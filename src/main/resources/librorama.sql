-- Script di inizializzazione del database Librorama
-- Esegui questo script per creare e popolare il database con dati di esempio

-- 1. Elimina il database se esiste già
DROP DATABASE IF EXISTS librorama;

-- 2. Crea il database
CREATE DATABASE librorama;
USE librorama;

-- 3. Crea le tabelle
CREATE TABLE Utente (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_cifrata VARCHAR(40) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    ruolo ENUM('admin', 'registrato') DEFAULT 'registrato',
    telefono VARCHAR(20),
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Indirizzo (
    id_indirizzo INT AUTO_INCREMENT PRIMARY KEY,
    id_utente INT NOT NULL,
    via VARCHAR(100) NOT NULL,
    citta VARCHAR(50) NOT NULL,
    cap VARCHAR(10) NOT NULL,
    provincia VARCHAR(50) NOT NULL,
    paese VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_utente) REFERENCES Utente(id_utente) ON DELETE CASCADE
);

CREATE TABLE Libro (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL,
    autore VARCHAR(255) NOT NULL,
    prezzo DECIMAL(8,2) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    descrizione TEXT,
    disponibilita INT DEFAULT 0,
    copertina VARCHAR(255),
    data_inserimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vendite_totalis INT DEFAULT 0,
    CONSTRAINT chk_prezzo CHECK (prezzo >= 0),
    CONSTRAINT chk_disponibilita CHECK (disponibilita >= 0)
);

CREATE TABLE Categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome_categoria VARCHAR(50) NOT NULL UNIQUE,
    icona VARCHAR(50)
);

CREATE TABLE LibroCategoria (
    id_libro INT,
    id_categoria INT,
    PRIMARY KEY (id_libro, id_categoria),
    FOREIGN KEY (id_libro) REFERENCES Libro(id_libro) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES Categoria(id_categoria) ON DELETE CASCADE
);

CREATE TABLE Ordine (
    id_ordine INT AUTO_INCREMENT PRIMARY KEY,
    id_utente INT NOT NULL,
    id_indirizzo INT,
    data_ordine DATETIME DEFAULT CURRENT_TIMESTAMP,
    stato ENUM('in_elaborazione', 'spedito', 'consegnato', 'annullato') DEFAULT 'in_elaborazione',
    totale DECIMAL(10,2),
    FOREIGN KEY (id_utente) REFERENCES Utente(id_utente),
    FOREIGN KEY (id_indirizzo) REFERENCES Indirizzo(id_indirizzo),
    CONSTRAINT chk_totale CHECK (totale >= 0)
);

CREATE TABLE Contiene (
    id_ordine INT,
    id_libro INT,
    quantita INT NOT NULL,
    prezzo_unitario DECIMAL(8,2) NOT NULL,
    PRIMARY KEY (id_ordine, id_libro),
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_libro) REFERENCES Libro(id_libro),
    CONSTRAINT chk_quantita CHECK (quantita > 0),
    CONSTRAINT chk_prezzo_unitario CHECK (prezzo_unitario >= 0)
);

CREATE TABLE Valutazione (
    id_valutazione INT AUTO_INCREMENT PRIMARY KEY,
    id_utente INT NOT NULL,
    id_libro INT NOT NULL,
    voto INT,
    commento TEXT,
    data_valutazione DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utente) REFERENCES Utente(id_utente) ON DELETE CASCADE,
    FOREIGN KEY (id_libro) REFERENCES Libro(id_libro) ON DELETE CASCADE,
    CONSTRAINT chk_voto CHECK (voto >= 1 AND voto <= 5)
);

-- 4. Inserisci dati di esempio
-- Utenti
INSERT INTO Utente (email, password_cifrata, nome, cognome, ruolo, telefono) VALUES
('admin@librorama.it', SHA1('admin123'), 'Admin', 'Sistema', 'admin', '1234567890'),
('mario.rossi@email.com', SHA1('password123'), 'Mario', 'Rossi', 'registrato', '3456789012'),
('laura.bianchi@email.com', SHA1('password123'), 'Laura', 'Bianchi', 'registrato', '4567890123');

-- Indirizzi
INSERT INTO Indirizzo (id_utente, via, citta, cap, provincia, paese) VALUES
(2, 'Via Roma 123', 'Milano', '20121', 'MI', 'Italia'),
(3, 'Corso Italia 45', 'Torino', '10121', 'TO', 'Italia');

-- Categorie
INSERT INTO Categoria (nome_categoria, icona) VALUES
('Narrativa', 'fa-book'),
('Scolastica', 'fa-graduation-cap'),
('Scientifici', 'fa-flask'),
('Bambini', 'fa-child'),
('Fantasy', 'fa-dragon'),
('Gialli', 'fa-search'),
('Romanzi', 'fa-book-open'),
('Biografie', 'fa-user-tie');

-- Libri
INSERT INTO Libro (titolo, autore, prezzo, isbn, descrizione, disponibilita, copertina, vendite_totalis) VALUES
('Il Signore degli Anelli', 'J.R.R. Tolkien', 25.90, '9788845205899', 'Un classico della letteratura fantasy', 50, 'signore_anelli.jpg', 120),
('Fisica 1', 'James S. Walker', 45.50, '9788808061548', 'Manuale di fisica per l\'università', 30, 'fisica1.jpg', 85),
('Il Piccolo Principe', 'Antoine de Saint-Exupéry', 12.90, '9788845205905', 'Una favola per bambini e adulti', 75, 'piccolo_principe.jpg', 200),
('1984', 'George Orwell', 14.90, '9788804668237', 'Un classico della letteratura distopica', 40, '1984.jpg', 150),
('La ragazza del treno', 'Paula Hawkins', 9.90, '9788804668244', 'Un thriller psicologico avvincente', 25, 'ragazza_treno.jpg', 95);

-- Associazione libri-categorie
INSERT INTO LibroCategoria (id_libro, id_categoria) VALUES
(1, 1), (1, 5),  -- Il Signore degli Anelli: Narrativa, Fantasy
(2, 2), (2, 3),  -- Fisica 1: Scolastica, Scientifici
(3, 1), (3, 4),  -- Il Piccolo Principe: Narrativa, Bambini
(4, 1), (4, 6),  -- 1984: Narrativa, Gialli
(5, 1), (5, 6);  -- La ragazza del treno: Narrativa, Gialli

-- Ordini di esempio
INSERT INTO Ordine (id_utente, id_indirizzo, data_ordine, stato, totale) VALUES
(2, 1, '2025-06-15 14:30:00', 'consegnato', 70.80),
(3, 2, '2025-07-01 10:15:00', 'spedito', 60.40);

-- Dettagli ordini
INSERT INTO Contiene (id_ordine, id_libro, quantita, prezzo_unitario) VALUES
(1, 1, 1, 25.90),
(1, 3, 2, 12.90),
(2, 2, 1, 45.50),
(2, 5, 1, 9.90);

-- Recensioni
INSERT INTO Valutazione (id_utente, id_libro, voto, commento) VALUES
(2, 1, 5, 'Assolutamente fantastico! Un capolavoro senza tempo.'),
(3, 2, 4, 'Molto utile per lo studio, spiegazioni chiare.'),
(2, 3, 5, 'Un libro che tutti dovrebbero leggere almeno una volta nella vita.');

-- 5. Crea le viste utili
CREATE VIEW VistaLibriConCategorie AS
SELECT 
    l.*,
    GROUP_CONCAT(DISTINCT c.nome_categoria SEPARATOR ', ') AS categorie
FROM 
    Libro l
LEFT JOIN 
    LibroCategoria lc ON l.id_libro = lc.id_libro
LEFT JOIN 
    Categoria c ON lc.id_categoria = c.id_categoria
GROUP BY 
    l.id_libro;

-- 6. Crea gli indici per migliorare le prestazioni
CREATE INDEX idx_libro_titolo ON Libro(titolo);
CREATE INDEX idx_libro_autore ON Libro(autore);
CREATE INDEX idx_libro_prezzo ON Libro(prezzo);
CREATE INDEX idx_ordine_utente ON Ordine(id_utente);
CREATE INDEX idx_ordine_data ON Ordine(data_ordine);

-- 7. Crea un utente con permessi limitati per l'applicazione
CREATE USER IF NOT EXISTS 'librorama_app'@'localhost' IDENTIFIED BY 'password_sicura';
GRANT SELECT, INSERT, UPDATE, DELETE ON librorama.* TO 'librorama_app'@'localhost';

-- 8. Messaggio di completamento
SELECT 'Database inizializzato con successo!' AS Message;

