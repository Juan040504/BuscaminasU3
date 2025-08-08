package persistence;

import celda.TableroBuscaminas;

import java.sql.SQLException;
import java.util.List;


public class PartidaService {
    private final PartidaDAO partidaDAO;
    

    public PartidaService() {
        this.partidaDAO = new PartidaDAO();
    }
    

    public int guardarPartida(String nombreJugador, TableroBuscaminas tablero) {
        try {
            return partidaDAO.guardarPartida(nombreJugador, tablero);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la partida: " + e.getMessage(), e);
        }
    }
    

    public void actualizarPartida(int partidaId, TableroBuscaminas tablero) {
        try {
            partidaDAO.actualizarPartida(partidaId, tablero);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la partida: " + e.getMessage(), e);
        }
    }
    

    public TableroBuscaminas cargarPartida(int partidaId) {
        try {
            return partidaDAO.cargarPartida(partidaId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar la partida: " + e.getMessage(), e);
        }
    }
    

    public List<PartidaDAO.PartidaInfo> obtenerPartidasGuardadas() {
        try {
            return partidaDAO.obtenerPartidasGuardadas();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las partidas guardadas: " + e.getMessage(), e);
        }
    }
    

    public void eliminarPartida(int partidaId) {
        try {
            partidaDAO.eliminarPartida(partidaId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la partida: " + e.getMessage(), e);
        }
    }
    

    public boolean isDatabaseAvailable() {
        return DatabaseManager.getInstance().isDatabaseAvailable();
    }
} 