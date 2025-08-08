package celda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase TableroBuscaminas
 * 
 * @author Juan040504
 * @version 1.0
 */
class TableroBuscaminasTest {
    
    private TableroBuscaminas tablero;
    
    @BeforeEach
    void setUp() {
        tablero = new TableroBuscaminas();
    }
    
    @Test
    @DisplayName("Test: Estado inicial del tablero")
    void testEstadoInicialTablero() {
        assertEquals(20, tablero.getTamano());
        assertEquals(60, tablero.getCantidadMinas());
        assertEquals(0, tablero.getCeldasReveladas());
        assertEquals(0, tablero.getMinasMarcadas());
        assertFalse(tablero.isJuegoTerminado());
        assertFalse(tablero.isJuegoGanado());
    }
    
    @Test
    @DisplayName("Test: Colocación de minas después del primer clic")
    void testColocacionMinas() {
        // Verificar que el primer clic no es una mina
        tablero.colocarMinas(0, 0);
        assertFalse(tablero.getCelda(0, 0).esMina());
        
        // Verificar que se colocaron las minas correctamente
        int minasContadas = 0;
        for (int fila = 0; fila < tablero.getTamano(); fila++) {
            for (int columna = 0; columna < tablero.getTamano(); columna++) {
                if (tablero.getCelda(fila, columna).esMina()) {
                    minasContadas++;
                }
            }
        }
        assertEquals(tablero.getCantidadMinas(), minasContadas);
    }
    
    @Test
    @DisplayName("Test: Revelado de celda segura")
    void testReveladoCeldaSegura() {
        tablero.colocarMinas(0, 0);
        
        // Buscar una celda que no sea mina
        boolean celdaRevelada = false;
        for (int fila = 0; fila < tablero.getTamano() && !celdaRevelada; fila++) {
            for (int columna = 0; columna < tablero.getTamano() && !celdaRevelada; columna++) {
                if (!tablero.getCelda(fila, columna).esMina()) {
                    celdaRevelada = tablero.revelarCelda(fila, columna);
                    break;
                }
            }
        }
        
        assertTrue(celdaRevelada);
        assertTrue(tablero.getCeldasReveladas() > 0);
    }
    
    @Test
    @DisplayName("Test: Revelado de mina (game over)")
    void testReveladoMina() {
        tablero.colocarMinas(0, 0);
        
        // Buscar una mina y revelarla
        boolean minaRevelada = false;
        for (int fila = 0; fila < tablero.getTamano() && !minaRevelada; fila++) {
            for (int columna = 0; columna < tablero.getTamano() && !minaRevelada; columna++) {
                if (tablero.getCelda(fila, columna).esMina()) {
                    minaRevelada = !tablero.revelarCelda(fila, columna);
                    break;
                }
            }
        }
        
        assertTrue(minaRevelada);
        assertTrue(tablero.isJuegoTerminado());
        assertFalse(tablero.isJuegoGanado());
    }
    
    @Test
    @DisplayName("Test: Marcado de celda")
    void testMarcadoCelda() {
        tablero.colocarMinas(0, 0);
        
        // Marcar una celda
        boolean celdaMarcada = tablero.marcarCelda(0, 0);
        assertTrue(celdaMarcada);
        assertTrue(tablero.getCelda(0, 0).estaMarcada());
        assertEquals(1, tablero.getMinasMarcadas());
        
        // Desmarcar la celda
        boolean celdaDesmarcada = tablero.marcarCelda(0, 0);
        assertTrue(celdaDesmarcada);
        assertFalse(tablero.getCelda(0, 0).estaMarcada());
        assertEquals(0, tablero.getMinasMarcadas());
    }
    
    @Test
    @DisplayName("Test: No marcar celda ya revelada")
    void testNoMarcarCeldaRevelada() {
        tablero.colocarMinas(0, 0);
        
        // Revelar una celda
        tablero.revelarCelda(0, 0);
        
        // Intentar marcar la celda revelada
        boolean marcadoFallido = tablero.marcarCelda(0, 0);
        assertFalse(marcadoFallido);
        assertFalse(tablero.getCelda(0, 0).estaMarcada());
    }
    
