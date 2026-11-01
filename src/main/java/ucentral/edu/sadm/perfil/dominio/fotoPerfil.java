package ucentral.edu.sadm.perfil.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import ucentral.edu.sadm.usuarios.dominio.usuario;

import java.time.Instant;

@Table(name = "foto_perfil")
@Entity
@NoArgsConstructor
public class fotoPerfil extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    public usuario usuario;

    @Column(name = "nombre_archivo")
    public String nombre_archivo;

    @Column(name = "tipo_contenido")
    public String tipo_contenido;

    @Column(name = "tamano_bytes")
    public Long tamano_bytes;

    @Column(name = "datos", columnDefinition = "bytea")
    public byte[] datos;

    @Column(name = "actualizada_en")
    public Instant actualizada_en;
}
