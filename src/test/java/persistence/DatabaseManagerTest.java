package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Pruebas unitarias para la clase DatabaseManager
 * 
 * @author Juan040504
 * @version 1.0
 */
class DatabaseManagerTest {
    
    private DatabaseManager databaseManager;
    
    @BeforeEach
    void setUp() {
        databaseManager = DatabaseManager.getInstance();
    }
    
    @Test
    @DisplayName("Test: Singleton pattern")
    void testSingletonPattern() {
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("Test: Conexión a la base de datos")
    void testConexionBaseDatos() {
        try (Connection connection = databaseManager.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        } catch (SQLException e) {
            fail("No se pudo establecer conexión con la base de datos: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Disponibilidad de la base de datos")
    void testDisponibilidadBaseDatos() {
        boolean disponible = databaseManager.isDatabaseAvailable();
        assertTrue(disponible);
    }
    
    @Test
    @DisplayName("Test: Múltiples conexiones simultáneas")
    void testMultiplesConexiones() {
        try (Connection conn1 = databaseManager.getConnection();
             Connection conn2 = databaseManager.getConnection()) {
            
            assertNotNull(conn1);
            assertNotNull(conn2);
            assertFalse(conn1.isClosed());
            assertFalse(conn2.isClosed());
            
        } catch (SQLException e) {
            fail("Error al manejar múltiples conexiones: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Cierre automático de conexiones")
    void testCierreAutomaticoConexiones() {
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        } catch (SQLException e) {
            fail("Error al obtener conexión: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    assertTrue(connection.isClosed());
                } catch (SQLException e) {
                    fail("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    @Test
    @DisplayName("Test: Inicialización de tablas")
    void testInicializacionTablas() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='slots_guardado'");
            assertTrue(rs.next());
            
            rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='celdas_slots'");
            assertTrue(rs.next());
            
        } catch (SQLException e) {
            fail("Error al verificar la inicialización de tablas: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Inicialización de slots por defecto")
    void testInicializacionSlotsPorDefecto() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM slots_guardado");
            
            if (rs.next()) {
                int count = rs.getInt("count");
                assertEquals(3, count);
            } else {
                fail("No se pudieron contar los slots");
            }
            
        } catch (SQLException e) {
            fail("Error al verificar la inicialización de slots: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Estructura de tabla slots_guardado")
    void testEstructuraTablaSlots() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery("PRAGMA table_info(slots_guardado)");
            
            boolean tieneSlotId = false;
            boolean tieneNombrePartida = false;
            boolean tieneNombreJugador = false;
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                switch (columnName) {
                    case "slot_id":
                        tieneSlotId = true;
                        break;
                    case "nombre_partida":
                        tieneNombrePartida = true;
                        break;
                    case "nombre_jugador":
                        tieneNombreJugador = true;
                        break;
                }
            }
            
            assertTrue(tieneSlotId, "La tabla debe tener la columna slot_id");
            assertTrue(tieneNombrePartida, "La tabla debe tener la columna nombre_partida");
            assertTrue(tieneNombreJugador, "La tabla debe tener la columna nombre_jugador");
            
        } catch (SQLException e) {
            fail("Error al verificar la estructura de la tabla: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Estructura de tabla celdas_slots")
    void testEstructuraTablaCeldas() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery("PRAGMA table_info(celdas_slots)");
            
            boolean tieneSlotId = false;
            boolean tieneFila = false;
            boolean tieneColumna = false;
            boolean tieneEsMina = false;
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                switch (columnName) {
                    case "slot_id":
                        tieneSlotId = true;
                        break;
                    case "fila":
                        tieneFila = true;
                        break;
                    case "columna":
                        tieneColumna = true;
                        break;
                    case "es_mina":
                        tieneEsMina = true;
                        break;
                }
            }
            
            assertTrue(tieneSlotId, "La tabla debe tener la columna slot_id");
            assertTrue(tieneFila, "La tabla debe tener la columna fila");
            assertTrue(tieneColumna, "La tabla debe tener la columna columna");
            assertTrue(tieneEsMina, "La tabla debe tener la columna es_mina");
            
        } catch (SQLException e) {
            fail("Error al verificar la estructura de la tabla: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Restricción de slot_id")
    void testRestriccionSlotId() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            
            try {
                stmt.execute("INSERT INTO slots_guardado (slot_id, nombre_partida, nombre_jugador) VALUES (4, 'Test', 'Test')");
                fail("Debería haber fallado al insertar slot_id = 4");
            } catch (SQLException e) {
                assertTrue(e.getMessage().contains("CHECK") || e.getMessage().contains("constraint"));
            }
            
        } catch (SQLException e) {
            fail("Error al probar la restricción: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test: Índices creados")
    void testIndicesCreados() {
        try (Connection connection = databaseManager.getConnection()) {
            var stmt = connection.createStatement();
            var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='index'");
            
            boolean tieneIndiceSlots = false;
            boolean tieneIndiceCeldas = false;
            
            while (rs.next()) {
                String indexName = rs.getString("name");
                if (indexName.contains("slots")) {
                    tieneIndiceSlots = true;
                }
                if (indexName.contains("celdas")) {
                    tieneIndiceCeldas = true;
                }
            }
            
            assertTrue(tieneIndiceSlots, "Debe existir un índice para la tabla slots");
            assertTrue(tieneIndiceCeldas, "Debe existir un índice para la tabla celdas");
            
        } catch (SQLException e) {
            fail("Error al verificar los índices: " + e.getMessage());
        }
    }
}
