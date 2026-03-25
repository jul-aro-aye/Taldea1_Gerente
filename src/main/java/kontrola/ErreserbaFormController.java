package kontrola;

import DatuBasea.ErreserbakDB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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

    private Erreserba editatzen;
    private Map<Integer, String> mahaiakMapa;

    @FXML
    public void initialize() {
        mahaiakMapa = ErreserbakDB.lortuMahaiakMap();
        cmbMahaia.setItems(FXCollections.observableArrayList(mahaiakMapa.values()));
        cmbTxanda.setItems(FXCollections.observableArrayList("bazkaria", "afaria"));
        cmbEgoera.setItems(FXCollections.observableArrayList("eginda", "amaitua", "bertan_behera"));
        cmbEgoera.setValue("eginda");
        cmbEgoera.setDisable(true);
    }

    public void setErreserba(Erreserba erreserba) {
        this.editatzen = erreserba;
        if (erreserba != null) {
            cmbMahaia.setValue(mahaiakMapa.get(erreserba.getMahaiaId()));
            txtBezeroa.setText(erreserba.getBezeroaIzena());
            txtTelefonoa.setText(erreserba.getTelefonoa() == null ? "" : erreserba.getTelefonoa());
            dpErreserbaData.setValue(erreserba.getErreserbaData().toLocalDate());
            cmbTxanda.setValue(erreserba.getTxanda());
            txtPertsonak.setText(String.valueOf(erreserba.getPertsonaKopurua()));
            cmbEgoera.setValue(erreserba.getEgoera());
            cmbEgoera.setDisable(false);
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

        Erreserba erreserba = new Erreserba(
                editatzen == null ? 0 : editatzen.getId(),
                lortuMahaiaId(mahaia),
                bezeroa,
                telefonoa,
                Date.valueOf(dpErreserbaData.getValue()),
                editatzen == null ? null : editatzen.getData(),
                txanda,
                pertsonak,
                egoera
        );

        if (editatzen == null) {
            int id = ErreserbakDB.gehituErreserba(erreserba);
            if (id == -1) {
                new Alert(Alert.AlertType.ERROR, "Ezin izan da erreserba gorde.").showAndWait();
                return;
            }
        } else {
            ErreserbakDB.eguneratuErreserba(erreserba);
        }

        itxi();
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
