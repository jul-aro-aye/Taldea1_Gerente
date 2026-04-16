package DatuBasea;

import Util.Conn;
import model.Erreserba;

import java.sql.Connection;
import java.sql.Date;
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
        if (mahaiaErreserbatutaDago(e.getMahaiaId(), e.getErreserbaData(), e.getTxanda(), null)) {
            return -1;
        }

        String sql = """
                INSERT INTO erreserbak
                (mahaia_id, bezeroa_izena, telefonoa, data, erreserba_data, txanda, pertsona_kopurua, egoera)
                VALUES (?, ?, ?, CURRENT_DATE, ?, ?, ?, ?)
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
        if (mahaiaErreserbatutaDago(e.getMahaiaId(), e.getErreserbaData(), e.getTxanda(), e.getId())) {
            return;
        }

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
        String sql = "SELECT id, zenbakia, kapazitatea FROM mahaiak ORDER BY zenbakia";

        try (Connection c = Conn.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                mapa.put(rs.getInt("id"), "Mahaia " + rs.getInt("zenbakia") + " (" + rs.getInt("kapazitatea") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return mapa;
    }

    public static Map<Integer, String> lortuMahaiLibreMap(Date erreserbaData, String txanda, int pertsonak, Integer salbuetsiId) {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        String sql = """
                SELECT m.id, m.zenbakia, m.kapazitatea
                FROM mahaiak m
                WHERE m.kapazitatea >= ?
                  AND NOT EXISTS (
                    SELECT 1
                    FROM erreserbak e
                    WHERE e.mahaia_id = m.id
                      AND e.erreserba_data = ?
                      AND e.txanda = ?
                      AND e.egoera <> 'bertan_behera'
                      AND (? IS NULL OR e.id <> ?)
                )
                ORDER BY m.zenbakia
                """;

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, pertsonak);
            ps.setDate(2, erreserbaData);
            ps.setString(3, txanda);
            if (salbuetsiId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, salbuetsiId);
                ps.setInt(5, salbuetsiId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mapa.put(rs.getInt("id"), "Mahaia " + rs.getInt("zenbakia") + " (" + rs.getInt("kapazitatea") + ")");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return mapa;
    }

    public static int lortuMahaiKapazitatea(int mahaiaId) {
        String sql = "SELECT kapazitatea FROM mahaiak WHERE id = ?";

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, mahaiaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("kapazitatea");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    public static boolean mahaiaErreserbatutaDago(int mahaiaId, Date erreserbaData, String txanda, Integer salbuetsiId) {
        String sql = """
                SELECT COUNT(*)
                FROM erreserbak
                WHERE mahaia_id = ?
                  AND erreserba_data = ?
                  AND txanda = ?
                  AND egoera <> 'bertan_behera'
                  AND (? IS NULL OR id <> ?)
                """;

        try (Connection c = Conn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, mahaiaId);
            ps.setDate(2, erreserbaData);
            ps.setString(3, txanda);
            if (salbuetsiId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, salbuetsiId);
                ps.setInt(5, salbuetsiId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private static String hutsikBadaNull(String balioa) {
        return balioa == null || balioa.trim().isEmpty() ? null : balioa.trim();
    }
}
