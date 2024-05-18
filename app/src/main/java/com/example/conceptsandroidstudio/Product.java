package com.example.conceptsandroidstudio;

import java.util.List;

public class Product {
    private String marca;
    private String modelo;
    private Long precio;
    private List<String> fotosUrls;

    public Product() {
    }

    public Product(String marca, String modelo, Long precio, List<String> fotosUrls) {
        this.marca = marca;
        this.modelo = modelo;
        this.precio = precio;
        this.fotosUrls = fotosUrls;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public List<String> getFotosUrls() {
        return fotosUrls;
    }

    public void setFotosUrls(List<String> fotosUrls) {
        this.fotosUrls = fotosUrls;
    }
}
