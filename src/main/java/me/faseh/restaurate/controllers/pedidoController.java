package me.faseh.restaurate.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class pedidoController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int idPedido;
    private final int idCliente;
    private final LocalDateTime dataInicio;
    private LocalDateTime dataSaida;
    private final List<Integer> idsPratos = new ArrayList<>();
    private boolean isEntregue;

    public pedidoController(int idPedido, int idCliente, LocalDateTime dataInicio) {
        this.idPedido   = idPedido;
        this.idCliente  = idCliente;
        this.dataInicio = dataInicio;
        this.dataSaida  = null;
        this.isEntregue = false;
    }

    public int getIdPedido()                        { return idPedido; }
    public int getIdCliente()                       { return idCliente; }
    public LocalDateTime getDataInicio()            { return dataInicio; }
    public LocalDateTime getDataSaida()             { return dataSaida; }
    public List<Integer> getIdsPratos()             { return idsPratos; }
    public boolean getEntregue()                    { return isEntregue; }
    public String getFormDataInicio()               { return dataInicio.format(FMT); }
    public void fecharPedido(LocalDateTime momento) { this.dataSaida = momento; }
    public void setEntregue()                       { this.isEntregue = true; }
    public void addPrato(int idPrato)               { idsPratos.add(idPrato); }

    @Override
    public String toString() {
        String fimStr = (dataSaida == null) ? "Em aberto" : dataSaida.format(FMT);
        return String.format("[%d] Cliente #%d | Inicio: %s | Fim: %s | Pratos: %s | Entregue: %s", idPedido, idCliente, getFormDataInicio(), fimStr, idsPratos, isEntregue ? "Sim" : "Nao");
    }
}
