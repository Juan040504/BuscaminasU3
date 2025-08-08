package persistence;

import celda.TableroBuscaminas;

import java.sql.SQLException;
import java.util.List;

public class SlotGuardadoService {
    
    private SlotGuardadoDAO slotDAO;
    
    public SlotGuardadoService() {
        this.slotDAO = new SlotGuardadoDAO();
    }
    
    /**
     * Guarda una partida en un slot específico
     * @param slotId ID del slot (1, 2, o 3)
     * @param nombrePartida Nombre personalizado de la partida
     * @param nombreJugador Nombre del jugador
     * @param tablero Tablero del juego
     * @throws Exception si hay error al guardar
     */
    public void guardarEnSlot(int slotId, String nombrePartida, String nombreJugador, TableroBuscaminas tablero) throws Exception {
        try {
            slotDAO.guardarEnSlot(slotId, nombrePartida, nombreJugador, tablero);
        } catch (SQLException e) {
            throw new Exception("Error al guardar en slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga una partida desde un slot específico
     * @param slotId ID del slot (1, 2, o 3)
     * @return Tablero cargado
     * @throws Exception si hay error al cargar
     */
    public TableroBuscaminas cargarDesdeSlot(int slotId) throws Exception {
        try {
            return slotDAO.cargarDesdeSlot(slotId);
        } catch (SQLException e) {
            throw new Exception("Error al cargar desde slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene información de todos los slots
     * @return Lista con información de los slots
     * @throws Exception si hay error al obtener la información
     */
    public List<SlotGuardadoDAO.SlotInfo> obtenerInfoSlots() throws Exception {
        try {
            return slotDAO.obtenerInfoSlots();
        } catch (SQLException e) {
            throw new Exception("Error al obtener información de slots: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si un slot está ocupado
     * @param slotId ID del slot
     * @return true si está ocupado, false si está vacío
     * @throws Exception si hay error al verificar
     */
    public boolean slotEstaOcupado(int slotId) throws Exception {
        try {
            return slotDAO.slotEstaOcupado(slotId);
        } catch (SQLException e) {
            throw new Exception("Error al verificar slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Limpia un slot (lo marca como vacío)
     * @param slotId ID del slot
     * @throws Exception si hay error al limpiar
     */
    public void limpiarSlot(int slotId) throws Exception {
        try {
            slotDAO.limpiarSlot(slotId);
        } catch (SQLException e) {
            throw new Exception("Error al limpiar slot " + slotId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si la base de datos está disponible
     * @return true si está disponible, false en caso contrario
     */
    public boolean isDatabaseAvailable() {
        return DatabaseManager.getInstance().isDatabaseAvailable();
    }
}

