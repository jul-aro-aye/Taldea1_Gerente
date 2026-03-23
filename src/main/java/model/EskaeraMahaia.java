package model;

public class EskaeraMahaia {

    private int id;
    private int eskaeraId;
    private int mahaiaId;

    public EskaeraMahaia() {
    }

    public EskaeraMahaia(int id, int eskaeraId, int mahaiaId) {
        this.id = id;
        this.eskaeraId = eskaeraId;
        this.mahaiaId = mahaiaId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEskaeraId() { return eskaeraId; }
    public void setEskaeraId(int eskaeraId) { this.eskaeraId = eskaeraId; }

    public int getMahaiaId() { return mahaiaId; }
    public void setMahaiaId(int mahaiaId) { this.mahaiaId = mahaiaId; }
}
