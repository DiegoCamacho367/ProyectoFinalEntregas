package com.rutapaquetes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutapaquetes.model.Conductor;
import com.rutapaquetes.model.Vehiculo;
import com.rutapaquetes.model.RutaProgramada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rutas")
public class RutaProgramadaController {

    private final String archivoRutas = "rutas.json";
    private final String archivoConductores = "conductores.json";
    private final String archivoVehiculos = "vehiculos.json";
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public List<RutaProgramada> listarRutas() {
        return leerRutas();
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarRuta(@RequestBody RutaProgramada ruta) {
        List<RutaProgramada> rutas = leerRutas();

        if (!existeConductor(ruta.getConductorId())) {
            return ResponseEntity.badRequest().body("No existe el conductor con id: " + ruta.getConductorId());
        }
        if (!existeVehiculo(ruta.getVehiculoId())) {
            return ResponseEntity.badRequest().body("No existe el vehículo con id: " + ruta.getVehiculoId());
        }

        ruta.setId("RUT" + (rutas.size() + 1));
        rutas.add(ruta);
        guardarRutas(rutas);
        return ResponseEntity.ok(ruta);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarRuta(@PathVariable String id, @RequestBody RutaProgramada actualizada) {
        List<RutaProgramada> rutas = leerRutas();
        for (RutaProgramada r : rutas) {
            if (r.getId().equals(id)) {
                if (!existeConductor(actualizada.getConductorId())) {
                    return ResponseEntity.badRequest().body("No existe el conductor con id: " + actualizada.getConductorId());
                }
                if (!existeVehiculo(actualizada.getVehiculoId())) {
                    return ResponseEntity.badRequest().body("No existe el vehículo con id: " + actualizada.getVehiculoId());
                }
                r.setConductorId(actualizada.getConductorId());
                r.setVehiculoId(actualizada.getVehiculoId());
                r.setFecha(actualizada.getFecha());
                r.setNombreRuta(actualizada.getNombreRuta());
                guardarRutas(rutas);
                return ResponseEntity.ok("Ruta actualizada correctamente");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no encontrada");
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable String id) {
        List<RutaProgramada> rutas = leerRutas();
        if (rutas.removeIf(r -> r.getId().equals(id))) {
            guardarRutas(rutas);
            return ResponseEntity.ok("Ruta eliminada correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ruta no encontrada");
    }

    // ----------------------------------------------------------
    // MÉTODO PARA FILTRAR rutas por vehiculo, conductor, fecha
    // ----------------------------------------------------------

    @GetMapping("/buscar")
    public List<RutaProgramada> buscarRutas(
            @RequestParam(required = false) String vehiculo,
            @RequestParam(required = false) String conductor,
            @RequestParam(required = false) String fecha
    ) {
        List<RutaProgramada> rutas = leerRutas();

        if (vehiculo != null) {
            rutas = rutas.stream()
                    .filter(r -> r.getVehiculoId().equalsIgnoreCase(vehiculo))
                    .toList();
        }
        if (conductor != null) {
            rutas = rutas.stream()
                    .filter(r -> r.getConductorId().equalsIgnoreCase(conductor))
                    .toList();
        }
        if (fecha != null) {
            rutas = rutas.stream()
                    .filter(r -> r.getFecha().equals(fecha))
                    .toList();
        }
        return rutas;
    }

    // ----------------------------------------------------------
    // MÉTODOS PRIVADOS lectura / escritura de archivos
    // ----------------------------------------------------------

    private List<RutaProgramada> leerRutas() {
        try {
            File file = new File(archivoRutas);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<RutaProgramada>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarRutas(List<RutaProgramada> rutas) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(archivoRutas), rutas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean existeConductor(String id) {
        try {
            File file = new File(archivoConductores);
            if (file.exists()) {
                List<Conductor> lista = mapper.readValue(file, new TypeReference<List<Conductor>>() {});
                return lista.stream().anyMatch(c -> c.getId().equals(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existeVehiculo(String id) {
        try {
            File file = new File(archivoVehiculos);
            if (file.exists()) {
                List<Vehiculo> lista = mapper.readValue(file, new TypeReference<List<Vehiculo>>() {});
                return lista.stream().anyMatch(v -> v.getId().equals(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}