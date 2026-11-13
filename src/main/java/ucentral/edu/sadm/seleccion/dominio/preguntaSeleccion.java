package ucentral.edu.sadm.seleccion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pregunta_seleccion")
public class preguntaSeleccion extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    public etapaSeleccion etapa;

    @Column(nullable = false, length = 500)
    public String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_respuesta", nullable = false, length = 32)
    public tipoRespuestaSeleccion tipoRespuesta;

    @Column(nullable = false)
    public Integer puntaje;

    @ElementCollection
    @CollectionTable(name = "opcion_pregunta", joinColumns = @JoinColumn(name = "pregunta_id"))
    @OrderColumn(name = "orden")
    @Column(name = "opcion", nullable = false, length = 200)
    public List<String> opciones = new ArrayList<>();
}
