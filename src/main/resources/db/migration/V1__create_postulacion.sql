CREATE TABLE postulacion (
    id BIGSERIAL PRIMARY KEY,
    adoptante_id BIGINT NOT NULL,
    mascota_id BIGINT NOT NULL,
    fecha_postulacion TIMESTAMP WITH TIME ZONE NOT NULL,
    estado VARCHAR(40) NOT NULL DEFAULT 'ETAPA_1_EN_CURSO',
    puntaje INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_postulacion_adoptante FOREIGN KEY (adoptante_id) REFERENCES user_adoptante (id),
    CONSTRAINT fk_postulacion_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (id),
    CONSTRAINT uk_postulacion_adoptante_mascota UNIQUE (adoptante_id, mascota_id)
);
