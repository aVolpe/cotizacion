-- Visión Banco closed: its site no longer responds and there is no data since 2024-06, so its gatherer
-- was removed.
--
-- The place has no active flag (only the branches have it, see V4), so its branches are deactivated:
-- they are no longer listed and the place and its exchange_rate history are kept. The place itself
-- won't be shown anymore because the API only lists the rates of the last execution.
UPDATE place_branch
SET active = false
WHERE place_id = (SELECT id FROM place WHERE code = 'VISION_BANCO');
