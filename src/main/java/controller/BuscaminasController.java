package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.control.DialogPane;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import celda.Celda;
import celda.TableroBuscaminas;
import persistence.SlotGuardadoService;
import persistence.SlotGuardadoDAO;

import java.util.List;
import java.util.Optional;
public class BuscaminasController {

    // Constantes del juego
    private static final int TAMANO_TABLERO = 20;
    private static final int TAMANO_BOTON = 25;
    private static final int TAMANO_FUENTE = 12;
    private static final int ESPACIADO_GRID = 1;
    
    // Atributos del modelo
    private TableroBuscaminas tableroLogico;
    private Button[][] botones;
    private boolean primerClic;
    private boolean juegoIniciado;
    
    // Elementos de la interfaz
    @FXML private GridPane gridTablero;
    @FXML private Label lblCierre;
    @FXML private Label lblMinasRestantes;
    @FXML private Label lblTiempo;
    @FXML private Button btnNuevoJuego;
    @FXML private Label lblEstado;
    @FXML private Button btnPista;
    @FXML private Button btnModoFacil;
    @FXML private Button btnGuardarPartida;
    @FXML private Button btnCargarPartidas;
    
    // Variables de tiempo
    private long tiempoInicio;
    private boolean cronometroActivo;
    
    // Variables para los modos de juego
    private enum Dificultad {
        FACIL(30, "🌱 Fácil"),
        INTERMEDIO(60, "⚡ Intermedio"),
        DIFICIL(90, "💀 Difícil");
        
        private final int minas;
        private final String nombre;
        
        Dificultad(int minas, String nombre) {
            this.minas = minas;
            this.nombre = nombre;
        }
        
        public int getMinas() { return minas; }
        public String getNombre() { return nombre; }
    }
    
    private Dificultad dificultadActual = Dificultad.INTERMEDIO;
    
    // Variables para la base de datos
    private SlotGuardadoService slotService;
    private Integer slotActual = null;
    private String nombreJugador = "Jugador";
    private boolean guardadoAutomatico = true;
    
    // ========== MÉTODOS DE EVENTOS FXML ==========
    
    @FXML
    void click(MouseEvent event) {
        cerrarAplicacion();
    }
    
    @FXML
    void enter(MouseEvent event) {
        aplicarEstiloHoverCierre();
    }
    
    @FXML
    void exited(MouseEvent event) {
        aplicarEstiloNormalCierre();
    }
    
    @FXML
    void nuevoJuego(ActionEvent event) {
        reiniciarJuego();
    }
    
    @FXML
    void mostrarPista(ActionEvent event) {
        mostrarPista();
    }
    
    @FXML
    void cambiarModoFacil(ActionEvent event) {
        cambiarModoFacil();
    }
    
    @FXML
    void guardarPartida(ActionEvent event) {
        guardarPartidaActual();
    }
    
    @FXML
    void cargarPartidas(ActionEvent event) {
        mostrarVentanaPartidasGuardadas();
    }
    

    
    @FXML
    void initialize() {
        inicializarModelos();
        inicializarServiciosBaseDatos();
        configurarGrid();
        configurarBotonNuevoJuego();
        crearBotonesTablero();
        iniciarCronometro();
        
        // Configurar el botón de dificultad
        btnModoFacil.setText("🎯 Dificultad");
    }
    
    // ========== MÉTODOS DE INICIALIZACIÓN ==========
    

    private void inicializarModelos() {
        tableroLogico = new TableroBuscaminas();
        botones = new Button[TAMANO_TABLERO][TAMANO_TABLERO];
        primerClic = true;
        juegoIniciado = false;
    }
    

