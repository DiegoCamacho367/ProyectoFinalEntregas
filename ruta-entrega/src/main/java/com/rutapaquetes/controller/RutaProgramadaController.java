package com.rutapaquetes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutapaquetes.model.Conductor;
import com.rutapaquetes.model.Vehiculo;
import com.rutapaquetes.model.RutaProgramada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rutas")
public class RutaProgramadaController {

    private final String archivoRutas = "rutas.json";
    private final String archivoConductores = "conductores.json";
    private final String archivoVehiculos = "vehiculos.json";
    private final String archivoDetalles = "detalles.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public List<RutaProgramada> listarRutas() {
        return leerRutas();
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarRuta(@RequestBody RutaProgramada ruta) {
        List<RutaProgramada> rutas = leerRutas();

        if (!existeConductor(ruta.getConductorId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "No existe el conductor con id: " + ruta.getConductorId()));
        }
        if (!existeVehiculo(ruta.getVehiculoId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "No existe el vehículo con id: " + ruta.getVehiculoId()));
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
                    return ResponseEntity.badRequest().body(Map.of("error", "No existe el conductor con id: " + actualizada.getConductorId()));
                }
                if (!existeVehiculo(actualizada.getVehiculoId())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "No existe el vehículo con id: " + actualizada.getVehiculoId()));
                }
                r.setConductorId(actualizada.getConductorId());
                r.setVehiculoId(actualizada.getVehiculoId());
                r.setFecha(actualizada.getFecha());
                r.setNombreRuta(actualizada.getNombreRuta());
                guardarRutas(rutas);
                return ResponseEntity.ok(Map.of("mensaje", "Ruta actualizada correctamente"));
            }
        }
        return ResponseEntity.status(404).body(Map.of("error", "Ruta no encontrada"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable String id) {
        // Validar que no tenga detalles asignados
        try {
            File fileDetalles = new File(archivoDetalles);
            if (fileDetalles.exists()) {
                var detalles = mapper.readValue(fileDetalles, new TypeReference<List<Object>>() {});
                boolean tieneDetalles = detalles.stream()
                        .anyMatch(d -> ((java.util.LinkedHashMap)d).get("rutaId").equals(id));
                if (tieneDetalles) {
                    return ResponseEntity.badRequest().body(Map.of("error","No se puede eliminar la ruta porque tiene detalles asignados"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error","Error validando detalles de ruta"));
        }

        List<RutaProgramada> rutas = leerRutas();
        if (rutas.removeIf(r -> r.getId().equals(id))) {
            guardarRutas(rutas);
            return ResponseEntity.ok(Map.of("mensaje", "Ruta eliminada correctamente"));
        }
        return ResponseEntity.status(404).body(Map.of("error","Ruta no encontrada"));
    }

    @GetMapping("/buscar")
    public List<RutaProgramada> buscarRutas(
            @RequestParam(required = false) String vehiculo,
            @RequestParam(required = false) String conductor,
            @RequestParam(required = false) String fecha
    ) {
        List<RutaProgramada> rutas = leerRutas();

        if (vehiculo != null && !vehiculo.isEmpty()) {
            rutas = rutas.stream()
                    .filter(r -> r.getVehiculoId().equalsIgnoreCase(vehiculo))
                    .toList();
        }
        if (conductor != null && !conductor.isEmpty()) {
            rutas = rutas.stream()
                    .filter(r -> r.getConductorId().equalsIgnoreCase(conductor))
                    .toList();
        }
        if (fecha != null && !fecha.isEmpty()) {
            rutas = rutas.stream()
                    .filter(r -> r.getFecha().equals(fecha))
                    .toList();
        }
        return rutas;
    }

    // ----------------------------------------------------------
    // MÉTODOS PRIVADOS
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