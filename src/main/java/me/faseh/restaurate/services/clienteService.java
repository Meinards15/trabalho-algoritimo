package me.faseh.restaurate.services;

import me.faseh.restaurate.controllers.clienteController;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class clienteService {

    private final Connection con;

    public clienteService() {
        this.con = dbService.getInstance().getCon();
    }

    public clienteController registrarCliente(String nome) {
        String sql = "INSERT INTO clientes (nome, data_entrada) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, LocalDate.now().toString());
            ps.executeUpdate();
            int id = ps.getGeneratedKeys().getInt(1);
            return new clienteController(id, nome, LocalDate.now());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar cliente: " + e.getMessage(), e);
        }
    }

    public List<clienteController> listarTodos() {
        List<clienteController> lista = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome, data_entrada FROM clientes ORDER BY id")) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<clienteController> buscarPorId(int id) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id, nome, data_entrada FROM clientes WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<clienteController> buscarPorNome(String nome) {
        String sql = "SELECT id, nome, data_entrada FROM clientes WHERE nome = ? COLLATE NOCASE LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por nome: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean remover(int id) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM clientes WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    private clienteController mapRow(ResultSet rs) throws SQLException {
        return new clienteController(
                rs.getInt("id"),
                rs.getString("nome"),
                LocalDate.parse(rs.getString("data_entrada"))
        );
    }
}
