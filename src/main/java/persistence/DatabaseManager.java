package persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:buscaminas.db";
    private static final String DB_DRIVER = "org.sqlite.JDBC";
    

    private DatabaseManager() {
        initializeDatabase();
    }
    

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    

    private void initializeDatabase() {
        try {
            // Cargar el driver de SQLite
            Class.forName(DB_DRIVER);
            
            // Crear la base de datos y las tablas
            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement statement = connection.createStatement()) {
                
                // Crear tabla de slots de guardado
                String createSlotsTable = """
                    CREATE TABLE IF NOT EXISTS slots_guardado (
                        slot_id INTEGER PRIMARY KEY CHECK (slot_id IN (1, 2, 3)),
                        nombre_partida TEXT,
                        nombre_jugador TEXT,
                        fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                        fecha_ultima_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                        cantidad_minas INTEGER NOT NULL DEFAULT 60,
                        celdas_reveladas INTEGER NOT NULL DEFAULT 0,
                        minas_marcadas INTEGER NOT NULL DEFAULT 0,
                        juego_terminado BOOLEAN NOT NULL DEFAULT 0,
                        juego_ganado BOOLEAN NOT NULL DEFAULT 0,
                        tiempo_juego INTEGER DEFAULT 0,
                        estado_partida TEXT DEFAULT 'en_curso'
                    )
                    """;
                
                // Crear tabla de celdas para los slots
                String createCeldasSlotsTable = """
                    CREATE TABLE IF NOT EXISTS celdas_slots (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        slot_id INTEGER NOT NULL,
                        fila INTEGER NOT NULL,
                        columna INTEGER NOT NULL,
                        es_mina BOOLEAN NOT NULL DEFAULT 0,
                        esta_revelada BOOLEAN NOT NULL DEFAULT 0,
                        esta_marcada BOOLEAN NOT NULL DEFAULT 0,
                        minas_adyacentes INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (slot_id) REFERENCES slots_guardado(slot_id) ON DELETE CASCADE,
                        UNIQUE(slot_id, fila, columna)
                    )
                    """;
                
                // Crear índices para mejorar el rendimiento
                String createIndexes = """
                    CREATE INDEX IF NOT EXISTS idx_slots_fecha ON slots_guardado(fecha_ultima_modificacion);
                    CREATE INDEX IF NOT EXISTS idx_slots_estado ON slots_guardado(estado_partida);
                    CREATE INDEX IF NOT EXISTS idx_celdas_slots ON celdas_slots(slot_id);
                    """;
                
                // Ejecutar las sentencias SQL
                statement.execute(createSlotsTable);
                statement.execute(createCeldasSlotsTable);
                statement.execute(createIndexes);
                
                // Inicializar los 3 slots si no existen
                String insertSlots = """
                    INSERT OR IGNORE INTO slots_guardado (slot_id, nombre_partida, nombre_jugador) 
                    VALUES (1, 'Slot 1', 'Vacío'), (2, 'Slot 2', 'Vacío'), (3, 'Slot 3', 'Vacío')
                    """;
                statement.execute(insertSlots);
                
                logger.info("Base de datos SQLite inicializada correctamente con sistema de slots");
                
            } catch (SQLException e) {
                logger.error("Error al crear las tablas: {}", e.getMessage());
                throw new RuntimeException("No se pudo inicializar la base de datos", e);
            }
            
        } catch (ClassNotFoundException e) {
            logger.error("Driver de SQLite no encontrado: {}", e.getMessage());
            throw new RuntimeException("Driver de SQLite no disponible", e);
        }
    }
    

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    

    public boolean isDatabaseAvailable() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            logger.error("Error al verificar la disponibilidad de la base de datos: {}", e.getMessage());
            return false;
        }
    }
} 