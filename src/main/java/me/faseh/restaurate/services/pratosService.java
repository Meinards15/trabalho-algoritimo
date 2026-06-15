package me.faseh.restaurate.services;

import me.faseh.restaurate.controllers.pratosController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class pratosService {

    private final Connection con;
    private final databaseService db;

    public pratosService() {
        this.db   = databaseService.getInstance();
        this.con = db.getCon();

        if (db.delTable("pratos")) {
            semearCardapioPadrao();
        }
    }

    public pratosController adicionarPrato(String nome, double preco, int tempoMinutos) {
        String sql = "INSERT INTO pratos (nome, preco, tempo_minutos) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setDouble(2, preco);
            ps.setInt(3, tempoMinutos);
            ps.executeUpdate();

            int id = ps.getGeneratedKeys().getInt(1);
            return new pratosController(id, nome, preco, tempoMinutos);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar prato: " + e.getMessage(), e);
        }
    }

    public List<pratosController> listarTodos() {
        List<pratosController> lista = new ArrayList<>();

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT id, nome, preco, tempo_minutos FROM pratos ORDER BY id")) {

            while (rs.next()) lista.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pratos: " + e.getMessage(), e);
        }

        return lista;
    }

    public Optional<pratosController> buscarPorId(int id) {
        String sql = "SELECT id, nome, preco, tempo_minutos FROM pratos WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar prato: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean remover(int id) {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM pratos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover prato: " + e.getMessage(), e);
        }
    }

    private pratosController mapRow(ResultSet rs) throws SQLException {
        return new pratosController(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDouble("preco"),
                rs.getInt("tempo_minutos")
        );
    }

    private void semearCardapioPadrao() {
        adicionarPrato("Arroz, Feijao & Frango Grelhado",   32.50, 15);
        adicionarPrato("Arroz & File de Tilapia",   38.00, 20);
        adicionarPrato("Macarrao Alho e Oleo", 27.00, 12);
        adicionarPrato("Arroz, Farofa & Picanha",  65.00, 25);
        adicionarPrato("Lasnha Bolonhesa",   18.00,  8);
        adicionarPrato("Suco Natural",       9.50,  3);
        adicionarPrato("Refrigerante",       7.00,  2);
        adicionarPrato("Agua Mineral",       4.00,  1);
    }
}
