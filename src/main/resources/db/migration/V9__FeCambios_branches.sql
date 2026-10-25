-- FeCambios moved to a new site, the rates of each branch are loaded from
-- https://www.fecambios.com.py/<code>-tasks-list-cot.php, so the remote_code is now that prefix.
--
-- upsert_branch matches by remote_code, it can't change it, so the branches are renamed with an UPDATE keyed by
-- the place and the old remote_code (the history stays attached to the same rows).
UPDATE place_branch b
SET remote_code = v.new_code
FROM place p,
     (VALUES ('9', 'pal'),  -- Casa Matriz - Palma
             ('10', 'vmo'), -- Villa Morra
             ('11', 'cde'), -- Sucursal Monseñor Rodriguez (Ciudad del Este)
             ('13', 'slo')  -- Ag. San Lorenzo
     ) AS v(old_code, new_code)
WHERE p.id = b.place_id
  AND p.code = 'FeCambios'
  AND b.remote_code = v.old_code;

-- Pedro Juan Caballero is no longer listed in the site and its list returns every price as '0'.
SELECT deactivate_branch('FeCambios', '8');
