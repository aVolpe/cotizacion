SELECT
  qrd.id,
  p,
  br,
  qr.id,
  qr.date,
  qrd.id,
  qrd.sale_price,
  qrd.purchase_price
FROM query_response_detail qrd
  JOIN query_response qr ON qr.id = qrd.query_response_id
  JOIN place_branch br ON br.id = qr.branch_id
  JOIN place p ON br.place_id = p.id
WHERE qrd.iso_code = 'USD'
      AND qr.id IN (SELECT MAX(qr2.id)
                    FROM query_response qr2
                    WHERE qr2.branch_id = br.id)
ORDER BY qr.date DESC;


CREATE TABLE old_query_response_detail AS
  WITH moved_rows AS (
    DELETE FROM query_response_detail a
    USING query_response b
    WHERE a.query_response_id = b.id AND b.date < '2018-07-12'
    RETURNING a.* -- or specify columns
  )
  SELECT *
  FROM moved_rows;
