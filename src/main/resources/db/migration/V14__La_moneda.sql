SELECT upsert_place(code => 'LAMONEDA', name => 'La Moneda Cambios', type => 'BUREAU');


-- the remote_code is the 'sucursal' parameter of https://www.lamoneda.com.py/api/cotizaciones.php, the site doesn't
-- have the location of the branches.

-- Supercarretera Km 4, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'casa_matriz', name => 'Casa Matriz',
                     phone_number => '(061) 573 016');
-- Galería Jebai 2do piso, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_jebai', name => 'Sucursal Jebai',
                     phone_number => '(061) 500 700 / (061) 512 487');
-- Adrián Jara y Boquerón, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_centro', name => 'Sucursal Centro');
-- Julio César Riquelme entre Mons. Rodríguez y 13 Tuyutí, CDE
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_km7', name => 'Caja Auxiliar Km 7');
-- Km 12, a 300 m de Ruta PY02
SELECT upsert_branch(place_code => 'LAMONEDA', remote_code => 'sucursal_algesa', name => 'Caja Auxiliar Algesa');
