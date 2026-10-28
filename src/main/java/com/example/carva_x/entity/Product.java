package com.example.carva_x.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name; // Nombre

    @Column(nullable = false)
    private Number price; // Precio

    @Column(nullable = false)
    private Integer amount; // Cantidad

    @Column(nullable = false)
    private String description; // Descripcion

    @Column(nullable = false)
    private Number tax; // Impuesto

        // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
