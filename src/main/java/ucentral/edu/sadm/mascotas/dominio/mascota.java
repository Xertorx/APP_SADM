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
import ucentral.edu.sadm.usuarios.dominio.usuario;

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
    public usuario dador;

    @ElementCollection
    @CollectionTable(name = "mascota_temperamento", joinColumns = @JoinColumn(name = "mascota_id"))
    @Column(name = "temperamento")
    public List<String> temperamento;

    // HU10: coordenadas opcionales (nullable para no romper filas viejas ni HU06). Redondeadas a 3 decimales.
    // Nunca se devuelven en endpoints públicos: solo se usan para calcular distanciaKm.
    @Column(name = "latitud")
    public Double latitud;

    @Column(name = "longitud")
    public Double longitud;

    /** Constructor original (sin coordenadas): se conserva para no romper código existente. */
    public mascota(String nombre, String especie, String raza, Integer edad, String unidad_edad,
                   String tamano, String sexo, String historial_salud, String ubicacion, String estado,
                   Date fecha_publicacion, usuario dador, List<String> temperamento) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.unidad_edad = unidad_edad;
        this.tamano = tamano;
        this.sexo = sexo;
        this.historial_salud = historial_salud;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.fecha_publicacion = fecha_publicacion;
        this.dador = dador;
        this.temperamento = temperamento;
    }
}
