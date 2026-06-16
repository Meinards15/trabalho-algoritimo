package me.faseh.restaurate.controllers;

public class pratosController {

    private final int idPrato;
    private final String nomePrato;
    private final double precoVal;
    private final int dataMin;

    public pratosController(int idPrato, String nomePrato, double precoVal, int dataMin) {
        this.idPrato    = idPrato;
        this.nomePrato  = nomePrato;
        this.precoVal   = precoVal;
        this.dataMin    = dataMin;
    }

    public int getIdPrato()         { return idPrato; }
    public String getNomePrato()    { return nomePrato; }
    public double getPrecoVal()     { return precoVal; }
    public int getTempo()           { return dataMin; }

    @Override
    public String toString() {
        return String.format("[%d] %-30s R$ %6.2f  ~%d min", idPrato, nomePrato, precoVal, dataMin);
    }

    public String toStringSemId() {
        return String.format("%-30s  R$ %6.2f  ~%d min", getNomePrato(), getPrecoVal(), getTempo());
    }
}
