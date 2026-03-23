package model;

public class Faktura {

    private int id;
    private int eskaeraId;
    private String pdfIzena;
    private java.sql.Timestamp data;
    private Double guztira;

    public Faktura() {
    }

    public Faktura(int id, int eskaeraId, String pdfIzena, java.sql.Timestamp data, Double guztira) {
        this.id = id;
        this.eskaeraId = eskaeraId;
        this.pdfIzena = pdfIzena;
        this.data = data;
        this.guztira = guztira;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEskaeraId() { return eskaeraId; }
    public void setEskaeraId(int eskaeraId) { this.eskaeraId = eskaeraId; }

    public String getPdfIzena() { return pdfIzena; }
    public void setPdfIzena(String pdfIzena) { this.pdfIzena = pdfIzena; }

    public java.sql.Timestamp getData() { return data; }
    public void setData(java.sql.Timestamp data) { this.data = data; }

    public Double getGuztira() { return guztira; }
    public void setGuztira(Double guztira) { this.guztira = guztira; }
}
