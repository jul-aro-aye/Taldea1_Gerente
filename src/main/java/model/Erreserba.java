package model;

public class Erreserba {

    private int id;
    private int mahaiaId;
    private String bezeroaIzena;
    private String telefonoa;
    private java.sql.Date data;
    private String txanda;
    private int pertsonaKopurua;
    private String egoera;

    public Erreserba() {
    }

    public Erreserba(int id, int mahaiaId, String bezeroaIzena, String telefonoa,
                     java.sql.Date data, String txanda, int pertsonaKopurua, String egoera) {
        this.id = id;
        this.mahaiaId = mahaiaId;
        this.bezeroaIzena = bezeroaIzena;
        this.telefonoa = telefonoa;
        this.data = data;
        this.txanda = txanda;
        this.pertsonaKopurua = pertsonaKopurua;
        this.egoera = egoera;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMahaiaId() { return mahaiaId; }
    public void setMahaiaId(int mahaiaId) { this.mahaiaId = mahaiaId; }

    public String getBezeroaIzena() { return bezeroaIzena; }
    public void setBezeroaIzena(String bezeroaIzena) { this.bezeroaIzena = bezeroaIzena; }

    public String getTelefonoa() { return telefonoa; }
    public void setTelefonoa(String telefonoa) { this.telefonoa = telefonoa; }

    public java.sql.Date getData() { return data; }
    public void setData(java.sql.Date data) { this.data = data; }

    public String getTxanda() { return txanda; }
    public void setTxanda(String txanda) { this.txanda = txanda; }

    public int getPertsonaKopurua() { return pertsonaKopurua; }
    public void setPertsonaKopurua(int pertsonaKopurua) { this.pertsonaKopurua = pertsonaKopurua; }

    public String getEgoera() { return egoera; }
    public void setEgoera(String egoera) { this.egoera = egoera; }
}
