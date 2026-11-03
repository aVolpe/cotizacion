-- ueno bank was intervened by the Banco Central del Paraguay on 2026-11-03 (resolution procedure for
-- insufficient solvency): its operations are suspended, so its gatherer was removed.
--
-- As with Visión Banco (V18) the place has no active flag, so its branches are deactivated: they are no
-- longer listed and the place and its exchange_rate history are kept. The place itself won't be shown
-- anymore because the API only lists the rates of the last execution.
UPDATE place_branch
SET active = false
WHERE place_id = (SELECT id FROM place WHERE code = 'UENO');
