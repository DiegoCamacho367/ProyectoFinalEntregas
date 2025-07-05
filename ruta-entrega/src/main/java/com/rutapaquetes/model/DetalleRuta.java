package com.rutapaquetes.model;

public class DetalleRuta {
    private String id;
    private String rutaId;          // la ruta a la que pertenece este detalle
    private String direccion;       // dirección del punto de entrega
    private String descripcion;     // breve texto del detalle
    private double latitud;         // opcional si quieres coordenas
    private double longitud;

    // constructor vacío obligatorio
    public DetalleRuta() {
    }

    public DetalleRuta(String id, String rutaId, String direccion, String descripcion, double latitud, double longitud) {
        this.id = id;
        this.rutaId = rutaId;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}