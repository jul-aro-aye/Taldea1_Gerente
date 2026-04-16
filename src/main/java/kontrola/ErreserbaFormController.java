package kontrola;

import DatuBasea.ErreserbakDB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Erreserba;

import java.sql.Date;
import java.util.Map;

public class ErreserbaFormController {

    @FXML private ComboBox<String> cmbMahaia;
    @FXML private TextField txtBezeroa;
    @FXML private TextField txtTelefonoa;
    @FXML private DatePicker dpErreserbaData;
    @FXML private ComboBox<String> cmbTxanda;
    @FXML private TextField txtPertsonak;
    @FXML private ComboBox<String> cmbEgoera;
    @FXML private Label lblMahaiaOharra;

    private Erreserba editatzen;
    private Map<Integer, String> mahaiakMapa;

    @FXML
    public void initialize() {
        mahaiakMapa = ErreserbakDB.lortuMahaiakMap();
        cmbMahaia.setItems(FXCollections.observableArrayList(mahaiakMapa.values()));
        cmbTxanda.setItems(FXCollections.observableArrayList("bazkaria", "afaria"));
        cmbEgoera.setItems(FXCollections.observableArrayList("sortua", "eginda", "bertan_behera"));
        cmbEgoera.setValue("sortua");
        cmbEgoera.setDisable(true);
        cmbMahaia.setDisable(true);
        lblMahaiaOharra.setText("Lehenik erreserba data eta txanda hautatu.");

        dpErreserbaData.valueProperty().addListener((obs, oldVal, newVal) -> eguneratuMahaiak());
        cmbTxanda.valueProperty().addListener((obs, oldVal, newVal) -> eguneratuMahaiak());
        txtPertsonak.textProperty().addListener((obs, oldVal, newVal) -> eguneratuMahaiak());
    }

    public void setErreserba(Erreserba erreserba) {
        this.editatzen = erreserba;
        if (erreserba != null) {
            txtBezeroa.setText(erreserba.getBezeroaIzena());
            txtTelefonoa.setText(erreserba.getTelefonoa() == null ? "" : erreserba.getTelefonoa());
            dpErreserbaData.setValue(erreserba.getErreserbaData().toLocalDate());
            cmbTxanda.setValue(erreserba.getTxanda());
            txtPertsonak.setText(String.valueOf(erreserba.getPertsonaKopurua()));
            cmbEgoera.setValue(erreserba.getEgoera());
            cmbEgoera.setDisable(false);
            eguneratuMahaiak();
            cmbMahaia.setValue(mahaiakMapa.get(erreserba.getMahaiaId()));
        }
    }

    @FXML
    private void gordeErreserba() {
        String mahaia = cmbMahaia.getValue();
        String bezeroa = txtBezeroa.getText().trim();
        String telefonoa = txtTelefonoa.getText().trim();
        String txanda = cmbTxanda.getValue();
        String egoera = cmbEgoera.getValue();

        if (mahaia == null || bezeroa.isEmpty() || dpErreserbaData.getValue() == null
                || txanda == null || egoera == null) {
            new Alert(Alert.AlertType.WARNING, "Bete derrigorrezko eremu guztiak.").showAndWait();
            return;
        }

        int pertsonak;
        try {
            pertsonak = Integer.parseInt(txtPertsonak.getText().trim());
            if (pertsonak <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Pertsona kopurua zenbaki positiboa izan behar da.").showAndWait();
            return;
        }

        int mahaiaId = lortuMahaiaId(mahaia);
        int kapazitatea = ErreserbakDB.lortuMahaiKapazitatea(mahaiaId);
        if (kapazitatea != -1 && pertsonak > kapazitatea) {
            new Alert(Alert.AlertType.WARNING, "Mahai horren kapazitatea ez da nahikoa pertsona kopuru horrentzat.").showAndWait();
            eguneratuMahaiak();
            return;
        }

        Erreserba erreserba = new Erreserba(
                editatzen == null ? 0 : editatzen.getId(),
                mahaiaId,
                bezeroa,
                telefonoa,
                Date.valueOf(dpErreserbaData.getValue()),
                editatzen == null ? null : editatzen.getData(),
                txanda,
                pertsonak,
                egoera
        );

        if (ErreserbakDB.mahaiaErreserbatutaDago(
                erreserba.getMahaiaId(),
                erreserba.getErreserbaData(),
                erreserba.getTxanda(),
                editatzen == null ? null : editatzen.getId())) {
            new Alert(Alert.AlertType.WARNING, "Mahai hori data eta txanda horretan erreserbatuta dago jada.").showAndWait();
            eguneratuMahaiak();
            return;
        }

        if (editatzen == null) {
            int id = ErreserbakDB.gehituErreserba(erreserba);
            if (id == -1) {
                new Alert(Alert.AlertType.ERROR, "Ezin izan da erreserba gorde. Egiaztatu mahaia libre dagoela.").showAndWait();
                return;
            }
        } else {
            ErreserbakDB.eguneratuErreserba(erreserba);
        }

        itxi();
    }

    private void eguneratuMahaiak() {
        String aurrekoa = cmbMahaia.getValue();

        if (dpErreserbaData.getValue() == null || cmbTxanda.getValue() == null) {
            mahaiakMapa = ErreserbakDB.lortuMahaiakMap();
            cmbMahaia.getItems().clear();
            cmbMahaia.setValue(null);
            cmbMahaia.setDisable(true);
            lblMahaiaOharra.setText("Lehenik erreserba data eta txanda hautatu.");
            return;
        }

        int pertsonak;
        try {
            pertsonak = Integer.parseInt(txtPertsonak.getText().trim());
            if (pertsonak <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            cmbMahaia.getItems().clear();
            cmbMahaia.setValue(null);
            cmbMahaia.setDisable(true);
            lblMahaiaOharra.setText("Lehenik pertsona kopuru baliozkoa sartu.");
            return;
        }

        mahaiakMapa = ErreserbakDB.lortuMahaiLibreMap(
                Date.valueOf(dpErreserbaData.getValue()),
                cmbTxanda.getValue(),
                pertsonak,
                editatzen == null ? null : editatzen.getId()
        );

        cmbMahaia.setItems(FXCollections.observableArrayList(mahaiakMapa.values()));
        cmbMahaia.setDisable(false);

        if (aurrekoa != null && cmbMahaia.getItems().contains(aurrekoa)) {
            cmbMahaia.setValue(aurrekoa);
        } else {
            cmbMahaia.setValue(null);
        }

        if (mahaiakMapa.isEmpty()) {
            cmbMahaia.setDisable(true);
            lblMahaiaOharra.setText("Ez dago pertsona kopuru horrentzat mahai librerik hautatutako data eta txandarako.");
        } else {
            lblMahaiaOharra.setText("Hautatutako data, txanda eta pertsona kopururako balio duten mahaiak bakarrik agertzen dira.");
        }
    }

    private int lortuMahaiaId(String mahaiaIzena) {
        for (Map.Entry<Integer, String> entry : mahaiakMapa.entrySet()) {
            if (entry.getValue().equals(mahaiaIzena)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @FXML
    private void itxi() {
        Stage stage = (Stage) txtBezeroa.getScene().getWindow();
        stage.close();
    }
}
