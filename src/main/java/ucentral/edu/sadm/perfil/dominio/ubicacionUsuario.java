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

/**
 * HU10: ubicación de referencia del usuario (respaldo cuando niega el GPS).
 * Tabla propia: no se agregan columnas a «usuario» (es de otra HU). Una fila por usuario.
 */
@Table(name = "ubicacion_usuario")
@Entity
@NoArgsConstructor
public class ubicacionUsuario extends PanacheEntity {

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    public usuario usuario;

    @Column(name = "latitud", nullable = false)
    public Double latitud;

    @Column(name = "longitud", nullable = false)
    public Double longitud;

    /** Código de la localidad elegida (null si vino del GPS). */
    @Column(name = "localidad", length = 30)
    public String localidad;

    @Column(name = "fecha_actualizacion", nullable = false)
    public Instant fecha_actualizacion;
}
