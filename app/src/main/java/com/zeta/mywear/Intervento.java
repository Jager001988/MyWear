package com.zeta.mywear;

public class Intervento {
    private String impianto;

    private String tipo_intervento;
    private String data_richiesta;
    private String stato;
    private Integer id_stato;

    public Integer getId_intervento() {
        return id_intervento;
    }

    public void setId_intervento(Integer id_intervento) {
        this.id_intervento = id_intervento;
    }

    private Integer id_intervento;
    public String getTipo_intervento() {
        return tipo_intervento;
    }

    public void setTipo_intervento(String tipo_intervento) {
        this.tipo_intervento = tipo_intervento;
    }

    public String getImpianto() {
        return impianto;
    }

    public void setImpianto(String impianto) {
        this.impianto = impianto;
    }

    public String getData_richiesta() {
        return data_richiesta;
    }

    public void setData_richiesta(String data_richiesta) {
        this.data_richiesta = data_richiesta;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Integer getId_stato() {
        return id_stato;
    }

    public void setId_stato(Integer id_stato) {
        this.id_stato = id_stato;
    }
}
