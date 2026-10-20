DO $$
DECLARE
    foreign_key RECORD;
BEGIN
    IF to_regclass('public.user_adoptante') IS NOT NULL
       AND to_regclass('public.usuario') IS NULL THEN
        ALTER TABLE user_adoptante RENAME TO usuario;
    ELSIF to_regclass('public.user_adoptante') IS NOT NULL
       AND to_regclass('public.usuario') IS NOT NULL THEN
        ALTER TABLE usuario
            ADD COLUMN IF NOT EXISTS rol VARCHAR(30) NOT NULL DEFAULT 'USUARIO';

        FOR foreign_key IN
            SELECT conrelid::regclass AS tabla, conname
            FROM pg_constraint
            WHERE contype = 'f'
              AND confrelid = 'public.user_adoptante'::regclass
        LOOP
            EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', foreign_key.tabla, foreign_key.conname);
        END LOOP;

        INSERT INTO usuario (
            id, nombre, apellido, identificacion, fecha_nacimiento, correo,
            password_hash, telefono, direccion, estado, rol
        )
        SELECT
            id, nombre, apellido, identificacion, fecha_nacimiento, correo,
            contraseña, telefono, direccion, estado, 'USUARIO'
        FROM user_adoptante
        ON CONFLICT (id) DO NOTHING;

        DROP TABLE user_adoptante;
    END IF;
END
$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'usuario'
          AND column_name = 'contraseña'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'usuario'
          AND column_name = 'password_hash'
    ) THEN
        ALTER TABLE usuario RENAME COLUMN "contraseña" TO password_hash;
    END IF;
END
$$;

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS rol VARCHAR(30) NOT NULL DEFAULT 'USUARIO';

CREATE UNIQUE INDEX IF NOT EXISTS uk_usuario_correo_ci
    ON usuario (lower(correo));

CREATE SEQUENCE IF NOT EXISTS usuario_seq START WITH 1 INCREMENT BY 50;

DO $$
DECLARE
    max_usuario_id BIGINT;
BEGIN
    SELECT MAX(id) INTO max_usuario_id FROM usuario;
    IF max_usuario_id IS NULL THEN
        PERFORM setval('usuario_seq', 1, false);
    ELSE
        PERFORM setval('usuario_seq', max_usuario_id, true);
    END IF;
END
$$;

DO $$
DECLARE
    foreign_key RECORD;
    target_column SMALLINT;
BEGIN
    IF to_regclass('public.postulacion') IS NOT NULL THEN
        SELECT attnum INTO target_column
        FROM pg_attribute
        WHERE attrelid = 'public.postulacion'::regclass
          AND attname = 'adoptante_id'
          AND NOT attisdropped;

        FOR foreign_key IN
            SELECT conname
            FROM pg_constraint
            WHERE contype = 'f'
              AND conrelid = 'public.postulacion'::regclass
              AND conkey @> ARRAY[target_column]
        LOOP
            EXECUTE format('ALTER TABLE postulacion DROP CONSTRAINT %I', foreign_key.conname);
        END LOOP;

        ALTER TABLE postulacion
            ADD CONSTRAINT fk_postulacion_usuario
                FOREIGN KEY (adoptante_id) REFERENCES usuario (id);
    END IF;

    IF to_regclass('public.mascota') IS NOT NULL THEN
        SELECT attnum INTO target_column
        FROM pg_attribute
        WHERE attrelid = 'public.mascota'::regclass
          AND attname = 'dador_id'
          AND NOT attisdropped;

        FOR foreign_key IN
            SELECT conname
            FROM pg_constraint
            WHERE contype = 'f'
              AND conrelid = 'public.mascota'::regclass
              AND conkey @> ARRAY[target_column]
        LOOP
            EXECUTE format('ALTER TABLE mascota DROP CONSTRAINT %I', foreign_key.conname);
        END LOOP;

        ALTER TABLE mascota
            ADD CONSTRAINT fk_mascota_dador_usuario
                FOREIGN KEY (dador_id) REFERENCES usuario (id);
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS fundacion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuario (id),
    nombre VARCHAR(100) NOT NULL,
    nit VARCHAR(30) NOT NULL UNIQUE,
    estado VARCHAR(30) NOT NULL
);
