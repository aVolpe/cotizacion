-- Natural keys for places and branches, logical delete for branches and the functions used by the
-- next migrations to add, update and remove branches.
--
-- Usage in a new migration (Vn__<place>_<what>.sql):
--
--   SELECT upsert_place(code => 'NUEVA', name => 'Nueva Casa', type => 'BUREAU');
--   SELECT upsert_branch(place_code => 'MUNDIAL', remote_code => '8', name => 'Mariano Roque Alonso',
--                        latitude => -25.20, longitude => -57.53, phone_number => '(021) 123',
--                        schedule => 'Lunes a Viernes: 8:00 a 17:00 Hs.');
--   SELECT deactivate_branch('MUNDIAL', '3');


-- Data fixes needed by the constraints

-- The three Mercosur branches were loaded with remote_code '1', so all of them showed the rates of
-- the main office. The site uses /Sucursale/2 for Gran Via KM4 and /Sucursale/5 for San Alberto.
UPDATE place_branch
SET remote_code = '2'
WHERE name = 'Gran VIA KM4'
  AND place_id = (SELECT id FROM place WHERE code = 'MERCOSUR');
UPDATE place_branch
SET remote_code = '5'
WHERE name = 'San Alberto'
  AND place_id = (SELECT id FROM place WHERE code = 'MERCOSUR');

-- Amambay is queried at place level, its only branch had no remote code.
UPDATE place_branch
SET remote_code = 'central'
WHERE remote_code IS NULL
  AND place_id = (SELECT id FROM place WHERE code = 'AMAMBAY');


-- Constraints

ALTER TABLE place
    ALTER COLUMN code SET NOT NULL;
ALTER TABLE place
    ADD CONSTRAINT place_code_key UNIQUE (code);

ALTER TABLE place_branch
    ALTER COLUMN remote_code SET NOT NULL;
ALTER TABLE place_branch
    ALTER COLUMN place_id SET NOT NULL;
ALTER TABLE place_branch
    ADD CONSTRAINT place_branch_remote_code_key UNIQUE (place_id, remote_code);

ALTER TABLE place_branch
    ADD COLUMN active boolean NOT NULL DEFAULT true;

-- ALBERDI was replaced by ALBERDI_2 and has no gatherer.
UPDATE place_branch
SET active = false
WHERE place_id = (SELECT id FROM place WHERE code = 'ALBERDI');


-- Functions

CREATE OR REPLACE FUNCTION upsert_place(code text, name text, type text DEFAULT 'BUREAU')
    RETURNS bigint
    LANGUAGE plpgsql
AS
$$
DECLARE
    v_id bigint;
BEGIN
    INSERT INTO place (id, code, name, type)
    VALUES (nextval('hibernate_sequence'), upsert_place.code, upsert_place.name, upsert_place.type)
    ON CONFLICT ON CONSTRAINT place_code_key DO UPDATE
        SET name = excluded.name,
            type = excluded.type
    RETURNING id INTO v_id;
    RETURN v_id;
END
$$;


-- Creates the branch, or updates (and reactivates) it if the place already has one with the same
-- remote_code. Every column is overwritten, the omitted ones become NULL.
CREATE OR REPLACE FUNCTION upsert_branch(place_code text,
                                         remote_code text,
                                         name text,
                                         latitude double precision DEFAULT NULL,
                                         longitude double precision DEFAULT NULL,
                                         phone_number text DEFAULT NULL,
                                         email text DEFAULT NULL,
                                         image text DEFAULT NULL,
                                         schedule text DEFAULT NULL)
    RETURNS bigint
    LANGUAGE plpgsql
AS
$$
DECLARE
    v_place_id bigint;
    v_id       bigint;
BEGIN
    SELECT p.id INTO v_place_id FROM place p WHERE p.code = upsert_branch.place_code;
    IF v_place_id IS NULL THEN
        RAISE EXCEPTION 'Place % does not exist, create it with upsert_place', upsert_branch.place_code;
    END IF;

    INSERT INTO place_branch (id, place_id, remote_code, name, latitude, longitude, phone_number, email,
                              image, schedule, active)
    VALUES (nextval('hibernate_sequence'), v_place_id, upsert_branch.remote_code, upsert_branch.name,
            upsert_branch.latitude, upsert_branch.longitude, upsert_branch.phone_number, upsert_branch.email,
            upsert_branch.image, upsert_branch.schedule, true)
    ON CONFLICT ON CONSTRAINT place_branch_remote_code_key DO UPDATE
        SET name         = excluded.name,
            latitude     = excluded.latitude,
            longitude    = excluded.longitude,
            phone_number = excluded.phone_number,
            email        = excluded.email,
            image        = excluded.image,
            schedule     = excluded.schedule,
            active       = true
    RETURNING id INTO v_id;
    RETURN v_id;
END
$$;


-- Logical delete: the branch is no longer queried nor listed, its history is kept.
CREATE OR REPLACE FUNCTION deactivate_branch(place_code text, remote_code text)
    RETURNS void
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE place_branch b
    SET active = false
    FROM place p
    WHERE p.id = b.place_id
      AND p.code = deactivate_branch.place_code
      AND b.remote_code = deactivate_branch.remote_code;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Branch % of place % does not exist',
            deactivate_branch.remote_code, deactivate_branch.place_code;
    END IF;
END
$$;
