package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Table(name = "foto_mascota")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class fotoMascota extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    public mascota mascota;

    @Column(name = "nombre_archivo")
    public String nombre_archivo;

    @Column(name = "tipo_contenido")
    public String tipo_contenido;

    @Column(name = "tamano_bytes")
    public Long tamano_bytes;

    @Column(name = "datos", columnDefinition = "bytea")
    public byte[] datos;

    public Integer orden;
}
