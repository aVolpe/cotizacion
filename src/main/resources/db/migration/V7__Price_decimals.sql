-- The prices are stored with two decimals: the ARS is worth 3-5 guaranies and '3,45' was stored as 3.
--
-- The history was already rounded, it stays as x.00. No index, view nor constraint uses these columns
-- (see V5), so they can be changed in place (the table is rewritten).

ALTER TABLE exchange_rate
    ALTER COLUMN purchase_price TYPE numeric(14, 2),
    ALTER COLUMN sale_price TYPE numeric(14, 2);
