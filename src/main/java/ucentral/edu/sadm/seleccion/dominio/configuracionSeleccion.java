package ucentral.edu.sadm.seleccion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ucentral.edu.sadm.mascotas.dominio.mascota;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "configuracion_seleccion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_configuracion_seleccion_mascota",
                columnNames = "mascota_id"
        )
)
public class configuracionSeleccion extends PanacheEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    public mascota mascota;

    @Column(nullable = false)
    public boolean definitiva;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant created_at;

    @Column(name = "updated_at", nullable = false)
    public Instant updated_at;

    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tipo ASC")
    public List<etapaSeleccion> etapas = new ArrayList<>();

    public void agregarEtapa(etapaSeleccion etapa) {
        etapa.configuracion = this;
        etapas.add(etapa);
    }

    @jakarta.persistence.PrePersist
    void alCrear() {
        Instant ahora = Instant.now();
        created_at = ahora;
        updated_at = ahora;
    }

    @jakarta.persistence.PreUpdate
    void alActualizar() {
        updated_at = Instant.now();
    }
}
