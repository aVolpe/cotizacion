-- The site of La Moneda doesn't have the location of the branches (see V14), the coordinates are the Google Maps
-- place of each one. upsert_branch overwrites every column, so the phones of V14 are repeated.

-- Supercarretera Km 4, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'casa_matriz', name => 'Casa Matriz',
                     latitude => -25.5078531, longitude => -54.6392235,
                     phone_number => '(061) 573 016');
-- Galería Jebai 2do piso, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_jebai', name => 'Sucursal Jebai',
                     latitude => -25.5103129, longitude => -54.6097678,
                     phone_number => '(061) 500 700 / (061) 512 487');
-- Adrián Jara y Boquerón, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_centro', name => 'Sucursal Centro',
                     latitude => -25.5108931, longitude => -54.6120617);
-- Julio César Riquelme entre Mons. Rodríguez y 13 Tuyutí, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_km7', name => 'Caja Auxiliar Km 7',
                     latitude => -25.4983725, longitude => -54.66602);
-- Km 12, a 300 m de Ruta PY02 (ALGESA, terminal de cargas de la aduana)
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_algesa', name => 'Caja Auxiliar Algesa',
                     latitude => -25.4910375, longitude => -54.7224219);
