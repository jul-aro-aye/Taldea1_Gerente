package kontrola;

import DatuBasea.ErreserbakDB;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Erreserba;

import java.util.Map;

public class ErreserbakController {

    @FXML private TableView<Erreserba> erreserbakTable;
    @FXML private TableColumn<Erreserba, String> colBezeroa;
    @FXML private TableColumn<Erreserba, String> colTelefonoa;
    @FXML private TableColumn<Erreserba, String> colData;
    @FXML private TableColumn<Erreserba, String> colTxanda;
    @FXML private TableColumn<Erreserba, Integer> colPertsonak;
    @FXML private TableColumn<Erreserba, String> colMahaia;
    @FXML private TableColumn<Erreserba, String> colEgoera;

    @FXML private TextField txtBilatu;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private ObservableList<Erreserba> erreserbak;
    private FilteredList<Erreserba> filtratua;
    private Map<Integer, String> mahaiakMapa;

    @FXML
    public void initialize() {
        mahaiakMapa = ErreserbakDB.lortuMahaiakMap();
        colBezeroa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBezeroaIzena()));
        colTelefonoa.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTelefonoa() == null ? "" : c.getValue().getTelefonoa()
        ));
        colData.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getErreserbaData())));
        colTxanda.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTxanda()));
        colPertsonak.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPertsonaKopurua()));
        colMahaia.setCellValueFactory(c -> new SimpleStringProperty(
                mahaiakMapa.getOrDefault(c.getValue().getMahaiaId(), String.valueOf(c.getValue().getMahaiaId()))
        ));
        colEgoera.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEgoera()));
        erreserbakTable.setPlaceholder(new Label("Ez dago daturik."));

        kargatuDatuak();

        txtBilatu.textProperty().addListener((obs, oldVal, newVal) -> aplikatuFiltro());

        erreserbakTable.setRowFactory(tv -> {
            TableRow<Erreserba> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    irekiFormularioa(row.getItem());
                }
            });
            return row;
        });

        erreserbakTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> botoiakAktualizatu());

        botoiakAktualizatu();
    }

    private void kargatuDatuak() {
        mahaiakMapa = ErreserbakDB.lortuMahaiakMap();
        erreserbak = FXCollections.observableArrayList(ErreserbakDB.lortuErreserbak());
        filtratua = new FilteredList<>(erreserbak, e -> true);
        erreserbakTable.setItems(filtratua);
        aplikatuFiltro();
    }

    private void aplikatuFiltro() {
        if (filtratua == null) {
            return;
        }

        String filtro = txtBilatu.getText() == null ? "" : txtBilatu.getText().trim().toLowerCase();
        filtratua.setPredicate(e -> {
            if (filtro.isEmpty()) {
                return true;
            }

            String bezeroa = e.getBezeroaIzena() == null ? "" : e.getBezeroaIzena().toLowerCase();
            String telefonoa = e.getTelefonoa() == null ? "" : e.getTelefonoa().toLowerCase();
            String mahaia = String.valueOf(e.getMahaiaId());

            return bezeroa.contains(filtro) || telefonoa.contains(filtro) || mahaia.contains(filtro);
        });
    }

    private void botoiakAktualizatu() {
        Erreserba sel = erreserbakTable.getSelectionModel().getSelectedItem();
        btnEdit.setDisable(sel == null);
        btnDelete.setDisable(sel == null);
    }

    @FXML
    private void gehituErreserba() {
        irekiFormularioa(null);
    }

    @FXML
    private void editatuErreserba() {
        Erreserba e = erreserbakTable.getSelectionModel().getSelectedItem();
        if (e != null) irekiFormularioa(e);
        else alerta("Hautatu erreserba bat editatzeko.");
    }

    @FXML
    private void ezabatuErreserba() {
        Erreserba e = erreserbakTable.getSelectionModel().getSelectedItem();
        if (e == null) {
            alerta("Hautatu erreserba bat ezabatzeko.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ziur zaude erreserba hau ezabatu nahi duzula?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Baieztapena");
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            boolean deleted = ErreserbakDB.ezabatuErreserba(e.getId());
            if (deleted) kargatuDatuak();
            else alerta("Errorea: ezin izan da erreserba ezabatu.");
        }
    }

    private void irekiFormularioa(Erreserba erreserba) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ErreserbaForm.fxml"));
            Parent root = loader.load();

            ErreserbaFormController controller = loader.getController();
            controller.setErreserba(erreserba);

            Stage stage = new Stage();
            stage.setTitle(erreserba == null ? "Erreserba gehitu" : "Erreserba aldatu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            kargatuDatuak();
        } catch (Exception ex) {
            ex.printStackTrace();
            alerta("Errorea: ezin izan da formularioa ireki.");
        }
    }

    private void alerta(String mezua) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mezua);
        alert.setTitle("Abisua");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
