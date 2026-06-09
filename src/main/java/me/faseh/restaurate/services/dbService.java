package me.faseh.restaurate.services;

import java.nio.file.*;
import java.sql.*;

public class dbService {

    private static final String DB_PATH = "data/restaurarte.db";
    private static dbService instance;
    private final Connection con;

    private dbService() {
        try {
            Files.createDirectories(Paths.get("data"));
            con = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            con.createStatement().execute("PRAGMA foreign_keys = ON");
            setupTable();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar banco: " + e.getMessage(), e);
        }
    }

    public static dbService getInstance() {
        if (instance == null) instance = new dbService();
        return instance;
    }

    public Connection getCon() { return con; }

    private void setupTable() throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS clientes (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome         TEXT    NOT NULL,
                    data_entrada TEXT    NOT NULL
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS pratos (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome          TEXT    NOT NULL,
                    preco         REAL    NOT NULL,
                    tempo_minutos INTEGER NOT NULL
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS pedidos (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_cliente INTEGER NOT NULL REFERENCES clientes(id),
                    inicio     TEXT    NOT NULL,
                    fim        TEXT,
                    entregue   INTEGER NOT NULL DEFAULT 0
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS pedido_pratos (
                    id_pedido INTEGER NOT NULL REFERENCES pedidos(id),
                    id_prato  INTEGER NOT NULL REFERENCES pratos(id),
                    posicao   INTEGER NOT NULL
                )
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS avaliacoes (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_cliente   INTEGER NOT NULL REFERENCES clientes(id),
                    nome_cliente TEXT    NOT NULL,
                    nota         INTEGER NOT NULL,
                    comentario   TEXT    DEFAULT '',
                    data         TEXT    NOT NULL
                )
            """);
        }
    }

    public boolean delTable(String tabela) {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true;
        }
    }
}
