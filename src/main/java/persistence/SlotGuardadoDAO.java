package persistence;

import celda.Celda;
import celda.TableroBuscaminas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SlotGuardadoDAO {
    

    public void guardarEnSlot(int slotId, String nombrePartida, String nombreJugador, TableroBuscaminas tablero) throws SQLException {
        String sqlSlot = "UPDATE slots_guardado SET nombre_partida = ?, nombre_jugador = ?, " +
                        "cantidad_minas = ?, celdas_reveladas = ?, minas_marcadas = ?, " +
                        "juego_terminado = ?, juego_ganado = ?, estado_partida = ?, " +
                        "fecha_ultima_modificacion = CURRENT_TIMESTAMP WHERE slot_id = ?";
        
        String sqlCelda = "INSERT OR REPLACE INTO celdas_slots (slot_id, fila, columna, es_mina, " +
                         "esta_revelada, esta_marcada, minas_adyacentes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psSlot = conn.prepareStatement(sqlSlot);
             PreparedStatement psCelda = conn.prepareStatement(sqlCelda)) {
            
            conn.setAutoCommit(false);
            
            // Actualizar información del slot
            psSlot.setString(1, nombrePartida);
            psSlot.setString(2, nombreJugador);
            psSlot.setInt(3, tablero.getCantidadMinas());
            psSlot.setInt(4, tablero.getCeldasReveladas());
            psSlot.setInt(5, tablero.getMinasMarcadas());
            psSlot.setBoolean(6, tablero.isJuegoTerminado());
            psSlot.setBoolean(7, tablero.isJuegoGanado());
            psSlot.setString(8, tablero.isJuegoTerminado() ? "terminada" : "en_curso");
            psSlot.setInt(9, slotId);
            
            int filasActualizadas = psSlot.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("Slot " + slotId + " no encontrado");
            }
            
            // Limpiar celdas existentes del slot
            String deleteCeldas = "DELETE FROM celdas_slots WHERE slot_id = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteCeldas)) {
                psDelete.setInt(1, slotId);
                psDelete.executeUpdate();
            }
            
            // Insertar celdas del tablero
            for (int fila = 0; fila < tablero.getTamano(); fila++) {
                for (int columna = 0; columna < tablero.getTamano(); columna++) {
                    Celda celda = tablero.getCelda(fila, columna);
                    
                    psCelda.setInt(1, slotId);
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
            
        } catch (SQLException e) {
            throw new SQLException("Error al guardar en slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    

    public TableroBuscaminas cargarDesdeSlot(int slotId) throws SQLException {
        String sqlSlot = "SELECT * FROM slots_guardado WHERE slot_id = ?";
        String sqlCeldas = "SELECT * FROM celdas_slots WHERE slot_id = ? ORDER BY fila, columna";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psSlot = conn.prepareStatement(sqlSlot);
             PreparedStatement psCeldas = conn.prepareStatement(sqlCeldas)) {
            
            psSlot.setInt(1, slotId);
            psCeldas.setInt(1, slotId);
            
            try (ResultSet rsSlot = psSlot.executeQuery();
                 ResultSet rsCeldas = psCeldas.executeQuery()) {
                
                if (!rsSlot.next()) {
                    throw new SQLException("Slot " + slotId + " no encontrado");
                }
                
                // Verificar si el slot está vacío
                String nombreJugador = rsSlot.getString("nombre_jugador");
                if ("Vacío".equals(nombreJugador)) {
                    throw new SQLException("El slot " + slotId + " está vacío");
                }
                
                // Crear tablero con la cantidad de minas guardada
                int cantidadMinas = rsSlot.getInt("cantidad_minas");
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
            throw new SQLException("Error al cargar desde slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    

    public List<SlotInfo> obtenerInfoSlots() throws SQLException {
        String sql = "SELECT * FROM slots_guardado ORDER BY slot_id";
        
        List<SlotInfo> slots = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SlotInfo info = new SlotInfo();
                info.setSlotId(rs.getInt("slot_id"));
                info.setNombrePartida(rs.getString("nombre_partida"));
                info.setNombreJugador(rs.getString("nombre_jugador"));
                info.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                info.setFechaUltimaModificacion(rs.getTimestamp("fecha_ultima_modificacion"));
                info.setCantidadMinas(rs.getInt("cantidad_minas"));
                info.setCeldasReveladas(rs.getInt("celdas_reveladas"));
                info.setMinasMarcadas(rs.getInt("minas_marcadas"));
                info.setJuegoTerminado(rs.getBoolean("juego_terminado"));
                info.setJuegoGanado(rs.getBoolean("juego_ganado"));
                info.setEstadoPartida(rs.getString("estado_partida"));
                
                slots.add(info);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener información de slots: " + e.getMessage(), e);
        }
        
        return slots;
    }
    

    public boolean slotEstaOcupado(int slotId) throws SQLException {
        String sql = "SELECT nombre_jugador FROM slots_guardado WHERE slot_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, slotId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreJugador = rs.getString("nombre_jugador");
                    return !"Vacío".equals(nombreJugador);
                }
                return false;
            }
        } catch (SQLException e) {
            throw new SQLException("Error al verificar slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    

    public void limpiarSlot(int slotId) throws SQLException {
        String sqlSlot = "UPDATE slots_guardado SET nombre_partida = ?, nombre_jugador = ?, " +
                        "cantidad_minas = 60, celdas_reveladas = 0, minas_marcadas = 0, " +
                        "juego_terminado = 0, juego_ganado = 0, estado_partida = 'en_curso', " +
                        "fecha_ultima_modificacion = CURRENT_TIMESTAMP WHERE slot_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement psSlot = conn.prepareStatement(sqlSlot)) {
            
            conn.setAutoCommit(false);
            
            // Limpiar información del slot
            psSlot.setString(1, "Slot " + slotId);
            psSlot.setString(2, "Vacío");
            psSlot.setInt(3, slotId);
            
            int filasActualizadas = psSlot.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("Slot " + slotId + " no encontrado");
            }
            
            // Eliminar celdas del slot
            String deleteCeldas = "DELETE FROM celdas_slots WHERE slot_id = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteCeldas)) {
                psDelete.setInt(1, slotId);
                psDelete.executeUpdate();
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            throw new SQLException("Error al limpiar slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    

    public static class SlotInfo {
        private int slotId;
        private String nombrePartida;
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
        public int getSlotId() { return slotId; }
        public void setSlotId(int slotId) { this.slotId = slotId; }
        
        public String getNombrePartida() { return nombrePartida; }
        public void setNombrePartida(String nombrePartida) { this.nombrePartida = nombrePartida; }
        
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
        
        public boolean estaOcupado() {
            return !"Vacío".equals(nombreJugador);
        }
        
        @Override
        public String toString() {
            if (estaOcupado()) {
                return String.format("Slot %d - %s (%s)", slotId, nombrePartida, estadoPartida);
            } else {
                return String.format("Slot %d - Vacío", slotId);
            }
        }
    }
}

