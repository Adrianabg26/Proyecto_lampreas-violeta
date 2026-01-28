package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.Db;
import model.Comercial;

public class ComercialDAO {
    // ----------------------------------------------------------
    // SENTENCIAS SQL PREPARADAS COMO CONSTANTES
    // ----------------------------------------------------------

    // Consulta SQL para insertar un comercial.
    // Usamos ? para parámetros → evita SQL injection y mejora rendimiento con sentencias preparadas.
    private static final String INSERT_SQL =
            "INSERT INTO comercial (id, nombre, zonaVenta, comision) VALUES (?, ?, ?, ?)";

    // Consulta SQL para buscar un comercial por su ID.
    private static final String SELECT_BY_ID_SQL =
            "SELECT id, nombre, zonaVenta, comision FROM comercial WHERE id = ?";

    // Consulta SQL para obtener todos los comerciales ordenados por id.
    private static final String SELECT_ALL_SQL =
            "SELECT id, nombre, zonaVenta, comision FROM comercial ORDER BY id";

    // Consulta SQL para borrar un comercial por su ID.
    private static final String DELETE_SQL =
            "DELETE FROM comercial WHERE id = ?";


    private static final String SEARCH_SQL = """
            SELECT id, nombre, zonaVenta, comision
            FROM comercial
            WHERE CAST(id AS TEXT) ILIKE ?
                OR nombre ILIKE ?
                OR zona ILIKE ?
            ORDER BY id
            """;


    // ----------------------------------------------------------
    // MÉTODO: INSERTAR UN COMERCIAL
    // ----------------------------------------------------------

    public void insert(Comercial c) throws SQLException {
        // Método público que inserta un comercial en la base de datos.
        // Recibe un objeto Comercial y lanza SQLException si algo sale mal.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            // try-with-resources: la conexión y el PreparedStatement se cerrarán automáticamente
            // al final del bloque, aunque haya errores.

            ps.setInt(1, c.getId());         // Parámetro 1 → columna id
            ps.setString(2, c.getNombre());  // Parámetro 2 → columna nombre
            ps.setString(3, c.getZonaVenta());  // Parámetro 3 → columna zona de Venta
            ps.setDouble(4, c.getComision());   // Parámetro 4 → columna comisión

            ps.executeUpdate();
            // Ejecuta la sentencia. Como es un INSERT, no devuelve ResultSet.

            // Recuperar el ID generado por PostgreSQL
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    c.setId(idGenerado);  // lo guardamos en el objeto
                }
            }
        }
    }

            // Versión transaccional: usa una conexión que le pasa el servicio
            public void insert(Comercial c, Connection con) throws SQLException {
                try (PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
                    ps.setInt(1, c.getId());
                    ps.setString(2, c.getNombre());
                    ps.setString(3, c.getZonaVenta());
                    ps.setDouble(4, c.getComision());

                    ps.executeUpdate();
                }
            }

    // ----------------------------------------------------------
    // MÉTODO: BUSCAR COMERCIAL POR ID
    // ----------------------------------------------------------

    public Comercial findById(int id) throws SQLException {
        // Devuelve el comercial cuyo id coincida con el parámetro.
        // Si no existe, devuelve null.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);  // Asignamos el id al parámetro ?

            try (ResultSet rs = ps.executeQuery()) {
                // executeQuery() devuelve un ResultSet ↔ una tabla virtual con las filas devueltas.

                if (rs.next()) {
                    // Si hay resultado, transformamos la fila de la BD en un objeto Java.
                    // Usamos un método de mapeo para que sea más limpio.
                    return mapRow(rs);
                }
                return null;
                // Si no hay resultado, devolvemos null para indicar "no encontrado".
            }
        }
    }

    // ----------------------------------------------------------
    // MÉTODO: LISTAR TODOS LOS COMERCIALES
    // ----------------------------------------------------------

    public List<Comercial> findAll() throws SQLException {
        // Devuelve una lista con todos los comerciales de la tabla.
        // Nunca devuelve null; si no hay datos, devuelve lista vacía.
        List<Comercial> out = new ArrayList<>();

        // Usamos try-with-resources para asegurar que la conexión, el statement
        // y el resultset se cierren automáticamente al terminar.
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // En lugar de crear el objeto aquí manualmente, llamamos a mapRow.
                // Esto limpia el bucle y centraliza la creación del objeto.
                out.add(mapRow(rs)); // Añadimos el comercial a la lista.
            }
        }
        return out;   // Devolvemos la lista cargada con los comerciales del sistema
    }

    public List<Comercial> search(String filtro) throws SQLException {

        String patron = "%" + filtro + "%";

        try (Connection con = Db.getConnection();
             PreparedStatement pst = con.prepareStatement(SEARCH_SQL)) {
            pst.setString(1, patron);
            pst.setString(2, patron);
            pst.setString(3, patron);

            List<Comercial> out = new ArrayList<>();

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
            return out;
        }
    }

    /**
     * Borra un registro de comercial.
     */
    public boolean delete(int id) throws SQLException {
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }


    /*
     * Método auxiliar para convertir una fila del ResultSet en un objeto Comercial.
     * Esto evita repetir código en findAll() y findById().
     */
    private Comercial mapRow(ResultSet rs) throws SQLException {
        Comercial c = new Comercial(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("zonaVenta"),
                rs.getDouble("comision")
        );
        return c;
    }
}
