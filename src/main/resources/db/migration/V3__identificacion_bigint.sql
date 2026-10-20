ALTER TABLE public.usuario
    ALTER COLUMN identificacion TYPE BIGINT
    USING identificacion::BIGINT;
