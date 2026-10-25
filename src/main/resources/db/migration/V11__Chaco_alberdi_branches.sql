-- Cambios Chaco: the branches are synced with https://www.cambioschaco.com.py/api/branch_office/, the
-- remote_code is the id of the site (the rates are loaded from /api/branch_office/<id>/exchange).
--
-- The site changed the id of two branches that are still the same physical branches: KM 7 (12) is now 32
-- (Ciudad del Este - Km7, ~70 m away and the same email) and Curupayty (14) is now 40. The exchange_rate
-- history is attached to the place_branch id, so their rows are moved to the new remote_code and keep it.
-- Both new ids were in use: 32 was Monseñor Rodriguez (CDE), which is no longer in the site, and 14 is now
-- Ag. Plaza Madero (Luque). Because of UNIQUE (place_id, remote_code) the order matters:
--   1. Monseñor Rodriguez frees 32: it keeps its history under '32-old' and is deactivated.
--   2. KM 7 moves from 12 to 32.
--   3. Curupayty moves from 14 to 40, freeing 14 for Plaza Madero.
-- Each UPDATE is guarded by the old remote_code and name, so running it again is a no-op.
UPDATE place_branch b
SET remote_code = '32-old',
    active      = false
FROM place p
WHERE p.id = b.place_id
  AND p.code = 'CAMBIOS_CHACO'
  AND b.remote_code = '32'
  AND b.name = 'Monseñor Rodriguez - CDE';

UPDATE place_branch b
SET remote_code = v.new_code
FROM place p,
     (VALUES ('12', 'KM 7 - CDE', '32'),
             ('14', 'Curupayty - CDE', '40')) AS v (old_code, name, new_code)
WHERE p.id = b.place_id
  AND p.code = 'CAMBIOS_CHACO'
  AND b.remote_code = v.old_code
  AND b.name = v.name;

-- Same columns the gatherer fills from the API (CambiosChaco.BranchData.map), the schedule uses its format.
-- 32 and 40 update the moved rows. The site doesn't publish the location of 40, so it keeps the one it
-- already had (it is the same branch) instead of losing it to null.
SELECT upsert_branch(place_code => 'CAMBIOS_CHACO', remote_code => '32', name => 'Ciudad del Este - Km7',
                     latitude => -25.4989169, longitude => -54.6666607, phone_number => '061-571200 / 201',
                     email => 'vlopez@cambioschaco.com.py',
                     schedule => 'Semana: 07:30hs. A 17:00hs., Sabado: 07:30hs. A 14:00hs., Domingo: Cerrado');
SELECT upsert_branch(place_code => 'CAMBIOS_CHACO', remote_code => '40', name => 'Ciudad del Este - Curupayty',
                     latitude => b.latitude, longitude => b.longitude,
                     phone_number => '(0631) 23 800/810', email => 'curupayty@cambioschaco.com.py',
                     schedule => 'Semana: 07:30 hs a 16:00 hs, Sabado: 07:30 hs a 13:00 hs, Domingo: Cerrado')
FROM place_branch b
         JOIN place p ON p.id = b.place_id
WHERE p.code = 'CAMBIOS_CHACO'
  AND b.remote_code = '40';

-- New branches. The site doesn't publish the location of 41 (lat/lng are null), the client hides the
-- branches without one from the map.
SELECT upsert_branch(place_code => 'CAMBIOS_CHACO', remote_code => '14', name => 'Ag. Plaza Madero',
                     latitude => -25.255264, longitude => -57.5135983, phone_number => '0972331608',
                     email => 'agmadero@cambioschaco.com.py',
                     schedule => 'Semana: de 08:30h a 17:00h, Sabado: de 09:00h  a 13:00h, Domingo: Cerrado');
SELECT upsert_branch(place_code => 'CAMBIOS_CHACO', remote_code => '16', name => 'Hernandarias - Super Carretera',
                     latitude => -25.4072835, longitude => -54.6297493, phone_number => '(0631) 23 800 / 810',
                     email => 'hernandarias2@cambioschaco.com.py',
                     schedule => 'Semana: 07:30 A 16:30 hs., Sabado: 07:30 A 13:00 hs., Domingo: Cerrado');
SELECT upsert_branch(place_code => 'CAMBIOS_CHACO', remote_code => '41', name => 'Minga Guazu - Abasto Este',
                     phone_number => '(0983) 407 655', email => 'mingaguazu@cambioschaco.com.py',
                     schedule => 'Semana: 07:30 hs a 16:30 hs, Sabado: 07:30 hs a 13:30 hs, Domingo: Cerrado');

-- No longer listed in the site: Supercarretera KM 4, Toledo, EBY Circuito Comercial and Hernandarias (16 is
-- another branch, ~1.5 km away and with another email). Their history is kept.
SELECT deactivate_branch('CAMBIOS_CHACO', '10');
SELECT deactivate_branch('CAMBIOS_CHACO', '15');
SELECT deactivate_branch('CAMBIOS_CHACO', '19');
SELECT deactivate_branch('CAMBIOS_CHACO', '20');

-- 24 (Zona Alta - ENC) stays active: the site lists it but /24/exchange returns '[]' and it never had rates.
-- The gatherer adds no rates for an empty answer, so it will show up by itself if the site publishes them.


-- Cambios Alberdi: Salto del Guairá is still listed in the site but every price is '0,000' since 2025-07,
-- the app no longer shows it.
SELECT deactivate_branch('ALBERDI_2', 'saltodelguaira');
