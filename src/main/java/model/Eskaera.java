package model;

public class Eskaera {

    private int id;
    private Integer mahaiaId;
    private int erabiltzaileId;
    private Integer komensalak;
    private String egoera;
    private String sukaldeaEgoera;
    private java.sql.Timestamp sortzeData;
    private java.sql.Timestamp itxieraData;
    private Integer erreserbaId;

    public Eskaera() {
    }

    public Eskaera(int id, Integer mahaiaId, int erabiltzaileId, Integer komensalak, String egoera,
                   String sukaldeaEgoera, java.sql.Timestamp sortzeData,
                   java.sql.Timestamp itxieraData, Integer erreserbaId) {
        this.id = id;
        this.mahaiaId = mahaiaId;
        this.erabiltzaileId = erabiltzaileId;
        this.komensalak = komensalak;
        this.egoera = egoera;
        this.sukaldeaEgoera = sukaldeaEgoera;
        this.sortzeData = sortzeData;
        this.itxieraData = itxieraData;
        this.erreserbaId = erreserbaId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getMahaiaId() { return mahaiaId; }
    public void setMahaiaId(Integer mahaiaId) { this.mahaiaId = mahaiaId; }

    public int getErabiltzaileId() { return erabiltzaileId; }
    public void setErabiltzaileId(int erabiltzaileId) { this.erabiltzaileId = erabiltzaileId; }

    public Integer getKomensalak() { return komensalak; }
    public void setKomensalak(Integer komensalak) { this.komensalak = komensalak; }

    public String getEgoera() { return egoera; }
    public void setEgoera(String egoera) { this.egoera = egoera; }

    public String getSukaldeaEgoera() { return sukaldeaEgoera; }
    public void setSukaldeaEgoera(String sukaldeaEgoera) { this.sukaldeaEgoera = sukaldeaEgoera; }

    public java.sql.Timestamp getSortzeData() { return sortzeData; }
    public void setSortzeData(java.sql.Timestamp sortzeData) { this.sortzeData = sortzeData; }

    public java.sql.Timestamp getItxieraData() { return itxieraData; }
    public void setItxieraData(java.sql.Timestamp itxieraData) { this.itxieraData = itxieraData; }

    public Integer getErreserbaId() { return erreserbaId; }
    public void setErreserbaId(Integer erreserbaId) { this.erreserbaId = erreserbaId; }
}
