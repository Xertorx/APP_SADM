# APP_SADM
Sistema de adopción de mascotas.

## Cuentas y roles

Las cuentas personales se registran como `USUARIO`; la misma cuenta puede postularse a una adopción y publicar mascotas. Las fundaciones tienen un perfil propio vinculado a una cuenta con rol `FUNDACION`. Los administradores son cuentas con rol `ADMINISTRADOR` y no pueden registrarse públicamente.

Rutas públicas:

| Método y ruta | Descripción |
|---|---|
| `POST /SADM/api/usuarios/crear` | Registra una cuenta personal. Acepta nombre, apellido, identificación, fecha de nacimiento, correo, contraseña, teléfono y dirección. El servidor asigna el rol y el estado. |
| `POST /SADM/api/fundaciones/crear` | Registra una cuenta de fundación y su perfil (`nombre`, `nit`, representante, correo, contraseña, teléfono y dirección). El perfil queda pendiente de aprobación. |
| `POST /SADM/api/autenticacion/login` | Autentica por correo y contraseña y devuelve un JWT. |
| `GET /SADM/api/mascotas` | Lista mascotas. |
| `GET /SADM/api/mascotas/{id}` | Consulta una mascota. |
| `GET /SADM/api/mascotas/mis-publicaciones` | Lista las mascotas del dador autenticado (`USUARIO` o `FUNDACION`). |
| `GET /SADM/api/mascotas/{id}/editar` | Consulta, para el dador propietario, los datos precargables y advierte si existen postulaciones activas. |
| `PUT /SADM/api/mascotas/{id}` | Actualiza una publicación del dador autenticado. Recibe `multipart/form-data` con `datos` (el mismo JSON validado al publicar) y, opcionalmente, `fotos` (JPG/PNG, hasta 6 de máximo 5 MB); si no se envían fotos, conserva las existentes. |
| `POST /SADM/api/mascotas/crear` | Guarda la mascota como `BORRADOR_CONFIGURACION`; aún no la envía a revisión. |
| `GET /SADM/api/mascotas/{id}/configuracion-seleccion` | Consulta el borrador de configuración (o una configuración vacía), solo para el dador propietario. |
| `PUT /SADM/api/mascotas/{id}/configuracion-seleccion` | Crea o actualiza el borrador completo de las cuatro etapas. Los elementos con `id` se actualizan; los omitidos se eliminan. |
| `POST /SADM/api/mascotas/{id}/configuracion-seleccion/finalizar` | Finaliza si cada etapa tiene elementos, los puntajes caben en cada etapa y los máximos suman exactamente 100. |
| `POST /SADM/api/mascotas/{id}/publicar` | Envía a revisión únicamente una mascota con la configuración finalizada. |

El `PUT` recibe las cuatro propiedades de etapa (`preguntas`, `documentos`, `entrevista`, `visitaDomiciliaria`), cada una con `puntajeMaximo` y su lista (`elementos` o `criterios`). Tipos de respuesta: `TEXTO`, `SI_NO`, `SELECCION_UNICA` y `SELECCION_MULTIPLE`. Ejemplo:

```json
{
  "preguntas": {
    "puntajeMaximo": 30,
    "elementos": [{
      "texto": "¿Cuántas horas estaría sola la mascota?",
      "tipoRespuesta": "SELECCION_UNICA",
      "puntaje": 10,
      "opciones": ["Menos de 2", "De 2 a 5", "Más de 5"]
    }]
  },
  "documentos": {
    "puntajeMaximo": 20,
    "elementos": [{
      "nombre": "Documento de identidad",
      "descripcion": "Copia legible del documento",
      "obligatorio": true,
      "puntajeMaximo": 20
    }]
  },
  "entrevista": {
    "puntajeMaximo": 25,
    "criterios": [{"descripcion": "Experiencia previa con mascotas", "puntajeMaximo": 25}]
  },
  "visitaDomiciliaria": {
    "puntajeMaximo": 25,
    "criterios": [{"descripcion": "Espacio seguro y adecuado", "puntajeMaximo": 25}]
  }
}
```

