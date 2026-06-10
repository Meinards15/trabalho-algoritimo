package me.faseh.restaurate.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class clienteController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int idCliente;
    private final String nomeCliente;
    private final LocalDate dataEntrada;

    public clienteController(int idCliente, String nomeCliente, LocalDate dataEntrada) {
        this.idCliente      = idCliente;
        this.nomeCliente    = nomeCliente;
        this.dataEntrada    = dataEntrada;
    }

    public int getIdCliente()           { return idCliente; }
    public String getNomeCliente()      { return nomeCliente; }
    public LocalDate getDataEntrada()   { return dataEntrada; }

    public String getFormData() {
        return dataEntrada.format(FMT);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s — desde %s", idCliente, nomeCliente, getFormData());
    }
}
