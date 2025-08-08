package controller;


public class EstilosUI {
    
    // Constantes para colores
    private static final String COLOR_FONDO_BOTON = "#F1F3F4";
    private static final String COLOR_BORDE_BOTON = "#BDBDBD";
    private static final String COLOR_FONDO_HOVER = "#E3F2FD";
    private static final String COLOR_BORDE_HOVER = "#42A5F5";
    private static final String COLOR_TEXTO_X = "#1976D2";
    private static final String COLOR_TEXTO_O = "#D81B60";
    private static final String COLOR_TEXTO_NORMAL = "#212121";
    
    // Constantes para estilos de botones
    private static final String ESTILO_BASE_BOTON = 
        "-fx-background-color: " + COLOR_FONDO_BOTON + "; " +
        "-fx-border-color: " + COLOR_BORDE_BOTON + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-text-fill: " + COLOR_TEXTO_NORMAL + "; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 40px;";
    
    private static final String ESTILO_HOVER_BOTON = 
        "-fx-background-color: " + COLOR_FONDO_HOVER + "; " +
        "-fx-border-color: " + COLOR_BORDE_HOVER + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-text-fill: " + COLOR_TEXTO_NORMAL + "; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 40px;";
    
    // Estilos para botones con símbolos
    public static final String ESTILO_BOTON_X = 
        "-fx-background-color: " + COLOR_FONDO_BOTON + "; " +
        "-fx-border-color: " + COLOR_BORDE_BOTON + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-text-fill: " + COLOR_TEXTO_X + "; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 40px;";
    
    public static final String ESTILO_BOTON_O = 
        "-fx-background-color: " + COLOR_FONDO_BOTON + "; " +
        "-fx-border-color: " + COLOR_BORDE_BOTON + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-text-fill: " + COLOR_TEXTO_O + "; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 40px;";
    
    // Estilos para botón Nuevo Juego
    public static final String ESTILO_BOTON_NUEVO_JUEGO = 
        "-fx-background-color: #42A5F5; " +
        "-fx-text-fill: white; " +
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-padding: 8;";
    
    public static final String ESTILO_BOTON_NUEVO_JUEGO_HOVER = 
        "-fx-background-color: #1E88E5; " +
        "-fx-text-fill: white; " +
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-border-radius: 5; " +
        "-fx-background-radius: 5; " +
        "-fx-padding: 8; " +
        "-fx-cursor: hand;";
    
    // Estilos para alertas
    public static final String ESTILO_ALERTA = 
        "-fx-background-color: #FFFFFF; " +
        "-fx-border-color: " + COLOR_BORDE_BOTON + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 8; " +
        "-fx-background-radius: 8; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);";
    
    // Métodos para obtener estilos dinámicamente
    public static String obtenerEstiloBotonNormal() {
        return ESTILO_BASE_BOTON;
    }
    
    public static String obtenerEstiloBotonHover() {
        return ESTILO_HOVER_BOTON;
    }
    
    public static String obtenerEstiloBotonPorSimbolo(String simbolo) {
        if ("X".equals(simbolo)) {
            return ESTILO_BOTON_X;
        } else if ("O".equals(simbolo)) {
            return ESTILO_BOTON_O;
        }
        return ESTILO_BASE_BOTON;
    }
    
    // Métodos para estilos del buscaminas
    public static String obtenerEstiloBotonBuscaminas() {
        return "-fx-background-color: #E8E8E8; " +
               "-fx-border-color: #BDBDBD; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: #212121; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
    
    public static String obtenerEstiloBotonBuscaminasHover() {
        return "-fx-background-color: #D0D0D0; " +
               "-fx-border-color: #9E9E9E; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: #212121; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
    
    public static String obtenerEstiloBotonMina() {
        return "-fx-background-color: #FF5722; " +
               "-fx-border-color: #D84315; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: white; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
    
    public static String obtenerEstiloBotonNumero(int numero) {
        String colorTexto;
        switch (numero) {
            case 1: colorTexto = "#1976D2"; break; // Azul
            case 2: colorTexto = "#388E3C"; break; // Verde
            case 3: colorTexto = "#D32F2F"; break; // Rojo
            case 4: colorTexto = "#7B1FA2"; break; // Púrpura
            case 5: colorTexto = "#F57C00"; break; // Naranja
            case 6: colorTexto = "#0097A7"; break; // Cyan
            case 7: colorTexto = "#424242"; break; // Gris
            case 8: colorTexto = "#616161"; break; // Gris claro
            default: colorTexto = "#212121"; break;
        }
        
        return "-fx-background-color: #F5F5F5; " +
               "-fx-border-color: #BDBDBD; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: " + colorTexto + "; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
    
    public static String obtenerEstiloBotonRevelado() {
        return "-fx-background-color: #F5F5F5; " +
               "-fx-border-color: #BDBDBD; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: #212121; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
    
    public static String obtenerEstiloBotonMarcado() {
        return "-fx-background-color: #FFC107; " +
               "-fx-border-color: #FF8F00; " +
               "-fx-border-width: 1; " +
               "-fx-border-radius: 2; " +
               "-fx-background-radius: 2; " +
               "-fx-text-fill: #212121; " +
               "-fx-font-weight: bold; " +
               "-fx-font-size: 12px;";
    }
} 