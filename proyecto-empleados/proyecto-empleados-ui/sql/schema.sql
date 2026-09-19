-- =====================================================================
-- Esquema propio — Variante A: Gestión de empleados
-- Autor: Leonardo Solórzano
--
-- Decisiones de diseño (justificación pedida en el enunciado):
--
-- 1. id INT AUTO_INCREMENT PRIMARY KEY
--    El identificador lo asigna el sistema, nunca el usuario, y nunca se
--    repite: eso es exactamente lo que hace una PK autoincremental.
--
-- 2. nombre_completo VARCHAR(100) NOT NULL
--    100 caracteres cubre con margen nombres compuestos guatemaltecos con
--    dos apellidos (ej. "María José del Rosario Hernández Recinos") sin
--    desperdiciar tanto espacio como para justificar un TEXT.
--
-- 3. departamento VARCHAR(50) NOT NULL
--    La variante dice explícitamente que es texto libre (no hay catálogo
--    cerrado de departamentos), así que no se modela como FK a otra tabla.
--    50 caracteres es de sobra para nombres de departamento típicos.
--
-- 4. salario_mensual DECIMAL(10,2) NOT NULL
--    Dinero nunca se guarda en FLOAT/DOUBLE por errores de redondeo.
--    DECIMAL(10,2) permite hasta 99,999,999.99 — más que suficiente para
--    un salario mensual individual, con exactamente 2 decimales (centavos).
--    Regla de negocio "salario > 0" se valida en la aplicación (Java) antes
--    de llegar aquí; SQL solo garantiza que el dato no sea NULL.
--
-- 5. fecha_contratacion DATE NOT NULL
--    Solo interesa el día, no la hora, así que DATE (no DATETIME/TIMESTAMP).
--    La regla "no puede ser fecha futura" se valida en Java, no en la BD.
--
-- 6. activo BOOLEAN NOT NULL DEFAULT TRUE
--    MySQL/MariaDB implementan BOOLEAN como TINYINT(1). Todo empleado nuevo
--    se registra activo por defecto. "Inactivo" no borra al empleado (ver
--    regla de negocio de historial) — por eso este campo existe separado
--    de la operación de eliminar (borrado físico real, sección "eliminar").

-- 7. tipo_contrato VARCHAR(20) NOT NULL DEFAULT 'Temporal'
--    Campo de selección fija (Temporal / Permanente / Por hora). La validación
--    de que sea una de las 3 opciones se hace en Java (JComboBox + validación
--    antes de guardar). El DEFAULT protege a los registros ya existentes.
-- =====================================================================

CREATE TABLE IF NOT EXISTS empleados (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo     VARCHAR(100)    NOT NULL,
    departamento        VARCHAR(50)     NOT NULL,
    salario_mensual     DECIMAL(10,2)   NOT NULL,
    fecha_contratacion  DATE            NOT NULL,
        activo              BOOLEAN         NOT NULL DEFAULT TRUE,
    tipo_contrato       VARCHAR(20)     NOT NULL DEFAULT 'Temporal'
);

-- Datos de ejemplo (los del enunciado), útiles para probar el CRUD desde el día 1.
INSERT INTO empleados (nombre_completo, departamento, salario_mensual, fecha_contratacion, activo, tipo_contrato) VALUES
('Ana Lucía Pérez',     'Sistemas',     8500.00, '2024-03-15', TRUE,  'Permanente'),
('Carlos Roberto Mux',  'Ventas',       6200.00, '2024-01-10', TRUE,  'Temporal'),
('Diana Sofía Cabrera', 'Contabilidad', 7100.00, '2023-11-02', FALSE, 'Por hora');