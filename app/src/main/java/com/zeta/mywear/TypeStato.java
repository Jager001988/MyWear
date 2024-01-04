package com.zeta.mywear;

public enum TypeStato {
    APERTO(0, "APERTO"),
    IN_CORSO(1, "IN CORSO"),
    CHIUSO(2, "CHIUSO");

    private Integer id;
    private String description;

    private TypeStato(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static TypeStato fromInt(Integer id) {
        for (TypeStato value : TypeStato.values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        return null;
    }
}
