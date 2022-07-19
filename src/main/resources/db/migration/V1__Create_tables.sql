CREATE SEQUENCE IF NOT EXISTS hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS execution
(
    id   bigint NOT NULL,
    date timestamp without time zone
);


CREATE TABLE IF NOT EXISTS place
(
    id   bigint NOT NULL,
    code text,
    name text,
    type text
);


CREATE TABLE IF NOT EXISTS place_branch
(
    id           bigint NOT NULL,
    email        text,
    image        text,
    latitude     double precision,
    longitude    double precision,
    name         text,
    phone_number text,
    remote_code  text,
    schedule     text,
    place_id     bigint
);

CREATE TABLE IF NOT EXISTS query_response
(
    id           bigint NOT NULL,
    date         timestamp,
    full_data    text,
    branch_id    bigint,
    place_id     bigint,
    execution_id bigint
);


CREATE TABLE IF NOT EXISTS query_response_detail
(
    id                bigint NOT NULL,
    iso_code          text,
    purchase_price    bigint NOT NULL,
    purchase_trend    bigint NOT NULL,
    sale_price        bigint NOT NULL,
    sale_trend        bigint NOT NULL,
    query_response_id bigint
);



ALTER TABLE ONLY execution
    DROP CONSTRAINT IF EXISTS execution_pkey;
ALTER TABLE ONLY execution
    ADD CONSTRAINT execution_pkey PRIMARY KEY (id);


ALTER TABLE ONLY place_branch
    DROP CONSTRAINT IF EXISTS place_branch_pkey;
ALTER TABLE ONLY place_branch
    ADD CONSTRAINT place_branch_pkey PRIMARY KEY (id);


ALTER TABLE ONLY place
    DROP CONSTRAINT IF EXISTS place_pkey;
ALTER TABLE ONLY place
    ADD CONSTRAINT place_pkey PRIMARY KEY (id);


ALTER TABLE ONLY query_response_detail
    DROP CONSTRAINT IF EXISTS query_response_detail_pkey;
ALTER TABLE ONLY query_response_detail
    ADD CONSTRAINT query_response_detail_pkey PRIMARY KEY (id);


ALTER TABLE ONLY query_response
    DROP CONSTRAINT IF EXISTS query_response_pkey;
ALTER TABLE ONLY query_response
    ADD CONSTRAINT query_response_pkey PRIMARY KEY (id);


CREATE INDEX IF NOT EXISTS test2_qrd_idx ON query_response_detail USING btree (iso_code, query_response_id);

-- hibernate generated constraint
ALTER TABLE ONLY query_response
    DROP CONSTRAINT IF EXISTS fk7cyxew7roko4lqgejeuq8fn4r;
ALTER TABLE ONLY query_response
    ADD CONSTRAINT query_response_to_branch FOREIGN KEY (branch_id) REFERENCES place_branch (id);



ALTER TABLE ONLY query_response_detail
    DROP CONSTRAINT IF EXISTS fka4xq7i19aalymgtuentahmjun;
ALTER TABLE ONLY query_response_detail
    ADD CONSTRAINT query_response_detail_to_query_response FOREIGN KEY (query_response_id) REFERENCES query_response (id);



ALTER TABLE ONLY query_response
    DROP CONSTRAINT IF EXISTS fkd5j83dh6x6lf39x5kgnwdy5he;
ALTER TABLE ONLY query_response
    ADD CONSTRAINT query_response_to_place FOREIGN KEY (place_id) REFERENCES place (id);


ALTER TABLE ONLY query_response
    DROP CONSTRAINT IF EXISTS fki0a16xg0peaqfmm7r2ns9i30h;
ALTER TABLE ONLY query_response
    ADD CONSTRAINT query_response_to_execution FOREIGN KEY (execution_id) REFERENCES execution (id);


ALTER TABLE ONLY place_branch
    DROP CONSTRAINT IF EXISTS fksj9rb6e31mixka6hs2lkktg6i;
ALTER TABLE ONLY place_branch
    ADD CONSTRAINT place_branch_to_place FOREIGN KEY (place_id) REFERENCES place (id);
