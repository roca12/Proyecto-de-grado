package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Diagnostic PersonaController with extensive troubleshooting capabilities
 */
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private DataSource dataSource;

    /**
     * Comprehensive diagnostic endpoint
     */
    @GetMapping(value = "/diagnostic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> diagnostic() {
        Map<String, Object> diagnosticInfo = new HashMap<>();

        try {
            // 1. Check Spring Context
            diagnosticInfo.put("springContextLoaded", applicationContext != null);
            diagnosticInfo.put("availableBeans", applicationContext != null ?
                    Arrays.asList(applicationContext.getBeanDefinitionNames()).size() : 0);

            // 2. Check PersonaService injection
            diagnosticInfo.put("personaServiceInjected", personaService != null);
            if (personaService != null) {
                diagnosticInfo.put("personaServiceClass", personaService.getClass().getName());
            }

            // 3. Check DataSource
            diagnosticInfo.put("dataSourceInjected", dataSource != null);
            if (dataSource != null) {
                try {
                    Connection connection = dataSource.getConnection();
                    diagnosticInfo.put("databaseConnectionAvailable", true);
                    diagnosticInfo.put("databaseUrl", connection.getMetaData().getURL());
                    connection.close();
                } catch (Exception e) {
                    diagnosticInfo.put("databaseConnectionAvailable", false);
                    diagnosticInfo.put("databaseError", e.getMessage());
                }
            }

            // 4. Try direct database query
            if (dataSource != null) {
                try {
                    Connection connection = dataSource.getConnection();
                    Statement stmt = connection.createStatement();

                    // Check if personas table exists
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM personas");
                    if (rs.next()) {
                        diagnosticInfo.put("personasTableExists", true);
                        diagnosticInfo.put("personasTableCount", rs.getInt("count"));
                    }
                    rs.close();

                    // Get table structure
                    rs = stmt.executeQuery("SELECT * FROM personas LIMIT 1");
                    int columnCount = rs.getMetaData().getColumnCount();
                    List<String> columns = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(rs.getMetaData().getColumnName(i));
                    }
                    diagnosticInfo.put("personasTableColumns", columns);
                    rs.close();

                    stmt.close();
                    connection.close();
                } catch (Exception e) {
                    diagnosticInfo.put("directDatabaseQueryError", e.getMessage());
                }
            }

            // 5. Try PersonaService call with error capture
            if (personaService != null) {
                try {
                    List<Persona> personas = personaService.getAllPersonas();
                    diagnosticInfo.put("personaServiceCallSuccessful", true);
                    diagnosticInfo.put("personaServiceResultSize", personas != null ? personas.size() : "null");
                } catch (Exception e) {
                    diagnosticInfo.put("personaServiceCallSuccessful", false);
                    diagnosticInfo.put("personaServiceError", e.getMessage());
                    diagnosticInfo.put("personaServiceErrorType", e.getClass().getName());

                    // Get stack trace
                    List<String> stackTrace = new ArrayList<>();
                    for (StackTraceElement element : e.getStackTrace()) {
                        stackTrace.add(element.toString());
                        if (stackTrace.size() > 10) break; // Limit stack trace
                    }
                    diagnosticInfo.put("personaServiceStackTrace", stackTrace);
                }
            }

            return ResponseEntity.ok(diagnosticInfo);

        } catch (Exception e) {
            diagnosticInfo.put("diagnosticError", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(diagnosticInfo);
        }
    }

    /**
     * Test endpoint that always works
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "PersonaController está funcionando correctamente");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }

    /**
     * Raw database query endpoint
     */
    @GetMapping(value = "/raw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRawPersonas() {
        if (dataSource == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Collections.singletonMap("error", "DataSource no disponible"));
        }

        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM personas");

            List<Map<String, Object>> personas = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> persona = new HashMap<>();
                persona.put("idPersona", rs.getObject("id_persona"));
                persona.put("nombre", rs.getString("nombre"));
                persona.put("apellido", rs.getString("apellido"));
                // Add other fields as needed
                personas.add(persona);
            }

            rs.close();
            stmt.close();
            connection.close();

            return ResponseEntity.ok(personas);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error en consulta directa: " + e.getMessage());
            error.put("errorType", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Main endpoint with enhanced error handling
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPersonas() {
        Map<String, Object> debugInfo = new HashMap<>();

        try {
            System.out.println("=== PersonaController.getAllPersonas() STARTED ===");
            debugInfo.put("step", "started");

            if (personaService == null) {
                System.err.println("ERROR: PersonaService is null");
                debugInfo.put("error", "PersonaService is null");
                debugInfo.put("step", "service_null_check");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(debugInfo);
            }

            System.out.println("PersonaService is available: " + personaService.getClass().getName());
            debugInfo.put("step", "service_available");
            debugInfo.put("serviceClass", personaService.getClass().getName());

            List<Persona> personas = personaService.getAllPersonas();
            System.out.println("PersonaService.getAllPersonas() returned: " + (personas != null ? personas.size() + " items" : "null"));

            if (personas == null) {
                System.err.println("PersonaService returned null");
                debugInfo.put("step", "service_returned_null");
                return ResponseEntity.ok(Collections.emptyList());
            }

            System.out.println("=== PersonaController.getAllPersonas() SUCCESS ===");
            return ResponseEntity.ok(personas);

        } catch (Exception e) {
            System.err.println("=== PersonaController.getAllPersonas() ERROR ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();

            debugInfo.put("step", "exception_caught");
            debugInfo.put("errorType", e.getClass().getName());
            debugInfo.put("errorMessage", e.getMessage());

            // Return empty list to prevent frontend crashes
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Get persona by ID with enhanced error handling
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPersonaById(@PathVariable Integer id) {
        try {
            if (personaService == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Collections.singletonMap("error", "PersonaService no disponible"));
            }

            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "ID inválido"));
            }

            Persona persona = personaService.getPersonaById(id);

            if (persona != null) {
                return ResponseEntity.ok(persona);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Persona no encontrada"));
            }

        } catch (Exception e) {
            System.err.println("Error in getPersonaById: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error interno: " + e.getMessage()));
        }
    }
}