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
        tablero.colocarMinas(0, 0);
        assertFalse(tablero.getCelda(0, 0).esMina());
        
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
        
        boolean celdaMarcada = tablero.marcarCelda(0, 0);
        assertTrue(celdaMarcada);
        assertTrue(tablero.getCelda(0, 0).estaMarcada());
        assertEquals(1, tablero.getMinasMarcadas());
        
        boolean celdaDesmarcada = tablero.marcarCelda(0, 0);
        assertTrue(celdaDesmarcada);
        assertFalse(tablero.getCelda(0, 0).estaMarcada());
        assertEquals(0, tablero.getMinasMarcadas());
    }
    
    @Test
    @DisplayName("Test: No marcar celda ya revelada")
    void testNoMarcarCeldaRevelada() {
        tablero.colocarMinas(0, 0);
        
        tablero.revelarCelda(0, 0);
        
        boolean marcadoFallido = tablero.marcarCelda(0, 0);
        assertFalse(marcadoFallido);
        assertFalse(tablero.getCelda(0, 0).estaMarcada());
    }
    
    @Test
    @DisplayName("Test: Algoritmo de inundación")
    void testAlgoritmoInundacion() {
        tablero.colocarMinas(0, 0);
        
        int celdasReveladasAntes = tablero.getCeldasReveladas();
        tablero.revelarCelda(0, 0);
        int celdasReveladasDespues = tablero.getCeldasReveladas();
        
        assertTrue(celdasReveladasDespues > celdasReveladasAntes);
    }
    
    @Test
    @DisplayName("Test: Reinicio del tablero")
    void testReinicioTablero() {
        tablero.colocarMinas(0, 0);
        tablero.revelarCelda(0, 0);
        tablero.marcarCelda(1, 1);
        
        tablero.reiniciar();
        
        assertEquals(0, tablero.getCeldasReveladas());
        assertEquals(0, tablero.getMinasMarcadas());
        assertFalse(tablero.isJuegoTerminado());
        assertFalse(tablero.isJuegoGanado());
        
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
        
        for (int fila = 0; fila < tablero.getTamano(); fila++) {
            for (int columna = 0; columna < tablero.getTamano(); columna++) {
                if (!tablero.getCelda(fila, columna).esMina()) {
                    tablero.revelarCelda(fila, columna);
                }
            }
        }
        
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
