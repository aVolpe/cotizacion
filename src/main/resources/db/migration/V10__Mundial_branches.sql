-- Mundial Cambios has a new site: every branch is a tab of the home page and the remote_code is the
-- normalized tab title (see Mundial.toRemoteCode). The names were loaded twice ('VILLA MORRA VILLA MORRA'),
-- they are replaced with the names of the site.
--
-- The address, phone, schedule and location are kept. A plain UPDATE is used instead of upsert_branch
-- (it sets the omitted columns to NULL) and deactivate_branch (it fails if the branch doesn't exist), so
-- running it again or over a database without these branches changes nothing.

UPDATE place_branch b
SET remote_code = v.remote_code,
    name        = v.name,
    active      = true
FROM place p,
     (VALUES ('1', 'globo-center', 'Globo Center'),
             ('3', 'jebai', 'Jebai Center'),
             ('4', 'km-4', 'KM 4 - Supercarretera'),
             ('5', 'salto-del-guaira', 'Salto del Guairá'),
             ('7', 'villa-morra', 'Villa Morra')) AS v (old_remote_code, remote_code, name)
WHERE p.id = b.place_id
  AND p.code = 'MUNDIAL'
  AND b.remote_code = v.old_remote_code;

-- Vendome and Asunción are no longer listed in the site, their history is kept.
UPDATE place_branch b
SET name   = v.name,
    active = false
FROM place p,
     (VALUES ('2', 'Vendome'),
             ('6', 'Asunción')) AS v (remote_code, name)
WHERE p.id = b.place_id
  AND p.code = 'MUNDIAL'
  AND b.remote_code = v.remote_code;
