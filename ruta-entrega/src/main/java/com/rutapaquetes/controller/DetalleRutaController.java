package com.rutapaquetes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutapaquetes.model.DetalleRuta;
import com.rutapaquetes.model.RutaProgramada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/detalles")
public class DetalleRutaController {

    private final String archivoDetalles = "detalles.json";
    private final String archivoRutas = "rutas.json";
    private final ObjectMapper mapper = new ObjectMapper();

    // Listar detalles filtrados por ruta si se pasa rutaId, o todos si no se pasa
    @GetMapping
    public List<DetalleRuta> listarDetalles(@RequestParam(required = false) String rutaId) {
        List<DetalleRuta> detalles = leerDetalles();
        if (rutaId == null || rutaId.isEmpty()) {
            return detalles;
        }
        List<DetalleRuta> filtrados = new ArrayList<>();
        for (DetalleRuta d : detalles) {
            if (d.getRutaId().equalsIgnoreCase(rutaId)) {
                filtrados.add(d);
            }
        }
        return filtrados;
    }

    // Registrar nuevo detalle
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarDetalle(@RequestBody DetalleRuta nuevo) {
        if (!existeRuta(nuevo.getRutaId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No existe la ruta con id: " + nuevo.getRutaId());
        }

        List<DetalleRuta> detalles = leerDetalles();
        String nuevoId = "DET" + (detalles.size() + 1);
        nuevo.setId(nuevoId);

        detalles.add(nuevo);
        guardarDetalles(detalles);

        return ResponseEntity.ok("Detalle registrado correctamente con id: " + nuevoId);
    }

    // Actualizar detalle
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarDetalle(@PathVariable String id, @RequestBody DetalleRuta actualizado) {
        List<DetalleRuta> detalles = leerDetalles();
        boolean encontrado = false;

        for (DetalleRuta d : detalles) {
            if (d.getId().equals(id)) {
                if (!existeRuta(actualizado.getRutaId())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("No existe la ruta con id: " + actualizado.getRutaId());
                }
                d.setRutaId(actualizado.getRutaId());
                d.setDireccion(actualizado.getDireccion());
                d.setDescripcion(actualizado.getDescripcion());
                d.setLatitud(actualizado.getLatitud());
                d.setLongitud(actualizado.getLongitud());
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado");
        }

        guardarDetalles(detalles);
        return ResponseEntity.ok("Detalle actualizado correctamente");
    }

    // Eliminar detalle
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable String id) {
        List<DetalleRuta> detalles = leerDetalles();
        boolean eliminado = detalles.removeIf(d -> d.getId().equals(id));

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado");
        }

        guardarDetalles(detalles);
        return ResponseEntity.ok("Detalle eliminado correctamente");
    }

    // Métodos privados
    private List<DetalleRuta> leerDetalles() {
        try {
            File file = new File(archivoDetalles);
            if (file.exists()) {
                return mapper.readValue(file, new TypeReference<List<DetalleRuta>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void guardarDetalles(List<DetalleRuta> detalles) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(archivoDetalles), detalles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean existeRuta(String rutaId) {
        try {
            File file = new File(archivoRutas);
            if (file.exists()) {
                List<RutaProgramada> rutas = mapper.readValue(file, new TypeReference<List<RutaProgramada>>() {});
                return rutas.stream().anyMatch(r -> r.getId().equals(rutaId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}