SELECT upsert_place(code => 'MASCAMBIOS', name => 'Más Cambios', type => 'BUREAU');


-- the remote_code is the 'sucursal' parameter of https://mascambios.com.py/api/cotizaciones.php (the value of the
-- branch select of the site), the location is the one of the google maps embed of each branch.

-- Avda. Adrián Jara esq. Abay, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'matriz', name => 'Matriz - Ciudad del Este',
                     latitude => -25.510758, longitude => -54.613695);
-- 25 de Mayo 161 casi Fulgencio Yegros, Asunción
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal1', name => 'Asunción',
                     latitude => -25.283463, longitude => -57.633840, phone_number => '(021) 451-086/90');
-- Itá Ybaté casi Avda. Mons. Rodríguez, Galería Zuni PB local 77-78, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal2', name => 'Agencia 2 - Ciudad del Este',
                     latitude => -25.510483, longitude => -54.608806);
-- Avda. Adrián Jara esq. Abay (Galería Dolly), CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal3', name => 'Agencia 3 - Ciudad del Este',
                     latitude => -25.510709, longitude => -54.611607);
-- Avda. Adrián Jara esq. Regimiento Piribebuy, Shopping Lai Lai 1er piso salón 107-109, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal4', name => 'Agencia 4 - Ciudad del Este',
                     latitude => -25.510624, longitude => -54.609906);
-- Avda. Adrián Jara entre Reg. Piribebuy e Itá Ybaté, Shopping Vendome, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal5', name => 'Agencia 5 - Ciudad del Este',
                     latitude => -25.511600, longitude => -54.609532);
-- Domingo Robledo casi Avda. Paraguay, Galería Nacional salón 56
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal6', name => 'Agencia 6 - Saltos del Guairá',
                     latitude => -24.059374, longitude => -54.306302);
-- C. Carlos Antonio López, PJC
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal7', name => 'Agencia 7 - Pedro Juan Caballero',
                     latitude => -22.551211, longitude => -55.726511);
-- Mcal. Francisco Solano López entre Yegros e Iturbe, PJC
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal8', name => 'Agencia 8 - Pedro Juan Caballero',
                     latitude => -22.542711, longitude => -55.732220);
-- Julia Miranda Cueto de Estigarribia esq. Mcal. F. S. López, PJC
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal9', name => 'Agencia 9 - Pedro Juan Caballero',
                     latitude => -22.536913, longitude => -55.733706);
-- Mons. Rodríguez casi Avda. Dr. Luis María Argaña, Shopping Box, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal10', name => 'Caja Auxiliar 2 - Ciudad del Este',
                     latitude => -25.509920, longitude => -54.606941);
-- Avda. Adrián Jara entre Abay y Avda. Carlos Antonio López, CDE
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal11', name => 'Caja Auxiliar 3 - Ciudad del Este',
                     latitude => -25.510711, longitude => -54.610894);
-- C. Carlos Antonio López casi 14 de Mayo 1485, PJC
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal12', name => 'Caja Auxiliar - Pedro Juan Caballero',
                     latitude => -22.541783, longitude => -55.731876);
-- Mcal. Francisco Solano López 1060, PJC
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal13', name => 'Caja Auxiliar 2 - Pedro Juan Caballero',
                     latitude => -22.537201, longitude => -55.733360);
-- Mcal. Francisco Solano López entre Yegros e Iturbe, PJC (the site repeats the address and location of the Agencia 8,
-- and names it 'CAJA AUX. 3-CDE')
SELECT upsert_branch(place_code => 'MASCAMBIOS', remote_code => 'sucursal14', name => 'Caja Auxiliar 3 - Pedro Juan Caballero',
                     latitude => -22.542711, longitude => -55.732220);
