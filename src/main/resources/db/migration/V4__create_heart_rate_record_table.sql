CREATE SEQUENCE heart_rate_record_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE heart_rate_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_date TIMESTAMPTZ NOT NULL,
    beats_per_minute INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);
