CREATE SEQUENCE steps_record_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE steps_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_date TIMESTAMPTZ NOT NULL,
    number_of_steps BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);
