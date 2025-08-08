package celda;


public class Celda {
    private boolean esMina;
    private boolean estaRevelada;
    private boolean estaMarcada;
    private int minasAdyacentes;
    
    // ========== CONSTRUCTOR ==========

    public Celda() {
        this.esMina = false;
        this.estaRevelada = false;
        this.estaMarcada = false;
        this.minasAdyacentes = 0;
    }
    
    // ========== GETTERS ==========

    public boolean esMina() {
        return esMina;
    }
    

    public boolean estaRevelada() {
        return estaRevelada;
    }
    

    public boolean estaMarcada() {
        return estaMarcada;
    }
    

    public int getMinasAdyacentes() {
        return minasAdyacentes;
    }
    
    // ========== SETTERS ==========

    public void establecerComoMina() {
        this.esMina = true;
    }
    

    public void revelar() {
        this.estaRevelada = true;
    }
    

    public void alternarMarcado() {
        this.estaMarcada = !this.estaMarcada;
    }
    

    public void incrementarMinasAdyacentes() {
        this.minasAdyacentes++;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========

    public boolean esVacia() {
        return !esMina && minasAdyacentes == 0;
    }
    

    public boolean tieneMinasAdyacentes() {
        return !esMina && minasAdyacentes > 0;
    }
    

    public void reiniciar() {
        this.esMina = false;
        this.estaRevelada = false;
        this.estaMarcada = false;
        this.minasAdyacentes = 0;
    }
} 