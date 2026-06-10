package me.faseh.restaurate.services;

import me.faseh.restaurate.controllers.pedidoController;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class pedidoService {

    private final Connection con;

    public pedidoService() {
        this.con = dbService.getInstance().getCon();
    }

    public pedidoController abrirPedido(int idCliente) {
        String sql = "INSERT INTO pedidos (id_cliente, inicio, entregue) VALUES (?, ?, 0)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime agora = LocalDateTime.now();
            ps.setInt(1, idCliente);
            ps.setString(2, agora.toString());
            ps.executeUpdate();

            int id = ps.getGeneratedKeys().getInt(1);
            return new pedidoController(id, idCliente, agora);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao abrir pedido: " + e.getMessage(), e);
        }
    }

    public boolean adicionarPrato(int idPedido, int idPrato) {
        Optional<pedidoController> opt = buscarPorId(idPedido);
        if (opt.isEmpty() || opt.get().getDataSaida() != null) return false;

        // Conta quantos itens já existem para determinar a posição do novo
        int proximaPosicao = contarItensDoPedido(idPedido) + 1;

        String sql = "INSERT INTO pedido_pratos (id_pedido, id_prato, posicao) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idPrato);
            ps.setInt(3, proximaPosicao);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar prato ao pedido: " + e.getMessage(), e);
        }
    }

    public boolean fecharPedido(int idPedido) {
        String sql = "UPDATE pedidos SET fim = ? WHERE id = ? AND fim IS NULL";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().toString());
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fechar pedido: " + e.getMessage(), e);
        }
    }

    public boolean marcarEntregue(int idPedido) {
        String sql = "UPDATE pedidos SET entregue = 1 WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar pedido como entregue: " + e.getMessage(), e);
        }
    }

    public Optional<pedidoController> buscarPorId(int id) {
        String sql = "SELECT id, id_cliente, inicio, fim, entregue FROM pedidos WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pedidoController p = mapRow(rs);
                carregarPratos(p);
                return Optional.of(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<pedidoController> listarTodos() {
        return listarComSQL(
                "SELECT id, id_cliente, inicio, fim, entregue FROM pedidos ORDER BY id");
    }

    public List<pedidoController> listarAbertos() {
        return listarComSQL(
                "SELECT id, id_cliente, inicio, fim, entregue FROM pedidos WHERE fim IS NULL ORDER BY id");
    }

    public List<pedidoController> listarPorCliente(int idCliente) {
        List<pedidoController> lista = new ArrayList<>();
        String sql = "SELECT id, id_cliente, inicio, fim, entregue FROM pedidos WHERE id_cliente = ? ORDER BY id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pedidoController p = mapRow(rs);
                carregarPratos(p);
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do cliente: " + e.getMessage(), e);
        }

        return lista;
    }

    private List<pedidoController> listarComSQL(String sql) {
        List<pedidoController> lista = new ArrayList<>();
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                pedidoController p = mapRow(rs);
                carregarPratos(p);
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    private void carregarPratos(pedidoController p) throws SQLException {
        String sql = "SELECT id_prato FROM pedido_pratos WHERE id_pedido = ? ORDER BY posicao";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPedido());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.addPrato(rs.getInt("id_prato"));
            }
        }
    }

    private int contarItensDoPedido(int idPedido) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM pedido_pratos WHERE id_pedido = ?")) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    private pedidoController mapRow(ResultSet rs) throws SQLException {
        int id             = rs.getInt("id");
        int idCliente      = rs.getInt("id_cliente");
        LocalDateTime inicio = LocalDateTime.parse(rs.getString("inicio"));

        String fimStr      = rs.getString("fim");
        LocalDateTime fim  = (fimStr == null) ? null : LocalDateTime.parse(fimStr);

        boolean entregue   = rs.getInt("entregue") == 1;

        pedidoController p = new pedidoController(id, idCliente, inicio);
        if (fim != null) p.fecharPedido(fim);
        if (entregue)    p.setEntregue();
        return p;
    }
}
