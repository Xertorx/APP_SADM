package ucentral.edu.sadm.seleccion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "etapa_seleccion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_etapa_seleccion_configuracion_tipo",
                columnNames = {"configuracion_id", "tipo"}
        )
)
public class etapaSeleccion extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_id", nullable = false)
    public configuracionSeleccion configuracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public tipoEtapaSeleccion tipo;

    @Column(name = "puntaje_maximo", nullable = false)
    public Integer puntajeMaximo = 0;

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<preguntaSeleccion> preguntas = new ArrayList<>();

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<documentoSeleccion> documentos = new ArrayList<>();

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<criterioEvaluacionSeleccion> criterios = new ArrayList<>();
}
