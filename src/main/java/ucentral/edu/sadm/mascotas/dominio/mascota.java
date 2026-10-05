package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;

import java.util.Date;
import java.util.List;

@Table(name = "mascota")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class mascota extends PanacheEntity {

    public String nombre;
    public String especie;
    public String raza;
    public Integer edad;

    @Column(name = "unidad_edad")
    public String unidad_edad;

    public String tamano;
    public String sexo;

    @Column(name = "historial_salud", length = 1000)
    public String historial_salud;

    public String ubicacion;
    public String estado;

    @Column(name = "fecha_publicacion")
    public Date fecha_publicacion;

    @ManyToOne
    @JoinColumn(name = "dador_id")
    public adoptante dador;

    @ElementCollection
    @CollectionTable(name = "mascota_temperamento", joinColumns = @JoinColumn(name = "mascota_id"))
    @Column(name = "temperamento")
    public List<String> temperamento;
}
