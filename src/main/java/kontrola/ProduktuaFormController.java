package kontrola;

import DatuBasea.KategoriakDB;
import DatuBasea.OsagaiakDB;
import DatuBasea.ProduktuOsagaiakDB;
import DatuBasea.ProduktuakDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.Osagaiak;
import model.ProduktuOsagaia;
import model.Produktuak;

import java.util.List;
import java.util.Map;

public class ProduktuaFormController {
    @FXML private TextField txtIzena, txtPrezioa, txtStock, txtBilatuOsagaia, txtKantitateBerria;
    @FXML private ComboBox<String> cmbKategoria;
    @FXML private ComboBox<String> cmbOsagaiBerria;
    @FXML private Label lblUnitateaBerria;
    @FXML private TableView<ProduktuOsagaia> osagaiTaula;
    @FXML private TableColumn<ProduktuOsagaia, String> colOsagaiaIzena, colUnitatea;
    @FXML private TableColumn<ProduktuOsagaia, Double> colKantitatea;

    private final ObservableList<ProduktuOsagaia> osagaiak = FXCollections.observableArrayList();
    private FilteredList<ProduktuOsagaia> filtratua;
    private Produktuak editatzen;
    private List<Osagaiak> osagaiGuztiak;

    public void setProduktua(Produktuak p) {
        this.editatzen = p;
        if (p != null) {
            txtIzena.setText(p.getIzena());
            txtPrezioa.setText(String.valueOf(p.getPrezioa()));
            txtStock.setText(String.valueOf(p.getStockAktuala()));
            osagaiak.setAll(ProduktuOsagaiakDB.lortuProduktukoOsagaiak(p.getId()));

            Map<Integer, String> mapa = KategoriakDB.lortuKategoriakMap();
            cmbKategoria.setValue(mapa.get(p.getKategoriaId()));
        }
    }

