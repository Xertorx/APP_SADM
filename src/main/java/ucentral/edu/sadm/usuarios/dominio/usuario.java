package ucentral.edu.sadm.usuarios.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "usuario")
@Entity
@NoArgsConstructor
public class usuario extends PanacheEntity {

    public String nombre;
    public String apellido;

    @Column(columnDefinition = "BIGINT")
    public Long identificacion;

    @Column(name = "fecha_nacimiento")
    public Date fecha_nacimiento;

    @Column(nullable = false, unique = true)
    public String correo;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    public String telefono;
    public String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public rolUsuario rol;

    @Column(nullable = false, length = 20)
    public String estado;
}
