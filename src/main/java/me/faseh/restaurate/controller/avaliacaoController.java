package me.faseh.restaurate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class avaliacaoController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int idAval;
    private final int idCliente;
    private final String nomeCliente;
    private final int notaVal;
    private final String descString;
    private final LocalDateTime dataAval;

    public avaliacaoController(int idAval, int idCliente, String nomeCliente, int notaVal, String descString, LocalDateTime dataAval) {
        this.idAval         = idAval;
        this.idCliente      = idCliente;
        this.nomeCliente    = nomeCliente;
        this.notaVal        = notaVal;
        this.descString     = (descString == null) ? "" : descString;
        this.dataAval       = dataAval;
    }

    public int getIdAval()              { return idAval; }
    public int getIdCliente()           { return idCliente; }
    public String getNomeCliente()      { return nomeCliente; }
    public int getNotaVal()             { return notaVal; }
    public String getDescString()       { return descString; }
    public LocalDateTime getDataAval()  { return dataAval; }

    public String getAval() {
        return "★".repeat(notaVal) + "☆".repeat(5 - notaVal);
    }

    @Override
    public String toString() {
        String coment = descString.isEmpty() ? "(sem comentario)" : "\"" + descString + "\"";
        return String.format("%-20s %s (%d/5)  %s  %s", nomeCliente, getAval(), notaVal, coment, dataAval.format(FMT));
    }
}
