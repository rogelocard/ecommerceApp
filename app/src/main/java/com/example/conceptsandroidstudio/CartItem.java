package com.example.conceptsandroidstudio;

public class CartItem {

    public String id;
    private String tituloProducto;
    private int cantidad;
    private long precioUnitario;
    private long precioTotal;

    private String foto;

    public CartItem(String id, String tituloProducto, int cantidad, long precioUnitario,long precioTotal, String foto) {
        this.id = id;
        this.tituloProducto = tituloProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioTotal = precioTotal;
        this.foto = foto;
    }


    public String getId() {
        return id;
    }

    public String getTituloProducto() {
        return tituloProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public long getPrecioUnitario() {
        return precioUnitario;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(long precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getFoto() {
        return foto;
    }
}
