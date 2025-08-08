package persistence;

import celda.Celda;
import celda.TableroBuscaminas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PartidaDAO {
    

    public int guardarPartida(String nombreJugador, TableroBuscaminas tablero) throws SQLException {
        String sqlPartida = "INSERT INTO partidas (nombre_jugador, cantidad_minas, celdas_reveladas, " +
                           "minas_marcadas, juego_terminado, juego_ganado, estado_partida) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        String sqlCelda = "INSERT INTO celdas_partida (partida_id, fila, columna, es_mina, " +
                         "esta_revelada, esta_marcada, minas_adyacentes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psPartida = conn.prepareStatement(sqlPartida, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psCelda = conn.prepareStatement(sqlCelda)) {
            
            conn.setAutoCommit(false);
            
            // Insertar partida
            psPartida.setString(1, nombreJugador);
            psPartida.setInt(2, tablero.getCantidadMinas());
            psPartida.setInt(3, tablero.getCeldasReveladas());
            psPartida.setInt(4, tablero.getMinasMarcadas());
            psPartida.setBoolean(5, tablero.isJuegoTerminado());
            psPartida.setBoolean(6, tablero.isJuegoGanado());
            psPartida.setString(7, tablero.isJuegoTerminado() ? "terminada" : "en_curso");
            
            psPartida.executeUpdate();
            
            // Obtener ID de la partida
            int partidaId;
            try (ResultSet rs = psPartida.getGeneratedKeys()) {
                if (rs.next()) {
                    partidaId = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la partida");
                }
            }
            
            // Insertar celdas
            for (int fila = 0; fila < tablero.getTamano(); fila++) {
                for (int columna = 0; columna < tablero.getTamano(); columna++) {
                    Celda celda = tablero.getCelda(fila, columna);
                    
                    psCelda.setInt(1, partidaId);
                    psCelda.setInt(2, fila);
                    psCelda.setInt(3, columna);
                    psCelda.setBoolean(4, celda.esMina());
                    psCelda.setBoolean(5, celda.estaRevelada());
                    psCelda.setBoolean(6, celda.estaMarcada());
                    psCelda.setInt(7, celda.getMinasAdyacentes());
                    
                    psCelda.executeUpdate();
                }
            }
            
            conn.commit();
            return partidaId;
            
        } catch (SQLException e) {
            throw new SQLException("Error al guardar la partida: " + e.getMessage(), e);
        }
    }
    

    public void actualizarPartida(int partidaId, TableroBuscaminas tablero) throws SQLException {
        String sqlPartida = "UPDATE partidas SET celdas_reveladas = ?, minas_marcadas = ?, " +
                           "juego_terminado = ?, juego_ganado = ?, estado_partida = ? WHERE id = ?";
        
        String sqlCelda = "UPDATE celdas_partida SET esta_revelada = ?, esta_marcada = ? " +
                         "WHERE partida_id = ? AND fila = ? AND columna = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psPartida = conn.prepareStatement(sqlPartida);
             PreparedStatement psCelda = conn.prepareStatement(sqlCelda)) {
            
            conn.setAutoCommit(false);
            
            // Actualizar partida
            psPartida.setInt(1, tablero.getCeldasReveladas());
            psPartida.setInt(2, tablero.getMinasMarcadas());
            psPartida.setBoolean(3, tablero.isJuegoTerminado());
            psPartida.setBoolean(4, tablero.isJuegoGanado());
            psPartida.setString(5, tablero.isJuegoTerminado() ? "terminada" : "en_curso");
            psPartida.setInt(6, partidaId);
            
            psPartida.executeUpdate();
            
            // Actualizar celdas
            for (int fila = 0; fila < tablero.getTamano(); fila++) {
                for (int columna = 0; columna < tablero.getTamano(); columna++) {
                    Celda celda = tablero.getCelda(fila, columna);
                    
                    psCelda.setBoolean(1, celda.estaRevelada());
                    psCelda.setBoolean(2, celda.estaMarcada());
                    psCelda.setInt(3, partidaId);
                    psCelda.setInt(4, fila);
                    psCelda.setInt(5, columna);
                    
                    psCelda.executeUpdate();
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar la partida: " + e.getMessage(), e);
        }
    }
    

    public TableroBuscaminas cargarPartida(int partidaId) throws SQLException {
        String sqlPartida = "SELECT * FROM partidas WHERE id = ?";
        String sqlCeldas = "SELECT * FROM celdas_partida WHERE partida_id = ? ORDER BY fila, columna";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psPartida = conn.prepareStatement(sqlPartida);
             PreparedStatement psCeldas = conn.prepareStatement(sqlCeldas)) {
            
            psPartida.setInt(1, partidaId);
            psCeldas.setInt(1, partidaId);
            
            try (ResultSet rsPartida = psPartida.executeQuery();
                 ResultSet rsCeldas = psCeldas.executeQuery()) {
                
                if (!rsPartida.next()) {
                    throw new SQLException("Partida no encontrada con ID: " + partidaId);
                }
                
                // Crear tablero con la cantidad de minas guardada
                int cantidadMinas = rsPartida.getInt("cantidad_minas");
                TableroBuscaminas tablero = new TableroBuscaminas(cantidadMinas);
                
                // Cargar estado de las celdas
                while (rsCeldas.next()) {
                    int fila = rsCeldas.getInt("fila");
                    int columna = rsCeldas.getInt("columna");
                    boolean esMina = rsCeldas.getBoolean("es_mina");
                    boolean estaRevelada = rsCeldas.getBoolean("esta_revelada");
                    boolean estaMarcada = rsCeldas.getBoolean("esta_marcada");
                    int minasAdyacentes = rsCeldas.getInt("minas_adyacentes");
                    
                    Celda celda = tablero.getCelda(fila, columna);
                    
                    if (esMina) {
                        celda.establecerComoMina();
                    }
                    
                    if (estaRevelada) {
                        celda.revelar();
                    }
                    
                    if (estaMarcada) {
                        celda.alternarMarcado();
                    }
                    
                    // Establecer minas adyacentes
                    for (int i = 0; i < minasAdyacentes; i++) {
                        celda.incrementarMinasAdyacentes();
                    }
                }
                
                return tablero;
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cargar la partida: " + e.getMessage(), e);
        }
    }
    

    public List<PartidaInfo> obtenerPartidasGuardadas() throws SQLException {
        String sql = "SELECT id, nombre_jugador, fecha_creacion, fecha_ultima_modificacion, " +
                    "cantidad_minas, celdas_reveladas, minas_marcadas, juego_terminado, " +
                    "juego_ganado, estado_partida FROM partidas ORDER BY fecha_ultima_modificacion DESC";
        
        List<PartidaInfo> partidas = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PartidaInfo info = new PartidaInfo();
                info.setId(rs.getInt("id"));
                info.setNombreJugador(rs.getString("nombre_jugador"));
                info.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                info.setFechaUltimaModificacion(rs.getTimestamp("fecha_ultima_modificacion"));
                info.setCantidadMinas(rs.getInt("cantidad_minas"));
                info.setCeldasReveladas(rs.getInt("celdas_reveladas"));
                info.setMinasMarcadas(rs.getInt("minas_marcadas"));
                info.setJuegoTerminado(rs.getBoolean("juego_terminado"));
                info.setJuegoGanado(rs.getBoolean("juego_ganado"));
                info.setEstadoPartida(rs.getString("estado_partida"));
                
                partidas.add(info);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener las partidas guardadas: " + e.getMessage(), e);
        }
        
        return partidas;
    }
    

    public void eliminarPartida(int partidaId) throws SQLException {
        String sql = "DELETE FROM partidas WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, partidaId);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la partida con ID: " + partidaId);
            }
            
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar la partida: " + e.getMessage(), e);
        }
    }
    

    public static class PartidaInfo {
        private int id;
        private String nombreJugador;
        private Timestamp fechaCreacion;
        private Timestamp fechaUltimaModificacion;
        private int cantidadMinas;
        private int celdasReveladas;
        private int minasMarcadas;
        private boolean juegoTerminado;
        private boolean juegoGanado;
        private String estadoPartida;
        
        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getNombreJugador() { return nombreJugador; }
        public void setNombreJugador(String nombreJugador) { this.nombreJugador = nombreJugador; }
        
        public Timestamp getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
        
        public Timestamp getFechaUltimaModificacion() { return fechaUltimaModificacion; }
        public void setFechaUltimaModificacion(Timestamp fechaUltimaModificacion) { this.fechaUltimaModificacion = fechaUltimaModificacion; }
        
        public int getCantidadMinas() { return cantidadMinas; }
        public void setCantidadMinas(int cantidadMinas) { this.cantidadMinas = cantidadMinas; }
        
        public int getCeldasReveladas() { return celdasReveladas; }
        public void setCeldasReveladas(int celdasReveladas) { this.celdasReveladas = celdasReveladas; }
        
        public int getMinasMarcadas() { return minasMarcadas; }
        public void setMinasMarcadas(int minasMarcadas) { this.minasMarcadas = minasMarcadas; }
        
        public boolean isJuegoTerminado() { return juegoTerminado; }
        public void setJuegoTerminado(boolean juegoTerminado) { this.juegoTerminado = juegoTerminado; }
        
        public boolean isJuegoGanado() { return juegoGanado; }
        public void setJuegoGanado(boolean juegoGanado) { this.juegoGanado = juegoGanado; }
        
        public String getEstadoPartida() { return estadoPartida; }
        public void setEstadoPartida(String estadoPartida) { this.estadoPartida = estadoPartida; }
        
        @Override
        public String toString() {
            return String.format("Partida %d - %s (%s)", id, nombreJugador, estadoPartida);
        }
    }
} 