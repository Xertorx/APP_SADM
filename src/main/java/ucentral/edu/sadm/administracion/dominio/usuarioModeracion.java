package ucentral.edu.sadm.administracion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import ucentral.edu.sadm.usuarios.dominio.usuario;

import java.time.Instant;

/** HU28: historial de inhabilitaciones/reactivaciones. Tabla propia: no se agregan columnas a usuario. */
@Entity
@Table(name = "usuario_moderacion")
@NoArgsConstructor
public class usuarioModeracion extends PanacheEntity {

    public static final String INHABILITAR = "INHABILITAR";
    public static final String REACTIVAR = "REACTIVAR";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    public usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    public usuario admin;

    @Column(nullable = false, length = 15)
    public String accion;

    @Column(length = 300)
    public String motivo;

    @Column(nullable = false)
    public Instant fecha;

    @PrePersist
    void alCrear() {
        if (fecha == null) {
            fecha = Instant.now();
        }
    }
}
