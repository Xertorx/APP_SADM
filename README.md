# APP_SADM
Sistema de adopción de mascotas

## Consulta de mascotas

`GET /SADM/mascotas` devuelve la lista de mascotas registradas, sin filtrar por estado. Cada elemento incluye sus datos, el identificador del dador y los identificadores de sus fotos.

## Postulación a una mascota

Las siguientes rutas se publican bajo el `root-path` actual `/SADM` y requieren una identidad autenticada con el rol `ADOPTANTE`. La identidad del proveedor de seguridad debe usar como nombre de principal el correo registrado del adoptante; el servidor resuelve con ese correo el `adoptante_id`. La aplicación aún no define un proveedor de autenticación propio.

| Método y ruta | Descripción |
|---|---|
| `GET /SADM/api/mascotas/{mascotaId}/postulacion/resumen` | Consulta la mascota, las etapas del proceso y si el adoptante puede postularse; no escribe en la base de datos. |
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

La migración PostgreSQL inicial está en `src/main/resources/db/migration/V1__create_postulacion.sql`. El proyecto todavía no tiene Flyway/Liquibase; aplícala después de que existan las tablas `user_adoptante` y `mascota`. Hibernate conserva su estrategia de actualización de esquema existente. Las etapas del proceso están definidas como constante en `postulacionServicio` para reemplazarlas cuando se incorpore un catálogo.
