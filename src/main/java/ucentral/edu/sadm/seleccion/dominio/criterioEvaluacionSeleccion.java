package ucentral.edu.sadm.seleccion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "criterio_evaluacion_seleccion")
public class criterioEvaluacionSeleccion extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    public etapaSeleccion etapa;

    @Column(nullable = false, length = 1000)
    public String descripcion;

    @Column(name = "puntaje_maximo", nullable = false)
    public Integer puntajeMaximo;
}
