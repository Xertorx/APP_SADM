package ucentral.edu.sadm.adoptantes.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "user_adoptante")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class adoptante extends PanacheEntity{

    public String nombre;
    public String apellido;
    public Integer identificacion;
    public Date fecha_nacimiento;
    public String correo;
    public String contraseña;
    public String telefono;
    public String tipo_usuario;
    public String direccion;
    public String estado;
}
