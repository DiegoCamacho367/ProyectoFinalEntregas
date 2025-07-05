package com.rutapaquetes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutapaquetes.model.Conductor;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/conductores")
public class ConductorController {

    private final String archivoDatos = "conductores.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/registrar")
    public String registrarConductor(@RequestBody Conductor conductor) {
        List<Conductor> conductores = leerConductores();
        String nuevoId = "CDT" + (conductores.size() + 1);
        conductor.setId(nuevoId);
        conductores.add(conductor);
        guardarConductores(conductores);
        return "Conductor registrado correctamente con id: " + nuevoId;
    }

    @GetMapping
    public List<Conductor> listarConductores() {
        return leerConductores();
    }

    @PutMapping("/actualizar/{id}")
    public String actualizarConductor(@PathVariable String id, @RequestBody Conductor conductorActualizado) {
        List<Conductor> conductores = leerConductores();
        for (Conductor c : conductores) {
            if (c.getId().equals(id)) {
                c.setNombres(conductorActualizado.getNombres());
                c.setApellidos(conductorActualizado.getApellidos());
                c.setNumeroLicencia(conductorActualizado.getNumeroLicencia());
                c.setTelefono(conductorActualizado.getTelefono());
                c.setCorreo(conductorActualizado.getCorreo());
                guardarConductores(conductores);
                return "Conductor actualizado correctamente";
            }
        }
        return "Conductor no encontrado";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminarConductor(@PathVariable String id) {
        List<Conductor> conductores = leerConductores();
        if (conductores.removeIf(c -> c.getId().equals(id))) {
            guardarConductores(conductores);
            return "Conductor eliminado correctamente";
        }
        return "Conductor no encontrado";
    }

    private List<Conductor> leerConductores() {
        try {
            File file = new File(archivoDatos);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<List<Conductor>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarConductores(List<Conductor> conductores) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(archivoDatos), conductores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}