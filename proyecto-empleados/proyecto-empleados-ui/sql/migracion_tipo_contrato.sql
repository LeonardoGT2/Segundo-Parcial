USE empleados_db;

ALTER TABLE empleados
    ADD COLUMN tipo_contrato VARCHAR(20) NOT NULL DEFAULT 'Temporal';