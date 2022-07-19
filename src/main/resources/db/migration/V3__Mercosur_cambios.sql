INSERT INTO place (id, code, name, type)
VALUES (NEXTVAL('hibernate_sequence'), 'MERCOSUR', 'Mercosur Cambios', 'BUREAU');



INSERT INTO place_branch (id, email, image, latitude, longitude, name, phone_number, remote_code, schedule, place_id)
VALUES (NEXTVAL('hibernate_sequence'), NULL, 'https://site.mercosurcambios.com/ImagemNormal/E/1', -25.5099801, -54.6133665, 'Casa Matriz', '(061) 509711', '1', 'Lunes a Viernes: 7:00 a 16:00 Hs. Sábado: 07:00 a 12:00 Hs.', (SELECT id FROM place WHERE code = 'MERCOSUR')),
       (NEXTVAL('hibernate_sequence'), NULL, 'https://site.mercosurcambios.com/ImagemNormal/E/2', -25.5079201, -54.6389299, 'Gran VIA KM4', '(061) 509711', '1', 'Lunes a Viernes: 7:00 a 16:00 Hs. Sábado: 07:00 a 12:00 Hs.', (SELECT id FROM place WHERE code = 'MERCOSUR')),
       (NEXTVAL('hibernate_sequence'), NULL, 'https://site.mercosurcambios.com/ImagemNormal/E/5', -24.9752059, -54.9210903, 'San Alberto', '(067) 720398', '1', 'Lunes a Viernes: 7:00 a 16:00 Hs. Sábado: 07:00 a 12:00 Hs.', (SELECT id FROM place WHERE code = 'MERCOSUR'));
