package ucentral.edu.sadm.administracion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class usuarioModeracionRepositorio implements PanacheRepository<usuarioModeracion> {

    /** Más reciente primero; trae al admin en la misma consulta (la relación es lazy). */
    public List<usuarioModeracion> listarPorUsuario(Long usuarioId) {
        return find("select m from usuarioModeracion m join fetch m.admin "
                + "where m.usuario.id = ?1 order by m.fecha desc, m.id desc", usuarioId).list();
    }
}