La respuesta del `PUT` incluye los identificadores persistidos y la distribución (`puntajeDistribuido`, `puntosPendientes`, `puntosExcedentes`). El mismo formato con los IDs permite editar elementos sin cambiar su identidad. Los borradores pueden estar incompletos; solo `finalizar` los hace definitivos. La configuración queda bloqueada cuando la mascota se envía a revisión. Ejecuta manualmente `V4__configuracion_seleccion.sql` después de las migraciones V1-V3 en bases PostgreSQL existentes; en desarrollo, Hibernate actualiza el esquema.

Después de crear una cuenta administrativa de confianza, un administrador de la base de datos puede asignarle el rol:

```sql
UPDATE usuario SET rol = 'ADMINISTRADOR' WHERE correo = lower('admin@example.com');
```

Los administradores pueden consultar usuarios y fundaciones en `GET /SADM/api/usuarios` y `GET /SADM/api/fundaciones`, y aprobar una fundación con `PATCH /SADM/api/fundaciones/{id}/aprobar`. Una fundación solo puede publicar mascotas cuando su perfil está aprobado.

## Sesión JWT

El login recibe `{"correo":"usuario@example.com","contraseña":"ClaveSegura123"}` y responde con `data.accessToken`, `data.tokenType` (`Bearer`) y `data.expiresIn`. En las rutas protegidas se envía `Authorization: Bearer <accessToken>`. `POST /SADM/api/autenticacion/sesion` permite verificar que el token siga vigente.

Los JWT están firmados con HS256 y expiran en 900 segundos por defecto. Configura `JWT_SECRET` con un secreto aleatorio de al menos 32 bytes; opcionalmente define `JWT_EXPIRATION_SECONDS`. No se incluye un secreto de producción en el repositorio. Al ser sesiones sin estado, un token no puede revocarse individualmente antes de expirar: el cliente debe volver a iniciar sesión al vencer.

Roles protegidos: `USUARIO` puede postularse; `USUARIO` y `FUNDACION` pueden publicar mascotas; `ADMINISTRADOR` puede consultar usuarios/fundaciones y aprobar fundaciones. El identificador del dador se toma de la identidad autenticada, no del cuerpo de la petición.
El formulario de edición requiere JWT con rol `USUARIO` o `FUNDACION`; solo el dador propietario puede consultarlo o guardar cambios.

## Postulación a una mascota

Las rutas requieren JWT con rol `USUARIO`. El servidor resuelve el usuario a partir del correo autenticado y establece el `adoptante_id`; no acepta ese identificador del cliente.

| Método y ruta | Descripción |
|---|---|
| `GET /SADM/api/postulaciones/mascotas/{mascotaId}/resumen` | Consulta la mascota, las etapas del proceso y si el usuario puede postularse; no escribe en la base de datos. |
| `POST /SADM/api/postulaciones` | Crea la postulación del usuario autenticado. Body: `{"mascotaId": 12}`. |

Ejemplo de postulación creada (`201`):

```json
{
  "status": 201,
  "message": "Postulación creada correctamente",
  "data": {
    "id": 34,
    "mascotaId": 12,
    "adoptanteId": 8,
    "fecha": "2026-10-14T23:30:00Z",
    "estado": "Etapa 1 en curso",
    "puntaje": 0
  }
}
```

Los errores de negocio conservan el sobre `status`/`message`/`data`, con `codigo` y `detalle` dentro de `data`. Los códigos de conflicto son `MASCOTA_NO_DISPONIBLE` y `POSTULACION_DUPLICADA`; mascota inexistente devuelve `404`. El resumen incluye `puedePostularse` y un `motivo` con uno de esos códigos cuando no puede postularse.

## Migración de base de datos

El proyecto no tiene Flyway/Liquibase; Hibernate conserva su estrategia de actualización de esquema. Antes de desplegar sobre una base existente, detén la aplicación, respalda la base y ejecuta las migraciones manualmente en orden: `V2__migrar_adoptantes_a_usuarios.sql` renombra o copia las cuentas antiguas, actualiza las claves foráneas y crea la tabla de fundaciones; `V3__identificacion_bigint.sql` amplía la identificación a `BIGINT`; `V4__configuracion_seleccion.sql` crea las tablas de HU12. La migración V1 de postulaciones referencia `usuario` y se aplica después de que existan las tablas `usuario` y `mascota`.
