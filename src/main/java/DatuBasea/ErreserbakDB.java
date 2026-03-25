package DatuBasea;

import Util.Conn;
import model.Erreserba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ErreserbakDB {

    public static List<Erreserba> lortuErreserbak() {
        List<Erreserba> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, m.zenbakia
                FROM erreserbak e
                LEFT JOIN mahaiak m ON m.id = e.mahaia_id
                ORDER BY e.erreserba_data DESC, e.txanda ASC, e.id DESC
                """;

        try (Connection c = Conn.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Erreserba(
                        rs.getInt("id"),
                        rs.getInt("mahaia_id"),
                        rs.getString("bezeroa_izena"),
                        rs.getString("telefonoa"),
                        rs.getDate("erreserba_data"),
                        rs.getDate("data"),
                        rs.getString("txanda"),
                        rs.getInt("pertsona_kopurua"),
                        rs.getString("egoera")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static int gehituErreserba(Erreserba e) {
        String sql = """
                INSERT INTO erreserbak
                (mahaia_id, bezeroa_izena, telefonoa, erreserba_data, data, txanda, pertsona_kopurua, egoera)
                VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)
                """;

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, e.getMahaiaId());
            ps.setString(2, e.getBezeroaIzena());
            ps.setString(3, hutsikBadaNull(e.getTelefonoa()));
            ps.setDate(4, e.getErreserbaData());
            ps.setString(5, e.getTxanda());
            ps.setInt(6, e.getPertsonaKopurua());
            ps.setString(7, e.getEgoera());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    public static void eguneratuErreserba(Erreserba e) {
        String sql = """
                UPDATE erreserbak
                SET mahaia_id = ?, bezeroa_izena = ?, telefonoa = ?, erreserba_data = ?, txanda = ?, pertsona_kopurua = ?, egoera = ?
                WHERE id = ?
                """;

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, e.getMahaiaId());
            ps.setString(2, e.getBezeroaIzena());
            ps.setString(3, hutsikBadaNull(e.getTelefonoa()));
            ps.setDate(4, e.getErreserbaData());
            ps.setString(5, e.getTxanda());
            ps.setInt(6, e.getPertsonaKopurua());
            ps.setString(7, e.getEgoera());
            ps.setInt(8, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean ezabatuErreserba(int id) {
        String sql = "DELETE FROM erreserbak WHERE id = ?";

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static Map<Integer, String> lortuMahaiakMap() {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        String sql = "SELECT id, zenbakia FROM mahaiak ORDER BY zenbakia";

        try (Connection c = Conn.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                mapa.put(rs.getInt("id"), "Mahaia " + rs.getInt("zenbakia"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return mapa;
    }

    private static String hutsikBadaNull(String balioa) {
        return balioa == null || balioa.trim().isEmpty() ? null : balioa.trim();
    }
}