    @FXML
    public void initialize() {
        osagaiTaula.setEditable(true);
        osagaiTaula.setPlaceholder(new Label("Ez dago daturik."));
        cmbKategoria.setItems(FXCollections.observableArrayList(KategoriakDB.lortuKategoriakMap().values()));
        osagaiGuztiak = OsagaiakDB.lortuGuztiak();
        ObservableList<String> osagaiIzenaLista = FXCollections.observableArrayList();
        for (Osagaiak o : osagaiGuztiak) osagaiIzenaLista.add(o.getIzena());
        cmbOsagaiBerria.setItems(osagaiIzenaLista);
        lblUnitateaBerria.setText("-");
        cmbOsagaiBerria.valueProperty().addListener((obs, oldVal, newVal) -> eguneratuUnitateaBerria(newVal));

        colOsagaiaIzena.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getIzena()));

        colKantitatea.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getKantitatea()));
        colKantitatea.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colKantitatea.setOnEditCommit(e -> {
            Double balioa = e.getNewValue();
            if (balioa == null || balioa <= 0) {
                alerta(Alert.AlertType.WARNING, "Kantitateak zenbaki positiboa izan behar du.");
                osagaiTaula.refresh();
                return;
            }
            e.getRowValue().setKantitatea(balioa);
            osagaiTaula.refresh();
        });

        colUnitatea.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUnitatea()));
        filtratua = new FilteredList<>(osagaiak, po -> true);
        osagaiTaula.setItems(filtratua);

        txtBilatuOsagaia.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase().trim();

            filtratua.setPredicate(po -> {
                if (filtro.isEmpty()) return true;
                return po.getIzena().toLowerCase().contains(filtro);
            });
        });
    }

    @FXML
    private void gehituOsagaia() {
        String osagaiIzena = cmbOsagaiBerria.getValue();
        if (osagaiIzena == null || osagaiIzena.isBlank()) {
            alerta(Alert.AlertType.WARNING, "Hautatu osagai bat.");
            return;
        }

        double kantitatea;
        try {
            kantitatea = Double.parseDouble(txtKantitateBerria.getText().trim());
            if (kantitatea <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            alerta(Alert.AlertType.WARNING, "Kantitateak zenbaki positiboa izan behar du.");
            return;
        }

        Osagaiak osagaia = lortuOsagaiaIzenez(osagaiIzena);
        if (osagaia == null) {
            alerta(Alert.AlertType.WARNING, "Hautatutako osagaia ez da existitzen.");
            return;
        }

        for (ProduktuOsagaia dagoen : osagaiak) {
            if (dagoen.getOsagaiaId() == osagaia.getId()) {
                alerta(Alert.AlertType.WARNING, "Osagai hori dagoeneko gehituta dago.");
                return;
            }
        }

        osagaiak.add(new ProduktuOsagaia(0, osagaia.getId(), osagaia.getIzena(), kantitatea, osagaia.getUnitatea()));
        cmbOsagaiBerria.setValue(null);
        txtKantitateBerria.clear();
        lblUnitateaBerria.setText("-");
    }

    @FXML
    private void kenduOsagaia() {
        ProduktuOsagaia o = osagaiTaula.getSelectionModel().getSelectedItem();
        if (o != null) osagaiak.remove(o);
    }

    @FXML
    private void gordeProduktua() {

        if (osagaiTaula.getEditingCell() != null) {
            osagaiTaula.edit(-1, null);
        }

        String izenaKategoria = cmbKategoria.getValue();
        if (izenaKategoria == null || izenaKategoria.isBlank()) {
            alerta(Alert.AlertType.ERROR, "Hautatu kategoria bat.");
            return;
        }
        int kategoriaId = -1;

        for (Map.Entry<Integer, String> e : KategoriakDB.lortuKategoriakMap().entrySet()) {
            if (e.getValue().trim().equalsIgnoreCase(izenaKategoria.trim())) {
                kategoriaId = e.getKey();
                break;
            }
        }

        if (kategoriaId == -1) {
            alerta(Alert.AlertType.ERROR, "Kategoria ez da existitzen: " + izenaKategoria);
            return;
        }

        if (txtIzena.getText().trim().isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Sartu produktuaren izena.");
            return;
        }

        double prezioa;
        int stocka;
        try {
            prezioa = Double.parseDouble(txtPrezioa.getText().trim());
            stocka = Integer.parseInt(txtStock.getText().trim());
            if (prezioa < 0 || stocka < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            alerta(Alert.AlertType.WARNING, "Prezioa eta stocka baliozko zenbakiak izan behar dira.");
            return;
        }

        for (ProduktuOsagaia o : osagaiak) {
            if (o.getOsagaiaId() <= 0 || o.getKantitatea() <= 0) {
                alerta(Alert.AlertType.WARNING, "Osagai guztiek baliozko datuak izan behar dituzte.");
                return;
            }
        }

        Produktuak p = new Produktuak(
                editatzen == null ? 0 : editatzen.getId(),
                txtIzena.getText().trim(),
                kategoriaId,
                prezioa,
                stocka
        );

        int productoId;

        if (editatzen == null) {
            productoId = ProduktuakDB.gehituProduktua(p);
            if (productoId == -1) {
                alerta(Alert.AlertType.ERROR, "Ezin izan da produktua gorde.");
                return;
            }
            p.setId(productoId);
        } else {
            ProduktuakDB.eguneratuProduktua(p);
            productoId = p.getId();
            ProduktuOsagaiakDB.ezabatuProduktukoOsagaiak(productoId);
        }

        for (ProduktuOsagaia o : osagaiak) {
            o.setProduktuaId(productoId);
            ProduktuOsagaiakDB.gehituProduktukoOsagaia(productoId, o);
        }

        itxi();
    }

    @FXML
    private void itxi() {
        ((Stage) txtIzena.getScene().getWindow()).close();
    }

    private void eguneratuUnitateaBerria(String osagaiIzena) {
        Osagaiak osagaia = lortuOsagaiaIzenez(osagaiIzena);
        lblUnitateaBerria.setText(osagaia == null ? "-" : osagaia.getUnitatea());
    }

    private Osagaiak lortuOsagaiaIzenez(String izena) {
        if (izena == null) {
            return null;
        }
        for (Osagaiak osagaia : osagaiGuztiak) {
            if (osagaia.getIzena().equalsIgnoreCase(izena.trim())) {
                return osagaia;
            }
        }
        return null;
    }

    private void alerta(Alert.AlertType mota, String mezua) {
        Alert alert = new Alert(mota);
        alert.setTitle(mota == Alert.AlertType.ERROR ? "Errorea" : "Abisua");
        alert.setHeaderText(null);
        alert.setContentText(mezua);
        alert.showAndWait();
    }
}
