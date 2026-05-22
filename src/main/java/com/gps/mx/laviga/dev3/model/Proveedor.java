package com.gps.mx.laviga.dev3.model;

import javax.persistence.*;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "nombre")
    private String nombre;

    public Long getId() { return id; }
    public String getVendorId() { return vendorId; }
    public String getNombre() { return nombre; }
}