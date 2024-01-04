package com.zeta.mywear;

public class Intervento {
    private String impianto;

    private String tipoIntervento;
    private String dataRichiesta;
    private String stato;
    private Integer id_stato;

    public Integer getId_intervento() {
        return id_intervento;
    }

    public void setId_intervento(Integer id_intervento) {
        this.id_intervento = id_intervento;
    }

    private Integer id_intervento;
    public String getTipoIntervento() {
        return tipoIntervento;
    }

    public void setTipoIntervento(String tipoIntervento) {
        this.tipoIntervento = tipoIntervento;
    }

    public String getImpianto() {
        return impianto;
    }

    public void setImpianto(String impianto) {
        this.impianto = impianto;
    }

    public String getDataRichiesta() {
        return dataRichiesta;
    }

    public void setDataRichiesta(String dataRichiesta) {
        this.dataRichiesta = dataRichiesta;
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
