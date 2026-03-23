package model;

public class EskaeraProduktua {

    private int id;
    private int eskaeraId;
    private int produktuaId;
    private int kantitatea;
    private String egoera;
    private double prezioUnitarioa;
    private double guztira;

    public EskaeraProduktua() {
    }

    public EskaeraProduktua(int id, int eskaeraId, int produktuaId, int kantitatea,
                            String egoera, double prezioUnitarioa, double guztira) {
        this.id = id;
        this.eskaeraId = eskaeraId;
        this.produktuaId = produktuaId;
        this.kantitatea = kantitatea;
        this.egoera = egoera;
        this.prezioUnitarioa = prezioUnitarioa;
        this.guztira = guztira;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEskaeraId() { return eskaeraId; }
    public void setEskaeraId(int eskaeraId) { this.eskaeraId = eskaeraId; }

    public int getProduktuaId() { return produktuaId; }
    public void setProduktuaId(int produktuaId) { this.produktuaId = produktuaId; }

    public int getKantitatea() { return kantitatea; }
    public void setKantitatea(int kantitatea) { this.kantitatea = kantitatea; }

    public String getEgoera() { return egoera; }
    public void setEgoera(String egoera) { this.egoera = egoera; }

    public double getPrezioUnitarioa() { return prezioUnitarioa; }
    public void setPrezioUnitarioa(double prezioUnitarioa) { this.prezioUnitarioa = prezioUnitarioa; }

    public double getGuztira() { return guztira; }
    public void setGuztira(double guztira) { this.guztira = guztira; }
}
