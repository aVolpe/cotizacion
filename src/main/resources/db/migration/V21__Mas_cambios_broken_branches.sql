-- Más Cambios branches whose site data is abandoned: the week of 2026-10-26 they didn't change once and
-- they are far from the rest of the market (and from their own main branch):
--   sucursal4 (Agencia 4 - CDE): USD 6950 / 7080, BRL 1300 / 1380, ARS 9 / 10
--   sucursal6 (Agencia 6 - Saltos del Guairá): USD 5976.74 / 7700, BRL 1165.35 / 1400, ARS 3.98 / 95
--   sucursal7 (Agencia 7 - PJC): USD 6550 / 6670, BRL 1205 / 1240
--   sucursal9 (Agencia 9 - PJC): USD 6026.02 / 6520, EUR 7350 / 8200
-- sucursal14 (Caja Auxiliar 3 - PJC) never returned rates.
--
-- The branches are deactivated (the gatherer only queries the active ones), their rows and history are kept.
SELECT deactivate_branch('MASCAMBIOS', 'sucursal4');
SELECT deactivate_branch('MASCAMBIOS', 'sucursal6');
SELECT deactivate_branch('MASCAMBIOS', 'sucursal7');
SELECT deactivate_branch('MASCAMBIOS', 'sucursal9');
SELECT deactivate_branch('MASCAMBIOS', 'sucursal14');
