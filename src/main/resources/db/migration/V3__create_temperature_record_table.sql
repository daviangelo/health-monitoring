CREATE SEQUENCE temperature_record_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE temperature_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_date TIMESTAMPTZ NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    scale VARCHAR(255) NOT NULL CHECK (scale IN ('FAHRENHEIT','CELSIUS','KELVIN')),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);
