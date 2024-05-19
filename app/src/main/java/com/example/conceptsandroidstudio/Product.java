package com.example.conceptsandroidstudio;

import java.util.List;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String marca;
    private String modelo;
    private Long precio;
    private List<String> fotosUrls;
    private String procesador;
    private String ram;
    private String rom;
    private String color;
    private String sistemaOperativo;
    private int pantalla;
    private String id;

    public Product() {
    }

    public Product(String marca, String modelo, Long precio, List<String> fotosUrls,
                   String procesador, String ram, String rom, String color,
                   String sistemaOperativo, int pantalla, String id) {
        this.marca = marca;
        this.modelo = modelo;
        this.precio = precio;
        this.fotosUrls = fotosUrls;
        this.procesador = procesador;
        this.ram = ram;
        this.rom = rom;
        this.color = color;
        this.sistemaOperativo = sistemaOperativo;
        this.pantalla = pantalla;
        this.id=id;
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

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public int getPantalla() {
        return pantalla;
    }
    public void setPantalla(int pantalla) {
        this.pantalla = pantalla;
    }

    public String getId() {
        return id;
    }
}
