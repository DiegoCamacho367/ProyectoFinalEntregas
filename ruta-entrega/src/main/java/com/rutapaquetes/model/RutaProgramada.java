package com.rutapaquetes.model;

public class RutaProgramada {
    private String id;
    private String conductorId;
    private String vehiculoId;
    private String fecha;
    private String nombreRuta;

    public RutaProgramada() {}

    public RutaProgramada(String id, String conductorId, String vehiculoId, String fecha, String nombreRuta) {
        this.id = id;
        this.conductorId = conductorId;
        this.vehiculoId = vehiculoId;
        this.fecha = fecha;
        this.nombreRuta = nombreRuta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }
}