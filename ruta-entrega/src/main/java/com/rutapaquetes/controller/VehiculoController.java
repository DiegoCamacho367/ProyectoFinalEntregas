package com.rutapaquetes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutapaquetes.model.Vehiculo;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final String archivoDatos = "vehiculos.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/registrar")
    public String registrarVehiculo(@RequestBody Vehiculo vehiculo) {
        List<Vehiculo> vehiculos = leerVehiculos();
        String nuevoId = "VEH" + (vehiculos.size() + 1);
        vehiculo.setId(nuevoId);
        vehiculos.add(vehiculo);
        guardarVehiculos(vehiculos);
        return "Vehículo registrado correctamente con id: " + nuevoId;
    }

    @GetMapping
    public List<Vehiculo> listarVehiculos() {
        return leerVehiculos();
    }

    @PutMapping("/actualizar/{id}")
    public String actualizarVehiculo(@PathVariable String id, @RequestBody Vehiculo vehiculoActualizado) {
        List<Vehiculo> vehiculos = leerVehiculos();
        for (Vehiculo v : vehiculos) {
            if (v.getId().equals(id)) {
                v.setPlaca(vehiculoActualizado.getPlaca());
                v.setMarca(vehiculoActualizado.getMarca());
                v.setModelo(vehiculoActualizado.getModelo());
                v.setColor(vehiculoActualizado.getColor());
                v.setCapacidadCarga(vehiculoActualizado.getCapacidadCarga());
                guardarVehiculos(vehiculos);
                return "Vehículo actualizado correctamente";
            }
        }
        return "Vehículo no encontrado";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminarVehiculo(@PathVariable String id) {
        List<Vehiculo> vehiculos = leerVehiculos();
        if (vehiculos.removeIf(v -> v.getId().equals(id))) {
            guardarVehiculos(vehiculos);
            return "Vehículo eliminado correctamente";
        }
        return "Vehículo no encontrado";
    }

    private List<Vehiculo> leerVehiculos() {
        try {
            File file = new File(archivoDatos);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<List<Vehiculo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarVehiculos(List<Vehiculo> vehiculos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(archivoDatos), vehiculos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}