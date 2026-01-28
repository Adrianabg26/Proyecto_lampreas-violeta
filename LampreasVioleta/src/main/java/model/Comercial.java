package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad "Comercial".
 * Relaciones:
 * - 1:N con Cliente (un comercial gestiona varios clientes).
 * - 1:N con Pedidos (un comercial tiene una lista de pedidos asociados)
 */

public class Comercial {
    private Integer id;
    private String nombre;
    private String zonaVenta;
    private double comision;


    // Relación 1:N
    private List<Cliente> clientesAsignados = new ArrayList<>();

    // Relación 1:N
    private List<Pedido> pedidos = new ArrayList<>();


    // Constructor vacío
    public Comercial() {
    }

    // Constructor completo
    public Comercial(Integer id, String nombre, String zonaVenta, double comision) {
        this.id = id;
        this.nombre = nombre;
        this.zonaVenta = zonaVenta;
        this.comision = comision;
    }

    // Getters y Setters
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getZonaVenta() {return zonaVenta;}
    public void setZonaVenta(String zonaVenta) {this.zonaVenta = zonaVenta;}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public double getComision() {return comision;}
    public void setComision(double comision) {this.comision = comision;}

    public List<Cliente> getClientesAsignados() {return clientesAsignados;}
    public void setClientesAsignados(List<Cliente> clientesAsignados) {this.clientesAsignados = clientesAsignados;}

    public List<Pedido> getPedidos() {return pedidos;}
    public void setPedidos(List<Pedido> pedidos) {this.pedidos = pedidos;}

    @Override
    public String toString() {
        return "Comercial{id=%d, nombre='%s', zona='%s', comision=%.2f%%}".formatted(id, nombre, zonaVenta, comision);
    }
}
