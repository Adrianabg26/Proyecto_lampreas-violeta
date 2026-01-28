// Paquete donde vive esta clase. Normalmente 'dao' agrupa los Data Access Objects,
// clases dedicadas exclusivamente a hablar con la base de datos.
package dao;

// Clase que gestiona la obtención de conexiones JDBC.
import db.Db;

// Modelo/entidad Repartidor. Representa una fila de la tabla 'repartidor'.
import model.Repartidor;

// Imports necesarios para el uso del API JDBC de Java.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Usamos listas dinámicas para devolver varios repartidores cuando hacemos un SELECT *.
import java.util.ArrayList;
import java.util.List;

// Clase DAO que contiene la lógica de acceso a datos para la entidad Repartidor.
// Todo lo relacionado con INSERT, SELECT, UPDATE y DELETE de repartidores.
public class RepartidorDAO {

    // ----------------------------------------------------------
    // SENTENCIAS SQL PREPARADAS COMO CONSTANTES
    // ----------------------------------------------------------

    // Consulta SQL para insertar un repartidor.
    // Usamos ? para parámetros → evita SQL injection y mejora rendimiento con sentencias preparadas.
    private static final String INSERT_SQL =
            "INSERT INTO repartidor (id, nombre, vehiculo, email) VALUES (?, ?, ?, ?)";

    // Consulta SQL para buscar un repartidor por su ID.
    private static final String SELECT_BY_ID_SQL =
            "SELECT id, nombre, vehiculo, email FROM repartidor WHERE id = ?";

    // Consulta SQL para obtener todos los repartidores ordenados por id.
    private static final String SELECT_ALL_SQL = "SELECT id, nombre, vehiculo, email FROM repartidor ORDER BY id";

    // Consulta SQL para borrar un repartidor por su ID.
    private static final String DELETE_SQL = "DELETE FROM repartidor WHERE id = ?";


    private static final String SEARCH_SQL = """
            SELECT id, nombre, vehiculo, tarifaEnvio
            FROM cliente
            WHERE CAST(id AS TEXT) ILIKE ? 
                OR nombre ILIKE ?  
                OR vehiculo ILIKE ?
                OR email ILIKE ?
            ORDER BY id                    
            """;

    // ----------------------------------------------------------
    // MÉTODO: INSERTAR UN CLIENTE
    // ----------------------------------------------------------
    public void insert(Repartidor r) throws SQLException {
        // Método público que inserta un repartidor en la base de datos.
        // Recibe un objeto Repartidor y lanza SQLException si algo sale mal.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            // try-with-resources: la conexión y el PreparedStatement se cerrarán automáticamente
            // al final del bloque, aunque haya errores.

            ps.setInt(1, r.getId());         // Parámetro 1 → columna id
            ps.setString(2, r.getNombre());  // Parámetro 2 → columna nombre
            ps.setString(3, r.getVehiculo());   // Parámetro 3 → columna vehiculo
            ps.setString(4, r.getEmail()); // Parámetro 4 → columna email

            ps.executeUpdate();
            // Ejecuta la sentencia. Como es un INSERT, no devuelve ResultSet.

            // Recuperar el ID generado por PostgreSQL
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    r.setId(idGenerado);  // lo guardamos en el objeto
                }
            }
        }
    }

    // Versión transaccional: usa una conexión que le pasa el servicio
    public void insert(Repartidor r, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, r.getId());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getVehiculo());
            ps.setString(4, r.getEmail());
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------
    // MÉTODO: BUSCAR REPARTIDOR POR ID
    // ----------------------------------------------------------

    public Repartidor findById(int id) throws SQLException {
        // Devuelve el Repartidor cuyo id coincida con el parámetro.
        // Si no existe, devuelve null.

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);  // Asignamos el id al parámetro ?

            try (ResultSet rs = ps.executeQuery()) {
                // executeQuery() devuelve un ResultSet ↔ una tabla virtual con las filas devueltas.

                if (rs.next()) {
                    // Si rs.next() = true → hay fila. Avanzamos a ella y leemos sus columnas.
                    return mapRow(rs);
                }
                return null;
                // Si no hay resultado, devolvemos null para indicar "no encontrado".
            }
        }
    }


    // ----------------------------------------------------------
    // MÉTODO: LISTAR TODOS LOS REPARTIDORES
    // ----------------------------------------------------------

    public List<Repartidor> findAll() throws SQLException {
        // Devuelve una lista con todos los clientes de la tabla.
        // Nunca devuelve null; si no hay datos, devuelve lista vacía.

        List<Repartidor> out = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(mapRow(rs)); // Añadimos el repartidor a la lista.
            }
        }
        return out;   // Devolvemos la lista cargada con los repartidores
    }

    public List<Repartidor> search(String filtro) throws SQLException {

        String patron = "%" + filtro + "%";

        try (Connection con = Db.getConnection();
             PreparedStatement pst = con.prepareStatement(SEARCH_SQL)) {
            pst.setString(1, patron);
            pst.setString(2, patron);
            pst.setString(3, patron);
            pst.setString(4, patron);

            List<Repartidor> out = new ArrayList<>();

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
            return out;
        }
    }

    /**
     * Borra un registro de repartidor.
     */
    public boolean delete(int id) throws SQLException {
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Repartidor mapRow(ResultSet rs) throws SQLException {

        Repartidor r = new Repartidor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("vehiculo"),
                rs.getString("email")
        );

        return r;
    }
}