    private void inicializarServiciosBaseDatos() {
        try {
            slotService = new SlotGuardadoService();
            if (!slotService.isDatabaseAvailable()) {
                mostrarAlerta("Advertencia", "La base de datos no está disponible. Las funcionalidades de guardado estarán deshabilitadas.");
                btnGuardarPartida.setDisable(true);
                btnCargarPartidas.setDisable(true);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo inicializar la base de datos: " + e.getMessage());
            btnGuardarPartida.setDisable(true);
            btnCargarPartidas.setDisable(true);
        }
    }
    

    private void configurarGrid() {
        gridTablero.setHgap(ESPACIADO_GRID);
        gridTablero.setVgap(ESPACIADO_GRID);
        gridTablero.setAlignment(Pos.CENTER);
    }
    

    private void configurarBotonNuevoJuego() {
        btnNuevoJuego.setStyle(EstilosUI.ESTILO_BOTON_NUEVO_JUEGO);
        
        btnNuevoJuego.setOnMouseEntered(e -> 
            btnNuevoJuego.setStyle(EstilosUI.ESTILO_BOTON_NUEVO_JUEGO_HOVER));
        
        btnNuevoJuego.setOnMouseExited(e -> 
            btnNuevoJuego.setStyle(EstilosUI.ESTILO_BOTON_NUEVO_JUEGO));
    }
    

    private void crearBotonesTablero() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                Button boton = crearBotonTablero(fila, columna);
                botones[fila][columna] = boton;
                gridTablero.add(boton, columna, fila);
            }
        }
    }
    

    private Button crearBotonTablero(int fila, int columna) {
        Button boton = new Button();
        configurarPropiedadesBoton(boton);
        configurarEstilosBoton(boton);
        configurarEventosBoton(boton, fila, columna);
        return boton;
    }
    

    private void configurarPropiedadesBoton(Button boton) {
        boton.setFont(Font.font("Arial", TAMANO_FUENTE));
        boton.setPrefSize(TAMANO_BOTON, TAMANO_BOTON);
        boton.setMinSize(TAMANO_BOTON, TAMANO_BOTON);
        boton.setMaxSize(TAMANO_BOTON, TAMANO_BOTON);
    }
    

    private void configurarEstilosBoton(Button boton) {
        boton.setStyle(EstilosUI.obtenerEstiloBotonBuscaminas());
        
        boton.setOnMouseEntered(e -> {
            if (!tableroLogico.isJuegoTerminado() || !boton.getText().equals("💣")) {
                boton.setStyle(EstilosUI.obtenerEstiloBotonBuscaminasHover());
            }
        });
        
        boton.setOnMouseExited(e -> {
            if (!boton.getText().equals("🚩") && !boton.getText().equals("💣")) {
                boton.setStyle(EstilosUI.obtenerEstiloBotonBuscaminas());
            } else if (boton.getText().equals("🚩")) {
                // Mantener el estilo de bandera cuando el mouse sale
                boton.setStyle(EstilosUI.obtenerEstiloBotonMarcado());
            }
        });
    }
    

    private void configurarEventosBoton(Button boton, int fila, int columna) {
        boton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                manejarClicIzquierdo(fila, columna);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                manejarClicDerecho(fila, columna);
            }
        });
    }
    
    // ========== MÉTODOS DE LÓGICA DEL JUEGO ==========
    


    private void manejarClicIzquierdo(int fila, int columna) {
        if (tableroLogico.isJuegoTerminado()) {
            return;
        }
        
        if (primerClic) {
            iniciarJuego(fila, columna);
        }
        
        if (tableroLogico.revelarCelda(fila, columna)) {
            // Actualizar todas las celdas que fueron reveladas por el algoritmo de inundación
            actualizarTodasLasCeldasReveladas();
            actualizarInterfaz();
            
            // Guardar automáticamente después de cada movimiento
            guardarAutomaticamente();
            
            if (tableroLogico.isJuegoTerminado()) {
                manejarFinJuego();
            }
        } else if (tableroLogico.getCelda(fila, columna) != null && 
                   tableroLogico.getCelda(fila, columna).esMina()) {
            revelarTodasLasMinas();
            manejarFinJuego();
        }
    }
    


    private void manejarClicDerecho(int fila, int columna) {
        if (tableroLogico.isJuegoTerminado()) {
            return;
        }
        
        if (tableroLogico.marcarCelda(fila, columna)) {
            actualizarBotonMarcado(fila, columna);
            actualizarInterfaz();
            
            // Guardar automáticamente después de marcar/desmarcar
            guardarAutomaticamente();
        }
    }
    

    private void iniciarJuego(int fila, int columna) {
        tableroLogico.colocarMinas(fila, columna);
        primerClic = false;
        juegoIniciado = true;
        tiempoInicio = System.currentTimeMillis();
        cronometroActivo = true;
        lblEstado.setText("¡Juego iniciado!");
        
        // Deshabilitar botones durante el juego
        deshabilitarBotonesDuranteJuego();
        
        // Crear nueva partida automáticamente al iniciar
        crearNuevaPartida();
    }
    

    private void actualizarBotonRevelado(int fila, int columna) {
        Button boton = botones[fila][columna];
        Celda celda = tableroLogico.getCelda(fila, columna);
        
        if (celda.esMina()) {
            mostrarImagenMina(boton);
        } else if (celda.tieneMinasAdyacentes()) {
            boton.setText(String.valueOf(celda.getMinasAdyacentes()));
            boton.setStyle(EstilosUI.obtenerEstiloBotonNumero(celda.getMinasAdyacentes()));
        } else {
            boton.setText("");
            boton.setStyle(EstilosUI.obtenerEstiloBotonRevelado());
        }
        
        boton.setDisable(true);
    }
    

    private void actualizarBotonMarcado(int fila, int columna) {
        Button boton = botones[fila][columna];
        Celda celda = tableroLogico.getCelda(fila, columna);
        
        if (celda.estaMarcada()) {
            // Usar imagen de banderita en lugar del emoji
            boton.setText("");
            boton.setStyle(EstilosUI.obtenerEstiloBotonMarcado());
            // Cargar y establecer la imagen de la banderita
            try {
                javafx.scene.image.Image imagenBanderita = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/images/Banderita.png")
                );
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(imagenBanderita);
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                boton.setGraphic(imageView);
            } catch (Exception e) {
                // Fallback al emoji si no se puede cargar la imagen
                boton.setText("🚩");
                boton.setGraphic(null);
            }
        } else {
            boton.setText("");
            boton.setGraphic(null);
            boton.setStyle(EstilosUI.obtenerEstiloBotonBuscaminas());
        }
    }


    private void actualizarTodasLasCeldasReveladas() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                Celda celda = tableroLogico.getCelda(fila, columna);
                Button boton = botones[fila][columna];
                
                if (celda.estaRevelada() && !boton.isDisabled()) {
                    if (celda.esMina()) {
                        mostrarImagenMina(boton);
                    } else if (celda.tieneMinasAdyacentes()) {
                        boton.setText(String.valueOf(celda.getMinasAdyacentes()));
                        boton.setStyle(EstilosUI.obtenerEstiloBotonNumero(celda.getMinasAdyacentes()));
                    } else {
                        boton.setText("");
                        boton.setStyle(EstilosUI.obtenerEstiloBotonRevelado());
                    }
                    boton.setDisable(true);
                }
            }
        }
    }
    

    private void revelarTodasLasMinas() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                Celda celda = tableroLogico.getCelda(fila, columna);
                Button boton = botones[fila][columna];
                
                if (celda.esMina() && !celda.estaMarcada()) {
                    mostrarImagenMina(boton);
                    // Desactivar efectos hover para las minas
                    boton.setOnMouseEntered(null);
                    boton.setOnMouseExited(null);
                } else if (celda.estaRevelada() && !celda.esMina()) {
                    // Mostrar celdas reveladas que no son minas
                    if (celda.tieneMinasAdyacentes()) {
                        boton.setText(String.valueOf(celda.getMinasAdyacentes()));
                        boton.setStyle(EstilosUI.obtenerEstiloBotonNumero(celda.getMinasAdyacentes()));
                    } else {
                        boton.setText("");
                        boton.setStyle(EstilosUI.obtenerEstiloBotonRevelado());
                    }
                }
                boton.setDisable(true);
            }
        }
    }
    

    private void manejarFinJuego() {
        cronometroActivo = false;
        
        // Habilitar botones después del juego
        habilitarBotonesDespuesDelJuego();
        
        // Pequeña pausa para que el usuario vea el resultado antes de la alerta
        Platform.runLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (tableroLogico.isJuegoGanado()) {
                mostrarAlerta("¡Victoria!", "¡Felicidades! Has ganado el juego.");
                lblEstado.setText("¡Victoria!");
               
            } else {
                mostrarAlerta("¡Game Over!", "Has perdido. ¡Inténtalo de nuevo!");
                lblEstado.setText("¡Perdiste!");
               
            }
        });
    }
    

    private void manejarFinJuegoSinAlerta() {
        cronometroActivo = false;
        
        // Habilitar botones después del juego
        habilitarBotonesDespuesDelJuego();
        
        if (tableroLogico.isJuegoGanado()) {
            lblEstado.setText("¡Victoria! - Presiona 'Nuevo Juego' para continuar");
            lblEstado.setStyle("-fx-text-fill: #388E3C; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            lblEstado.setText("¡Game Over! - Presiona 'Nuevo Juego' para intentar de nuevo");
            lblEstado.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }
    
    // ========== MÉTODOS DE INTERFAZ ==========
    

    private void actualizarInterfaz() {
        int minasRestantes = tableroLogico.getCantidadMinas() - tableroLogico.getMinasMarcadas();
        lblMinasRestantes.setText("Minas: " + minasRestantes);
    }
    

    private void iniciarCronometro() {
        Thread cronometro = new Thread(() -> {
            while (true) {
                if (cronometroActivo && juegoIniciado) {
                    long tiempoTranscurrido = (System.currentTimeMillis() - tiempoInicio) / 1000;
                    Platform.runLater(() -> lblTiempo.setText("Tiempo: " + tiempoTranscurrido + "s"));
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cronometro.setDaemon(true);
        cronometro.start();
    }
    

    private void mostrarAlerta(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(EstilosUI.ESTILO_ALERTA);
            
            // Configurar para que aparezca en el centro y por encima de todo
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(gridTablero.getScene().getWindow());
            
            alert.showAndWait();
        });
    }
    

    private void aplicarEstiloHoverCierre() {
        lblCierre.setStyle("-fx-text-fill:red;");
    }
    

    private void aplicarEstiloNormalCierre() {
        lblCierre.setStyle("-fx-text-fill:Black;");
    }
    

    private void cerrarAplicacion() {
        Platform.exit();
        System.exit(0);
    }
    
    // ========== MÉTODOS DE REINICIO ==========
    

    private void reiniciarJuego() {
        reiniciarModelos();
        reiniciarInterfaz();
        reiniciarBotones();
        iniciarCronometro();
        
        // Habilitar botones al reiniciar
        habilitarBotonesDespuesDelJuego();
        
        // Resetear variables de partida
        slotActual = null;
        nombreJugador = "Jugador";
        guardadoAutomatico = true;
    }
    

    private void reiniciarModelos() {
        tableroLogico.reiniciar();
        primerClic = true;
        juegoIniciado = false;
        cronometroActivo = false;
    }
    

    private void reiniciarInterfaz() {
        lblEstado.setText("¡Preparado para jugar! - " + dificultadActual.getNombre() + " (" + dificultadActual.getMinas() + " minas)");
        lblEstado.setStyle("-fx-text-fill: #212121; -fx-font-size: 16px; -fx-font-weight: bold;");
        lblMinasRestantes.setText("Minas: " + tableroLogico.getCantidadMinas());
        lblTiempo.setText("Tiempo: 0s");
        btnNuevoJuego.setText("😊");
    }
    

    private void reiniciarBotones() {
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                Button boton = botones[fila][columna];
                boton.setText("");
                boton.setGraphic(null);
                boton.setStyle(EstilosUI.obtenerEstiloBotonBuscaminas());
                boton.setDisable(false);
                configurarEstilosBoton(boton);
            }
        }
    }
    
    // ========== MÉTODOS DE AYUDA ==========
    

    private void mostrarPista() {
        if (!juegoIniciado || tableroLogico.isJuegoTerminado()) {
            mostrarAlerta("Pista", "Debes iniciar el juego primero.");
            return;
        }
        
        // Buscar una celda segura para revelar
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                Celda celda = tableroLogico.getCelda(fila, columna);
                if (!celda.estaRevelada() && !celda.estaMarcada() && !celda.esMina()) {
                    // Revelar esta celda como pista
                    tableroLogico.revelarCelda(fila, columna);
                    actualizarTodasLasCeldasReveladas();
                    actualizarInterfaz();
                    mostrarAlerta("Pista", "¡He revelado una celda segura para ti!");
                    return;
                }
            }
        }
        
        mostrarAlerta("Pista", "No hay celdas seguras obvias. ¡Sigue intentando!");
    }
    

    /**
     * Muestra el menú de selección de dificultad.
     */
    private void cambiarModoFacil() {
        if (juegoIniciado && !tableroLogico.isJuegoTerminado()) {
            mostrarAlerta("Dificultad", "No puedes cambiar la dificultad durante el juego.");
            return;
        }
        
        mostrarMenuDificultad();
    }
    
    /**
     * Muestra un menú modal para seleccionar la dificultad.
     */
    private void mostrarMenuDificultad() {
        // Crear ventana modal para selección de dificultad
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(gridTablero.getScene().getWindow());
        modalStage.setTitle("Seleccionar Dificultad");
        modalStage.setResizable(false);
        modalStage.setMinWidth(300);
        modalStage.setMinHeight(300);
        
        // Crear contenido de la ventana
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
        content.setAlignment(Pos.TOP_CENTER);
        
        // Título
        Label titleLabel = new Label("Selecciona la Dificultad");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setAlignment(Pos.CENTER);
        
        // Botones de dificultad
        VBox botonesContainer = new VBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        
        for (Dificultad dificultad : Dificultad.values()) {
            Button btnDificultad = new Button(dificultad.getNombre() + " (" + dificultad.getMinas() + " minas)");
            btnDificultad.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-min-width: 200; -fx-font-size: 14px;");
            
            // Resaltar la dificultad actual
            if (dificultad == dificultadActual) {
                btnDificultad.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-min-width: 200; -fx-font-size: 14px;");
                btnDificultad.setText(dificultad.getNombre() + " (" + dificultad.getMinas() + " minas) ✓");
            }
            
            // Evento para seleccionar dificultad
            btnDificultad.setOnAction(e -> {
                seleccionarDificultad(dificultad);
                modalStage.close();
            });
            
            botonesContainer.getChildren().add(btnDificultad);
        }
        
        // Botón cancelar
        Button cancelarBtn = new Button("Cancelar");
        cancelarBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-min-width: 100;");
        cancelarBtn.setOnAction(e -> modalStage.close());
        
        content.getChildren().addAll(titleLabel, botonesContainer, cancelarBtn);
        
        // Crear escena y mostrar
        Scene scene = new Scene(content, 300, 300);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }
    
    /**
     * Aplica la dificultad seleccionada.
     * @param dificultad la dificultad seleccionada
     */
    private void seleccionarDificultad(Dificultad dificultad) {
        dificultadActual = dificultad;
        tableroLogico = new TableroBuscaminas(dificultad.getMinas());
        
        // Mantener el texto del botón como "Dificultad"
        btnModoFacil.setText("🎯 Dificultad");
        
        // Actualizar el estado
        lblEstado.setText("Dificultad: " + dificultad.getNombre() + " - " + dificultad.getMinas() + " minas");
        
        // Reiniciar la interfaz
        reiniciarInterfaz();
    }
    
    /**
     * Deshabilita los botones que no deben estar disponibles durante el juego.
     */
    private void deshabilitarBotonesDuranteJuego() {
        btnModoFacil.setDisable(true);
        btnCargarPartidas.setDisable(true);
    }
    
    /**
     * Habilita los botones cuando el juego termina.
     */
    private void habilitarBotonesDespuesDelJuego() {
        btnModoFacil.setDisable(false);
        btnCargarPartidas.setDisable(false);
    }
    

    private void mostrarImagenMina(Button boton) {
        boton.setText("");
        boton.setStyle(EstilosUI.obtenerEstiloBotonMina());
        // Cargar y establecer la imagen de la mina
        try {
            javafx.scene.image.Image imagenMina = new javafx.scene.image.Image(
                getClass().getResourceAsStream("/images/Mina.png")
            );
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(imagenMina);
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
            boton.setGraphic(imageView);
        } catch (Exception e) {
            // Fallback al emoji si no se puede cargar la imagen
            boton.setText("💣");
            boton.setGraphic(null);
        }
    }
    
    // ========== MÉTODOS DE BASE DE DATOS ==========
    

    private void guardarPartidaActual() {
        if (!juegoIniciado) {
            mostrarAlerta("Guardar Partida", "Debes iniciar el juego primero.");
            return;
        }
        
        mostrarVentanaSeleccionSlot();
    }
    

    private void guardarAutomaticamente() {
        if (guardadoAutomatico && juegoIniciado && slotActual != null) {
            try {
                // Actualizar partida existente en el slot actual
                String nombrePartida = "Partida Automática";
                slotService.guardarEnSlot(slotActual, nombrePartida, nombreJugador, tableroLogico);
            } catch (Exception e) {
                // Silenciar errores de guardado automático para no interrumpir el juego
                System.err.println("Error en guardado automático: " + e.getMessage());
            }
        }
    }
    

    private void mostrarVentanaSeleccionSlot() {
        try {
            List<SlotGuardadoDAO.SlotInfo> slots = slotService.obtenerInfoSlots();
            
            // Crear ventana modal para selección de slot
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(gridTablero.getScene().getWindow());
            modalStage.setTitle("Seleccionar Slot de Guardado");
            modalStage.setResizable(false);
            modalStage.setMinWidth(400);
            modalStage.setMinHeight(300);
            
            // Crear contenido de la ventana
            VBox content = new VBox(20);
            content.setPadding(new Insets(25));
            content.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
            content.setAlignment(Pos.TOP_CENTER);
            
            // Título
            Label titleLabel = new Label("Selecciona un Slot para Guardar");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            titleLabel.setAlignment(Pos.CENTER);
            
            // Subtítulo con instrucciones
            Label subtitleLabel = new Label("Elige un slot y asigna un nombre a tu partida");
            subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");
            subtitleLabel.setAlignment(Pos.CENTER);
            subtitleLabel.setWrapText(true);
            
            // Lista de slots
            VBox slotsList = new VBox(10);
            slotsList.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
            
            for (SlotGuardadoDAO.SlotInfo slot : slots) {
                HBox slotItem = new HBox(15);
                slotItem.setAlignment(Pos.CENTER_LEFT);
                slotItem.setPadding(new Insets(12));
                slotItem.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-cursor: hand; -fx-background-radius: 5; -fx-border-radius: 5;");
                
                // Información del slot
                VBox slotInfo = new VBox(2);
                slotInfo.setPrefWidth(250);
                
                Label nombreLabel = new Label("Slot " + slot.getSlotId() + " - " + slot.getNombrePartida());
                nombreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
                
                Label estadoLabel = new Label(slot.estaOcupado() ? "Ocupado - " + slot.getEstadoPartida() : "Vacío");
                estadoLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                
                slotInfo.getChildren().addAll(nombreLabel, estadoLabel);
                
                // Botón seleccionar
                Button seleccionarBtn = new Button("Seleccionar");
                seleccionarBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-min-width: 80;");
                
                // Evento para seleccionar el slot
                seleccionarBtn.setOnAction(e -> {
                    seleccionarSlotParaGuardar(slot.getSlotId(), modalStage);
                });
                
                slotItem.getChildren().addAll(slotInfo, seleccionarBtn);
                slotsList.getChildren().add(slotItem);
            }
            
            // Botón cancelar
            Button cancelarBtn = new Button("Cancelar");
            cancelarBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-min-width: 100;");
            cancelarBtn.setOnAction(e -> modalStage.close());
            
            content.getChildren().addAll(titleLabel, subtitleLabel, slotsList, cancelarBtn);
            
            // Crear escena y mostrar
            Scene scene = new Scene(content, 400, 350);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los slots: " + e.getMessage());
        }
    }
    
    private void seleccionarSlotParaGuardar(int slotId, Stage modalStage) {
        // Crear ventana para ingresar nombre de la partida
        Stage nombreStage = new Stage();
        nombreStage.initModality(Modality.APPLICATION_MODAL);
        nombreStage.initOwner(modalStage);
        nombreStage.setTitle("Nombre de la Partida");
        nombreStage.setResizable(false);
        nombreStage.setMinWidth(300);
        nombreStage.setMinHeight(200);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
        content.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Asigna un nombre a tu partida");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        javafx.scene.control.TextField nombreField = new javafx.scene.control.TextField();
        nombreField.setPromptText("Ej: Mi partida épica");
        nombreField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        
        Button guardarBtn = new Button("Guardar");
        guardarBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        
        Button cancelarBtn = new Button("Cancelar");
        cancelarBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        
        guardarBtn.setOnAction(e -> {
            String nombrePartida = nombreField.getText().trim();
            if (nombrePartida.isEmpty()) {
                nombrePartida = "Partida " + System.currentTimeMillis();
            }
            
            try {
                slotService.guardarEnSlot(slotId, nombrePartida, nombreJugador, tableroLogico);
                slotActual = slotId;
                modalStage.close();
                nombreStage.close();
                mostrarAlerta("Partida Guardada", "Se ha guardado la partida en el slot " + slotId + " con el nombre: " + nombrePartida);
            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo guardar la partida: " + ex.getMessage());
            }
        });
        
        cancelarBtn.setOnAction(e -> nombreStage.close());
        
        botonesContainer.getChildren().addAll(guardarBtn, cancelarBtn);
        content.getChildren().addAll(titleLabel, nombreField, botonesContainer);
        
        Scene scene = new Scene(content, 300, 200);
        nombreStage.setScene(scene);
        nombreStage.showAndWait();
    }
    
    private void mostrarVentanaPartidasGuardadas() {
        try {
            List<SlotGuardadoDAO.SlotInfo> slots = slotService.obtenerInfoSlots();
            
            // Crear ventana modal personalizada
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(gridTablero.getScene().getWindow());
            modalStage.setTitle("Slots de Guardado");
            modalStage.setResizable(false);
            modalStage.setMinWidth(500);
            modalStage.setMinHeight(400);
            
            // Crear contenido de la ventana
            VBox content = new VBox(15);
            content.setPadding(new Insets(25));
            content.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1;");
            content.setAlignment(Pos.TOP_CENTER);
            
            // Título
            Label titleLabel = new Label("Slots de Guardado");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
            titleLabel.setAlignment(Pos.CENTER);
            
            // Subtítulo con instrucciones
            Label subtitleLabel = new Label("Haz clic en 'Cargar' para continuar una partida o 'Limpiar' para borrar el slot");
            subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");
            subtitleLabel.setAlignment(Pos.CENTER);
            subtitleLabel.setWrapText(true);
            
            // Lista de slots con scroll
            VBox slotsList = new VBox(5);
            slotsList.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
            slotsList.setPrefHeight(250);
            slotsList.setMaxHeight(250);
            
            // ScrollPane para la lista de slots
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(slotsList);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
            
            for (SlotGuardadoDAO.SlotInfo slot : slots) {
                HBox slotItem = new HBox(15);
                slotItem.setAlignment(Pos.CENTER_LEFT);
                slotItem.setPadding(new Insets(12));
                slotItem.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-cursor: hand; -fx-background-radius: 5; -fx-border-radius: 5;");
                
                // Información del slot
                VBox slotInfo = new VBox(2);
                slotInfo.setPrefWidth(300);
                
                // Marcar si es el slot actualmente cargado
                String indicadorActual = (this.slotActual != null && this.slotActual == slot.getSlotId()) ? " (ACTUAL)" : "";
                
                Label nombreLabel = new Label("Slot " + slot.getSlotId() + " - " + slot.getNombrePartida() + indicadorActual);
                nombreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
                nombreLabel.setWrapText(true);
                
                Label estadoLabel = new Label("Estado: " + slot.getEstadoPartida());
                estadoLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                
                Label progresoLabel = new Label("Progreso: " + slot.getCeldasReveladas() + "/400 celdas");
                progresoLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                
                slotInfo.getChildren().addAll(nombreLabel, estadoLabel, progresoLabel);
                
                // Contenedor para los botones
                HBox botonesContainer = new HBox(5);
                botonesContainer.setAlignment(Pos.CENTER_RIGHT);
                botonesContainer.setPrefWidth(150);
                
                // Botón cargar
                Button cargarBtn = new Button("Cargar");
                cargarBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-min-width: 60;");
                cargarBtn.setDisable(!slot.estaOcupado());
                
                // Evento para cargar el slot
                cargarBtn.setOnAction(e -> {
                    cargarSlot(slot.getSlotId());
                    modalStage.close();
                });
                
                // Botón limpiar
                Button limpiarBtn = new Button("Limpiar");
                limpiarBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-min-width: 60;");
                limpiarBtn.setDisable(!slot.estaOcupado());
                
                // Evento para limpiar el slot
                limpiarBtn.setOnAction(e -> {
                    limpiarSlot(slot.getSlotId(), slotsList, slotItem);
                });
                
                botonesContainer.getChildren().addAll(cargarBtn, limpiarBtn);
                slotItem.getChildren().addAll(slotInfo, botonesContainer);
                slotsList.getChildren().add(slotItem);
            }
            
            // Botón cerrar
            Button cerrarBtn = new Button("Cerrar");
            cerrarBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25; -fx-min-width: 100;");
            cerrarBtn.setOnAction(e -> modalStage.close());
            
            content.getChildren().addAll(titleLabel, subtitleLabel, scrollPane, cerrarBtn);
            
            // Crear escena y mostrar
            Scene scene = new Scene(content, 500, 450);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los slots: " + e.getMessage());
        }
    }
    

    private String solicitarNombreJugador() {
        // Implementación simple - en una versión más avanzada podrías usar un diálogo
        return "Jugador_" + System.currentTimeMillis();
    }
    

    private void crearNuevaPartida() {
        nombreJugador = solicitarNombreJugador();
        slotActual = null; // Resetear slot para nueva partida
        guardadoAutomatico = true;
    }
    

    private void cargarSlot(int slotId) {
        try {
            TableroBuscaminas tableroCargado = slotService.cargarDesdeSlot(slotId);
            tableroLogico = tableroCargado;
            slotActual = slotId;
            
            // Actualizar la interfaz con el tablero cargado
            actualizarTodasLasCeldasReveladas();
            actualizarInterfaz();
            
            // Reiniciar el juego para que funcione correctamente con la partida cargada
            primerClic = false;
            juegoIniciado = true;
            cronometroActivo = true;
            
            mostrarAlerta("Partida Cargada", "Se ha cargado la partida del slot " + slotId + ". Los cambios se guardarán automáticamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la partida: " + e.getMessage());
        }
    }


    private void limpiarSlot(int slotId, VBox slotsList, HBox slotItem) {
        try {
            // Mostrar confirmación antes de limpiar
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Limpieza");
            confirmacion.setHeaderText(null);
            
            String mensajeConfirmacion;
            if (this.slotActual != null && this.slotActual == slotId) {
                mensajeConfirmacion = "¿Estás seguro de que quieres limpiar este slot? Está actualmente cargado y se perderá el progreso.";
            } else {
                mensajeConfirmacion = "¿Estás seguro de que quieres limpiar este slot? Esta acción no se puede deshacer.";
            }
            
            confirmacion.setContentText(mensajeConfirmacion);
            
            // Configurar para que aparezca por encima de la ventana modal
            confirmacion.initModality(Modality.APPLICATION_MODAL);
            confirmacion.initOwner(gridTablero.getScene().getWindow());
            
            DialogPane dialogPane = confirmacion.getDialogPane();
            dialogPane.setStyle(EstilosUI.ESTILO_ALERTA);
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // Limpiar el slot en la base de datos
                slotService.limpiarSlot(slotId);
                
                // Eliminar de la interfaz
                slotsList.getChildren().remove(slotItem);
                
                // Si el slot limpiado es el que está cargado actualmente, resetear
                if (this.slotActual != null && this.slotActual == slotId) {
                    this.slotActual = null;
                    reiniciarJuego();
                }
                
                mostrarAlerta("Slot Limpiado", "El slot ha sido limpiado exitosamente.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo limpiar el slot: " + e.getMessage());
        }
    }
    

} 