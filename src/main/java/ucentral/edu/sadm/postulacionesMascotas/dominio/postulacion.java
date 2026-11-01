package ucentral.edu.sadm.postulacionesMascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.mascotas.dominio.mascota;

import java.time.Instant;

@Entity
@Table(
        name = "postulacion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_postulacion_adoptante_mascota",
                columnNames = {"adoptante_id", "mascota_id"}
        )
)
public class postulacion extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adoptante_id", nullable = false)
    public usuario adoptante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    public mascota mascota;

    @Column(name = "fecha_postulacion", nullable = false, updatable = false)
    public Instant fecha_postulacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public estadoPostulacion estado;

    @Column(nullable = false)
    public Integer puntaje = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant created_at;

    @Column(name = "updated_at", nullable = false)
    public Instant updated_at;

    @PrePersist
    void alCrear() {
        Instant ahora = Instant.now();
        if (fecha_postulacion == null) {
            fecha_postulacion = ahora;
        }
        if (estado == null) {
            estado = estadoPostulacion.ETAPA_1_EN_CURSO;
        }
        if (puntaje == null) {
            puntaje = 0;
        }
        created_at = ahora;
        updated_at = ahora;
    }

    @PreUpdate
    void alActualizar() {
        updated_at = Instant.now();
    }
}
