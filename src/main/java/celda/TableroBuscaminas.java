package celda;

import java.util.Random;
import java.util.Stack;


public class TableroBuscaminas {
    private static final int TAMANO_TABLERO = 20;
    private static final int CANTIDAD_MINAS_DEFAULT = 60; // 15% del tablero
    
    private Celda[][] tablero;
    private boolean juegoTerminado;
    private boolean juegoGanado;
    private int celdasReveladas;
    private int minasMarcadas;
    private int cantidadMinas;
    
    // ========== CONSTRUCTORES ==========

    public TableroBuscaminas() {
        this(CANTIDAD_MINAS_DEFAULT);
    }
    

    public TableroBuscaminas(int cantidadMinas) {
        this.cantidadMinas = cantidadMinas;
        this.tablero = new Celda[TAMANO_TABLERO][TAMANO_TABLERO];
        inicializarTablero();
    }
    
    // ========== MÉTODOS PRIVADOS ==========

    private void inicializarTablero() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                tablero[fila][columna] = new Celda();
            }
        }
    }
    

    public void colocarMinas(int filaPrimerClic, int columnaPrimerClic) {
        Random random = new Random();
        int minasColocadas = 0;
        
        while (minasColocadas < cantidadMinas) {
            int fila = random.nextInt(TAMANO_TABLERO);
            int columna = random.nextInt(TAMANO_TABLERO);
            
            // Evitar colocar mina en la primera celda clickeada
            if (!tablero[fila][columna].esMina() && 
                (fila != filaPrimerClic || columna != columnaPrimerClic)) {
                tablero[fila][columna].establecerComoMina();
                minasColocadas++;
            }
        }
        
        calcularMinasAdyacentes();
    }
    

    private void calcularMinasAdyacentes() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                if (!tablero[fila][columna].esMina()) {
                    int minasAdyacentes = contarMinasAdyacentes(fila, columna);
                    for (int i = 0; i < minasAdyacentes; i++) {
                        tablero[fila][columna].incrementarMinasAdyacentes();
                    }
                }
            }
        }
    }
    

    private int contarMinasAdyacentes(int fila, int columna) {
        int contador = 0;
        
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nuevaFila = fila + df;
                int nuevaColumna = columna + dc;
                
                if (esPosicionValida(nuevaFila, nuevaColumna) && 
                    tablero[nuevaFila][nuevaColumna].esMina()) {
                    contador++;
                }
            }
        }
        
        return contador;
    }
    

    private boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < TAMANO_TABLERO && 
               columna >= 0 && columna < TAMANO_TABLERO;
    }
    

    public boolean revelarCelda(int fila, int columna) {
        if (!esPosicionValida(fila, columna) || 
            tablero[fila][columna].estaRevelada() || 
            tablero[fila][columna].estaMarcada()) {
            return false;
        }
        
        Celda celda = tablero[fila][columna];
        celda.revelar();
        celdasReveladas++;
        
        if (celda.esMina()) {
            juegoTerminado = true;
            return false;
        }
        
        if (celda.esVacia()) {
            revelarCeldasAdyacentes(fila, columna);
        }
        
        verificarVictoria();
        return true;
    }
    

    private void revelarCeldasAdyacentes(int fila, int columna) {
        Stack<int[]> pila = new Stack<>();
        pila.push(new int[]{fila, columna});
        
        while (!pila.isEmpty()) {
            int[] posicion = pila.pop();
            int f = posicion[0];
            int c = posicion[1];
            
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nuevaFila = f + df;
                    int nuevaColumna = c + dc;
                    
                    if (esPosicionValida(nuevaFila, nuevaColumna)) {
                        Celda celdaAdyacente = tablero[nuevaFila][nuevaColumna];
                        
                        if (!celdaAdyacente.estaRevelada() && !celdaAdyacente.estaMarcada()) {
                            celdaAdyacente.revelar();
                            celdasReveladas++;
                            
                            if (celdaAdyacente.esVacia()) {
                                pila.push(new int[]{nuevaFila, nuevaColumna});
                            }
                        }
                    }
                }
            }
        }
    }
    

    public boolean marcarCelda(int fila, int columna) {
        if (!esPosicionValida(fila, columna) || tablero[fila][columna].estaRevelada()) {
            return false;
        }
        
        Celda celda = tablero[fila][columna];
        boolean estabaMarcada = celda.estaMarcada();
        celda.alternarMarcado();
        
        if (estabaMarcada) {
            minasMarcadas--;
        } else {
            minasMarcadas++;
        }
        
        verificarVictoria();
        return true;
    }
    

    private void verificarVictoria() {
        int celdasSeguras = TAMANO_TABLERO * TAMANO_TABLERO - cantidadMinas;
        if (celdasReveladas == celdasSeguras) {
            juegoGanado = true;
            juegoTerminado = true;
        }
    }
    

    public void reiniciar() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                tablero[fila][columna].reiniciar();
            }
        }
        
        juegoTerminado = false;
        juegoGanado = false;
        celdasReveladas = 0;
        minasMarcadas = 0;
    }
    
    // ========== GETTERS ==========

    public Celda getCelda(int fila, int columna) {
        return esPosicionValida(fila, columna) ? tablero[fila][columna] : null;
    }
    

    public int getTamano() {
        return TAMANO_TABLERO;
    }
    

    public int getCantidadMinas() {
        return cantidadMinas;
    }
    

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    

    public boolean isJuegoGanado() {
        return juegoGanado;
    }
    

    public int getCeldasReveladas() {
        return celdasReveladas;
    }
    

    public int getMinasMarcadas() {
        return minasMarcadas;
    }
} 