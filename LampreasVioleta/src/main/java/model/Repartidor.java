package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Nueva entidad "Repartidor" añadida al sistema.
 * Relaciones:
 * - 1:N con Pedidos (Un repartidor entrega muchos pedidos, pero cada pedido es entregado por un único repartidor)
 */

public class Repartidor {
    private Integer id;
    private String nombre;
    private String vehiculo;
    private String email;

    // Relación 1:N
    private List<Repartidor> pedidos = new ArrayList<>();

     // Constructor vacío
    public Repartidor() {
    }

    // Constructor completo
    public Repartidor(Integer id, String nombre, String vehiculo, String email) {
        this.id = id;
        this.nombre = nombre;
        this.vehiculo = vehiculo;
        this.email = email;
    }

    // Getters y Setters
    public List<Repartidor> getPedidos() {return pedidos;}
    public void setPedidos(List<Repartidor> pedidos) {this.pedidos = pedidos;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getVehiculo() {return vehiculo;}
    public void setVehiculo(String vehiculo) {this.vehiculo = vehiculo;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    @Override
    public String toString() {
        return "Repartidor{id=%d, nombre='%s', vehiculo='%s', email='%s'}".formatted(id, nombre, vehiculo, email);
    }
}