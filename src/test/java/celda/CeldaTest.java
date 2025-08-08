package celda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CeldaTest {
    
    Celda celda;
    
    @BeforeEach
    void setUp() {
        celda = new Celda();
    }
    
    // ========== PRUEBAS DEL CONSTRUCTOR ==========
    
    @Test
    void testConstructor_EstadoInicial() {
        // Verificar que la celda se inicializa correctamente
        assertFalse(celda.esMina(), "Una celda nueva no debería ser una mina");
        assertFalse(celda.estaRevelada(), "Una celda nueva no debería estar revelada");
        assertFalse(celda.estaMarcada(), "Una celda nueva no debería estar marcada");
        assertEquals(0, celda.getMinasAdyacentes(), "Una celda nueva debería tener 0 minas adyacentes");
    }
    
    // ========== PRUEBAS DE GETTERS ==========
    
    @Test
    void testEsMina_ValorPorDefecto() {
        boolean resultado = celda.esMina();
        assertFalse(resultado, "Una celda nueva debería retornar false para esMina()");
    }
    
    @Test
    void testEstaRevelada_ValorPorDefecto() {
        boolean resultado = celda.estaRevelada();
        assertFalse(resultado, "Una celda nueva debería retornar false para estaRevelada()");
    }
    
    @Test
    void testEstaMarcada_ValorPorDefecto() {
        boolean resultado = celda.estaMarcada();
        assertFalse(resultado, "Una celda nueva debería retornar false para estaMarcada()");
    }
    
    @Test
    void testGetMinasAdyacentes_ValorPorDefecto() {
        int resultado = celda.getMinasAdyacentes();
        assertEquals(0, resultado, "Una celda nueva debería tener 0 minas adyacentes");
    }
    
    // ========== PRUEBAS DE SETTERS ==========
    
    @Test
    void testEstablecerComoMina_CambiaEstado() {
        celda.establecerComoMina();
        assertTrue(celda.esMina(), "Después de establecer como mina, esMina() debería retornar true");
    }
    
    @Test
    void testRevelar_CambiaEstado() {
        celda.revelar();
        assertTrue(celda.estaRevelada(), "Después de revelar, estaRevelada() debería retornar true");
    }
    
    @Test
    void testAlternarMarcado_DeFalseATrue() {
        celda.alternarMarcado();
        assertTrue(celda.estaMarcada(), "Después de alternar marcado, estaMarcada() debería retornar true");
    }
    
    @Test
    void testAlternarMarcado_DeTrueAFalse() {
        // Primero marcar la celda
        celda.alternarMarcado();
        assertTrue(celda.estaMarcada(), "Primera alternancia debería marcar la celda");
        
        // Luego desmarcar
        celda.alternarMarcado();
        assertFalse(celda.estaMarcada(), "Segunda alternancia debería desmarcar la celda");
    }
    
    @Test
    void testIncrementarMinasAdyacentes_AumentaContador() {
        celda.incrementarMinasAdyacentes();
        assertEquals(1, celda.getMinasAdyacentes(), "Después de incrementar, debería tener 1 mina adyacente");
        
        celda.incrementarMinasAdyacentes();
        assertEquals(2, celda.getMinasAdyacentes(), "Después de incrementar dos veces, debería tener 2 minas adyacentes");
    }
    
    // ========== PRUEBAS DE MÉTODOS DE UTILIDAD ==========
    
    @Test
    void testEsVacia_CeldaNueva() {
        boolean resultado = celda.esVacia();
        assertTrue(resultado, "Una celda nueva debería ser vacía (sin mina y sin minas adyacentes)");
    }
    
    @Test
    void testEsVacia_CeldaConMina() {
        celda.establecerComoMina();
        boolean resultado = celda.esVacia();
        assertFalse(resultado, "Una celda con mina no debería ser vacía");
    }
    
    @Test
    void testEsVacia_CeldaConMinasAdyacentes() {
        celda.incrementarMinasAdyacentes();
        boolean resultado = celda.esVacia();
        assertFalse(resultado, "Una celda con minas adyacentes no debería ser vacía");
    }
    
    @Test
    void testTieneMinasAdyacentes_CeldaNueva() {
        boolean resultado = celda.tieneMinasAdyacentes();
        assertFalse(resultado, "Una celda nueva no debería tener minas adyacentes");
    }
    
    @Test
    void testTieneMinasAdyacentes_CeldaConMinasAdyacentes() {
        celda.incrementarMinasAdyacentes();
        boolean resultado = celda.tieneMinasAdyacentes();
        assertTrue(resultado, "Una celda con minas adyacentes debería retornar true");
    }
    
    @Test
    void testTieneMinasAdyacentes_CeldaConMina() {
        celda.establecerComoMina();
        celda.incrementarMinasAdyacentes();
        boolean resultado = celda.tieneMinasAdyacentes();
        assertFalse(resultado, "Una celda que es mina no debería tener minas adyacentes (método retorna false)");
    }
    
    // ========== PRUEBAS DE REINICIO ==========
    
    @Test
    void testReiniciar_ReseteaTodosLosEstados() {
        // Configurar la celda con varios estados
        celda.establecerComoMina();
        celda.revelar();
        celda.alternarMarcado();
        celda.incrementarMinasAdyacentes();
        
        // Verificar que los estados están configurados
        assertTrue(celda.esMina(), "Celda debería ser mina antes del reinicio");
        assertTrue(celda.estaRevelada(), "Celda debería estar revelada antes del reinicio");
        assertTrue(celda.estaMarcada(), "Celda debería estar marcada antes del reinicio");
        assertEquals(1, celda.getMinasAdyacentes(), "Celda debería tener minas adyacentes antes del reinicio");
        
        // Reiniciar
        celda.reiniciar();
        
        // Verificar que todos los estados se resetean
        assertFalse(celda.esMina(), "Después del reinicio, esMina() debería retornar false");
        assertFalse(celda.estaRevelada(), "Después del reinicio, estaRevelada() debería retornar false");
        assertFalse(celda.estaMarcada(), "Después del reinicio, estaMarcada() debería retornar false");
        assertEquals(0, celda.getMinasAdyacentes(), "Después del reinicio, debería tener 0 minas adyacentes");
    }
    
    // ========== PRUEBAS DE INTEGRACIÓN ==========
    
    @Test
    void testFlujoCompleto_RevelarCeldaVacia() {
        // Simular el flujo de revelar una celda vacía
        celda.revelar();
        
        assertTrue(celda.estaRevelada(), "La celda debería estar revelada");
        assertFalse(celda.esMina(), "La celda no debería ser una mina");
        assertFalse(celda.estaMarcada(), "La celda no debería estar marcada");
        assertEquals(0, celda.getMinasAdyacentes(), "La celda debería tener 0 minas adyacentes");
        assertTrue(celda.esVacia(), "La celda debería ser vacía");
    }
    
    @Test
    void testFlujoCompleto_MarcarYDesmarcar() {
        // Simular el flujo de marcar y desmarcar una celda
        celda.alternarMarcado();
        assertTrue(celda.estaMarcada(), "Primera alternancia debería marcar la celda");
        
        celda.alternarMarcado();
        assertFalse(celda.estaMarcada(), "Segunda alternancia debería desmarcar la celda");
        
        celda.alternarMarcado();
        assertTrue(celda.estaMarcada(), "Tercera alternancia debería marcar la celda nuevamente");
    }
}