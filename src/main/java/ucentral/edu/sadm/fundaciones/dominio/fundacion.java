package ucentral.edu.sadm.fundaciones.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import ucentral.edu.sadm.usuarios.dominio.usuario;

@Entity
@Table(name = "fundacion")
@NoArgsConstructor
public class fundacion extends PanacheEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    public usuario usuario;

    @Column(nullable = false, length = 100)
    public String nombre;

    @Column(nullable = false, unique = true, length = 30)
    public String nit;

    @Column(nullable = false, length = 30)
    public String estado;
}
