package com.gps.mx.laviga.dev3.repository;

import com.gps.mx.laviga.dev3.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Búsqueda inteligente: elimina puntos, comas y términos comunes de régimen fiscal
    @Query(value = "SELECT * FROM proveedores WHERE " +
           "REPLACE(REPLACE(REPLACE(LOWER(nombre), ' s.a. de c.v.', ''), ' sa de cv', ''), ' s de rl de cv', '') " +
           "LIKE LOWER(CONCAT('%', :nombreCfdi, '%')) LIMIT 1", 
           nativeQuery = true)
    Optional<Proveedor> findProveedorByNombreFlexible(@Param("nombreCfdi") String nombreCfdi);
}