    @Test
    @DisplayName("Test: Algoritmo de inundación")
    void testAlgoritmoInundacion() {
        tablero.colocarMinas(0, 0);
        
        // Revelar una celda vacía (que debería activar el algoritmo de inundación)
        int celdasReveladasAntes = tablero.getCeldasReveladas();
        tablero.revelarCelda(0, 0);
        int celdasReveladasDespues = tablero.getCeldasReveladas();
        
        // El algoritmo de inundación debería revelar más de una celda
        assertTrue(celdasReveladasDespues > celdasReveladasAntes);
    }
    
    @Test
    @DisplayName("Test: Reinicio del tablero")
    void testReinicioTablero() {
        tablero.colocarMinas(0, 0);
        tablero.revelarCelda(0, 0);
        tablero.marcarCelda(1, 1);
        
        // Reiniciar el tablero
        tablero.reiniciar();
        
        // Verificar que el tablero está en estado inicial
        assertEquals(0, tablero.getCeldasReveladas());
        assertEquals(0, tablero.getMinasMarcadas());
        assertFalse(tablero.isJuegoTerminado());
        assertFalse(tablero.isJuegoGanado());
        
        // Verificar que las celdas están en estado inicial
        for (int fila = 0; fila < tablero.getTamano(); fila++) {
            for (int columna = 0; columna < tablero.getTamano(); columna++) {
                Celda celda = tablero.getCelda(fila, columna);
                assertFalse(celda.estaRevelada());
                assertFalse(celda.estaMarcada());
                assertFalse(celda.esMina());
                assertEquals(0, celda.getMinasAdyacentes());
            }
        }
    }
    
    @Test
    @DisplayName("Test: Cálculo de minas adyacentes")
    void testCalculoMinasAdyacentes() {
        tablero.colocarMinas(0, 0);
        
        // Verificar que las celdas tienen el número correcto de minas adyacentes
        boolean minasAdyacentesCalculadas = false;
        for (int fila = 0; fila < tablero.getTamano(); fila++) {
            for (int columna = 0; columna < tablero.getTamano(); columna++) {
                Celda celda = tablero.getCelda(fila, columna);
                if (!celda.esMina() && celda.getMinasAdyacentes() > 0) {
                    minasAdyacentesCalculadas = true;
                    break;
                }
            }
        }
        
        assertTrue(minasAdyacentesCalculadas);
    }
    
    @Test
    @DisplayName("Test: Victoria del juego")
    void testVictoriaJuego() {
        tablero.colocarMinas(0, 0);
        
        // Revelar todas las celdas que no son minas
        for (int fila = 0; fila < tablero.getTamano(); fila++) {
            for (int columna = 0; columna < tablero.getTamano(); columna++) {
                if (!tablero.getCelda(fila, columna).esMina()) {
                    tablero.revelarCelda(fila, columna);
                }
            }
        }
        
        // Verificar victoria
        assertTrue(tablero.isJuegoTerminado());
        assertTrue(tablero.isJuegoGanado());
    }
    
    @Test
    @DisplayName("Test: Constructor con cantidad de minas personalizada")
    void testConstructorConMinasPersonalizadas() {
        TableroBuscaminas tableroPersonalizado = new TableroBuscaminas(30);
        assertEquals(30, tableroPersonalizado.getCantidadMinas());
        assertEquals(20, tableroPersonalizado.getTamano());
    }
    
    @Test
    @DisplayName("Test: Límites del tablero")
    void testLimitesTablero() {
        tablero.colocarMinas(0, 0);
        
        // Verificar que no se puede acceder fuera de los límites
        assertThrows(IndexOutOfBoundsException.class, () -> {
            tablero.getCelda(-1, 0);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            tablero.getCelda(20, 0);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            tablero.getCelda(0, -1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            tablero.getCelda(0, 20);
        });
    }
}
