CREATE TABLE configuracion_seleccion (
    id BIGSERIAL PRIMARY KEY,
    mascota_id BIGINT NOT NULL,
    definitiva BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_configuracion_seleccion_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascota (id) ON DELETE CASCADE,
    CONSTRAINT uk_configuracion_seleccion_mascota UNIQUE (mascota_id)
);

CREATE TABLE etapa_seleccion (
    id BIGSERIAL PRIMARY KEY,
    configuracion_id BIGINT NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    puntaje_maximo INTEGER NOT NULL DEFAULT 0 CHECK (puntaje_maximo BETWEEN 0 AND 100),
    CONSTRAINT fk_etapa_seleccion_configuracion
        FOREIGN KEY (configuracion_id) REFERENCES configuracion_seleccion (id) ON DELETE CASCADE,
    CONSTRAINT uk_etapa_seleccion_configuracion_tipo UNIQUE (configuracion_id, tipo),
    CONSTRAINT ck_etapa_seleccion_tipo
        CHECK (tipo IN ('PREGUNTAS', 'DOCUMENTOS', 'ENTREVISTA', 'VISITA_DOMICILIARIA'))
);

CREATE TABLE pregunta_seleccion (
    id BIGSERIAL PRIMARY KEY,
    etapa_id BIGINT NOT NULL,
    texto VARCHAR(500) NOT NULL,
    tipo_respuesta VARCHAR(32) NOT NULL,
    puntaje INTEGER NOT NULL CHECK (puntaje BETWEEN 0 AND 100),
    CONSTRAINT fk_pregunta_seleccion_etapa
        FOREIGN KEY (etapa_id) REFERENCES etapa_seleccion (id) ON DELETE CASCADE,
    CONSTRAINT ck_pregunta_seleccion_tipo
        CHECK (tipo_respuesta IN ('TEXTO', 'SI_NO', 'SELECCION_UNICA', 'SELECCION_MULTIPLE'))
);

CREATE TABLE opcion_pregunta (
    pregunta_id BIGINT NOT NULL,
    orden INTEGER NOT NULL,
    opcion VARCHAR(200) NOT NULL,
    PRIMARY KEY (pregunta_id, orden),
    CONSTRAINT fk_opcion_pregunta_pregunta
        FOREIGN KEY (pregunta_id) REFERENCES pregunta_seleccion (id) ON DELETE CASCADE
);

CREATE TABLE documento_seleccion (
    id BIGSERIAL PRIMARY KEY,
    etapa_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1000),
    obligatorio BOOLEAN NOT NULL,
    puntaje_maximo INTEGER NOT NULL CHECK (puntaje_maximo BETWEEN 0 AND 100),
    CONSTRAINT fk_documento_seleccion_etapa
        FOREIGN KEY (etapa_id) REFERENCES etapa_seleccion (id) ON DELETE CASCADE
);

CREATE TABLE criterio_evaluacion_seleccion (
    id BIGSERIAL PRIMARY KEY,
    etapa_id BIGINT NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    puntaje_maximo INTEGER NOT NULL CHECK (puntaje_maximo BETWEEN 0 AND 100),
    CONSTRAINT fk_criterio_evaluacion_seleccion_etapa
        FOREIGN KEY (etapa_id) REFERENCES etapa_seleccion (id) ON DELETE CASCADE
);
