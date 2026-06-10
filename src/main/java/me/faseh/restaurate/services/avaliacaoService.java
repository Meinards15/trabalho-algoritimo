package me.faseh.restaurate.services;

import me.faseh.restaurate.controllers.avaliacaoController;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class avaliacaoService {

    private final Connection con;

    public avaliacaoService() {
        this.con = dbService.getInstance().getCon();
    }

    public avaliacaoController registrar(int idCliente, String nomeCliente, int nota, String comentario) {
        if (nota < 0 || nota > 5) throw new IllegalArgumentException("Nota deve ser de 0 a 5.");
        String sql = "INSERT INTO avaliacoes (id_cliente, nome_cliente, nota, comentario, data) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime agora = LocalDateTime.now();
            ps.setInt(1, idCliente);
            ps.setString(2, nomeCliente);
            ps.setInt(3, nota);
            ps.setString(4, comentario == null ? "" : comentario);
            ps.setString(5, agora.toString());
            ps.executeUpdate();
            int id = ps.getGeneratedKeys().getInt(1);
            return new avaliacaoController(id, idCliente, nomeCliente, nota, comentario, agora);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar avaliacao: " + e.getMessage(), e);
        }
    }

    public List<avaliacaoController> listarTodas() {
        List<avaliacaoController> lista = new ArrayList<>();
        String sql = "SELECT id, id_cliente, nome_cliente, nota, comentario, data FROM avaliacoes ORDER BY id DESC";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar avaliacoes: " + e.getMessage(), e);
        }
        return lista;
    }

    public OptionalDouble calcularMedia() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT AVG(nota) FROM avaliacoes")) {
            double avg = rs.getDouble(1);
            if (rs.wasNull()) return OptionalDouble.empty();
            return OptionalDouble.of(avg);
        } catch (SQLException e) {
            return OptionalDouble.empty();
        }
    }

    private avaliacaoController mapRow(ResultSet rs) throws SQLException {
        return new avaliacaoController(
                rs.getInt("id"),
                rs.getInt("id_cliente"),
                rs.getString("nome_cliente"),
                rs.getInt("nota"),
                rs.getString("comentario"),
                LocalDateTime.parse(rs.getString("data"))
        );
    }
}
