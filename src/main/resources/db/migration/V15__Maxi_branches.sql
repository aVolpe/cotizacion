-- MaxiCambios: the branches are the ones listed in https://www.maxicambios.com.py/ (each one has its data in
-- the initMap2(...) call of the 'Sucursales' section), the remote_code is the id of the site.
--
-- The site only publishes two boards, Asunción and Ciudad del Este, so the branches don't have their own
-- rates: the gatherer uses the CDE board for the CDE branches (MaxiCambios.CDE_CODES) and the Asunción one
-- for the rest.
--
-- The two existing branches used '0' (Asunción) and '13' (CDE) as remote_code. They move to their site id
-- (1 and 17) so their exchange_rate history is kept. '13' is now Fuente Shopping de Salemma, so it is moved
-- first. Each UPDATE is guarded by the old remote_code and name, so running it again is a no-op.
UPDATE place_branch b
SET remote_code = v.new_code
FROM place p,
     (VALUES ('13', 'Casa Central CDE', '17'),
             ('0', 'Shopping Multiplaza Casa Central', '1')) AS v (old_code, name, new_code)
WHERE p.id = b.place_id
  AND p.code = 'MaxiCambios'
  AND b.remote_code = v.old_code
  AND b.name = v.name;


-- Asunción

-- Avda. Eusebio Ayala 4501, Torre C Local 79
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '1', name => 'Shopping Multiplaza Casa Central',
                     latitude => -25.3166893, longitude => -57.5716532, phone_number => '(021) 525 105/8',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5d6/ffe/0b9/5d6ffe0b93019070535960.jpg',
                     schedule => 'Lunes a Sábado: De 8 a 21 Hs.
Domingo: De 10 a 20 Hs.');
-- Avda. Fernando de la Mora esq. Rca. Argentina, PB
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '2', name => 'Terminal de Omnibus',
                     latitude => -25.3259849, longitude => -57.5960224, phone_number => '(0992) 268 755',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/644/bd3/248/644bd324836e5121752154.jpeg',
                     schedule => 'Lunes a Domingo: De 6 a 21 Hs.');
-- Avda. Aviadores del Chaco y Prof. Delia Frutos de Gonzalez, locales 211 (2do. nivel) y 117 (1er. nivel)
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '5', name => 'Shopping del Sol',
                     latitude => -25.2826013, longitude => -57.5667682,
                     phone_number => '(021) 611 777 / (0995) 355 554 / (021) 611 979 / (0994) 641 418',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/84b/094/5db84b0941b88435424342.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Quesada 5050 c/ Charles de Gaulle, PB, Local 1A
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '6', name => 'Shopping Mariscal',
                     latitude => -25.2951183, longitude => -57.5810583,
                     phone_number => '(021) 664 250/2 / (0994) 857 934',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/84b/ac2/5db84bac2a68c138505712.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Avda. Santa Teresa e/ Aviadores del Chaco y Herminio Maldonado, Local S1 104 - Nivel S1
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '7', name => 'Paseo La Galería',
                     latitude => -25.2850008, longitude => -57.5633466,
                     phone_number => '(021) 695 569/70 / (0992) 268 866',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/84e/105/5db84e105bc74867218463.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- 14 de Mayo e/ Gral. Diaz y Oliva, PB, Local 166
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '8', name => 'Asunción Super Centro',
                     latitude => -25.2824309, longitude => -57.6385384,
                     phone_number => '(021) 496 222/3 / (021) 494 637 / (0995) 635 555',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/84e/8e3/5db84e8e37e94428284357.jpg',
                     schedule => 'Lunes a Viernes: De 8 a 17 Hs.
Sábado: De 8 a 12 Hs.');
-- Av. Brasilia 2028 c/ Av. Artigas, Local C1-003. The site doesn't publish its location (0.0, 0.0), it is
-- the one of Google Maps.
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '24', name => 'Distrito Perseverancia',
                     latitude => -25.2678279, longitude => -57.5963885,
                     phone_number => '(0992) 080 000',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/694/2ca/343/6942ca343ac21283019194.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');


-- Mariano Roque Alonso

-- Ruta Transchaco km 10, PB Local A100
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '9', name => 'Shopping La Rural',
                     latitude => -25.2334228, longitude => -57.5375304,
                     phone_number => '(021) 756 300 / (0993) 310 333',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/851/1ec/5db8511ec9e2c179818634.jpg',
                     schedule => 'Lunes a Sábado: De 8 a 20 Hs.
Domingo: De 9 a 18 Hs.');
-- Ruta Transchaco km 14 esq. Manuel Irala Fernandez, Local 5
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '10', name => 'Shopping Mariano',
                     latitude => -25.2262989, longitude => -57.5348227,
                     phone_number => '(021) 762 014/5 / (0992) 268 699',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/851/b63/5db851b63d197520160438.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');


-- San Lorenzo

-- Ruta Nro. 2 Mcal. Estigarribia c/ Sgto. Penayo, km 15, Local 24A
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '11', name => 'San Lorenzo Shopping',
                     latitude => -25.3432611, longitude => -57.4886878, phone_number => '(0992) 268 935',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/861/76b/5db86176bdd9c313377511.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Avda. Mcal. López c/ 26 de Febrero, Local 45B
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '12', name => 'Pinedo Shopping',
                     latitude => -25.3241453, longitude => -57.520785, phone_number => '(0992) 999 998',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/862/0c4/5db8620c4ef1b672223197.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Ruta Mcal. Estigarribia km. 9.5, Local C03, Primer Piso
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '13', name => 'Fuente Shopping de Salemma',
                     latitude => -25.3345529, longitude => -57.5371466, phone_number => '(0992) 296 164',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/863/e27/5db863e27180d129885396.jpg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Ruta 2 Mcal. Estigarribia Km 14,5. Local 32, Planta Baja
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '14', name => 'Parque 14 y Medio',
                     latitude => -25.3412477, longitude => -57.4971976,
                     phone_number => '(021) 328 1517 / (0992) 229 480',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/864/7a4/5db8647a4ef83099143595.jpg',
                     schedule => 'Lunes a Sábado: De 8 a 18 Hs.
Domingo: De 9 a 15 Hs.');


-- Limpio

-- Ruta Nro. 3 Gral. Aquino km 22 Local 26, bloque Shopping
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '15', name => 'Abasto Norte',
                     latitude => -25.177777, longitude => -57.4839878, phone_number => '(021) 782 228/230',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/865/22c/5db86522c14ab634779882.jpg',
                     schedule => 'Lunes a Viernes: De 7 a 17 Hs.
Sábado: De 7 a 15 Hs.');
-- Ruta Nº 3 Gral. Aquino y Manuel Gondra, Local 58. The site doesn't publish its location (0.0, 0.0), it is
-- the one of Google Maps.
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '23', name => 'Plaza Norte - Limpio',
                     latitude => -25.1753575, longitude => -57.4855344,
                     phone_number => '(0992) 268 647',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/688/130/289/688130289ee6c068605670.jpeg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 19 Hs.');


-- Luque

-- Avda. General Aquino Nro. 999 esq. Tte. Rivas
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '16', name => 'Supermercado Los Jardines',
                     latitude => -25.265063956230193, longitude => -57.505725311114475,
                     phone_number => '(0992) 450 275',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/866/593/5db86659329e2713541645.jpg',
                     schedule => 'Lunes a Sábado: De 7 a 21 Hs.
Domingo: De 8 a 19 Hs.');
-- Sportivo Luqueño e/ Balderrama y Javier Bogarin. The site doesn't publish its location (0.0, 0.0), it is
-- the one of Google Maps.
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '22', name => 'AhoAho Comercial',
                     latitude => -25.271454, longitude => -57.49035,
                     phone_number => '(0994) 457 029',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/687/e6b/f37/687e6bf3785dc371634952.jpeg',
                     schedule => 'Lunes a Domingo: De 7 a 21 Hs.');


-- Ciudad del Este, they use the CDE board

-- Súper Carretera Mcal. Lopez y Calle Trebol km 4
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '17', name => 'Casa Central - Ciudad del Este',
                     latitude => -25.50847469999999, longitude => -54.639116599999966,
                     phone_number => '(061) 573 106 / 574 270 / (0991) 713 309',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/5db/867/037/5db86703774da433953298.jpg',
                     schedule => 'Lunes a Viernes: De 7 a 18 Hs.
Sábado: De 7 a 12:30 Hs.');
-- Monseñor Rodríguez y Consejal Romero
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '18', name => 'Lago Shopping - Ciudad del Este',
                     latitude => -25.510189016753714, longitude => -54.61834822798479,
                     phone_number => '(021) 728 3888',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/63f/f78/955/63ff789554535736549172.jpg',
                     schedule => 'Lunes a Sábado: De 8 a 20 Hs.
Domingo: De 9 a 18 Hs.');
-- Avda. Dr. Luis María Argaña, Centro, Local 121 (Planta baja)
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '20', name => 'Shopping Paris - Ciudad del Este',
                     latitude => -25.51304557959199, longitude => -54.606010297291384,
                     phone_number => '(0994) 444 402',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/659/d9a/814/659d9a814d71e669394430.jpeg',
                     schedule => 'Lunes a Sábado: De 8 a 19 Hs.
Domingo: De 9 a 17 Hs.');


-- Interior

-- General Aquino 1848 Local 4, Shopping Costanera
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '19', name => 'Shopping Costanera - Encarnación',
                     latitude => -27.325260512926107, longitude => -55.875474764413745,
                     phone_number => '(0994) 221 185',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/641/05e/ac4/64105eac46acb165711667.jpeg',
                     schedule => 'Lunes a Sábado: De 9 a 21 Hs.
Domingo: De 10 a 21 Hs.');
-- Carlos Antonio López 701 c/ Mcal. Estigarribia
SELECT upsert_branch(place_code => 'MaxiCambios', remote_code => '25', name => 'Villarrica',
                     latitude => -25.7814802166271, longitude => -56.446689828416716,
                     phone_number => '(0992) 300 090',
                     image => 'https://www.maxicambios.com.py/storage/app/uploads/public/694/3ee/3ab/6943ee3abe4bc557050679.jpeg',
                     schedule => 'Lunes a Viernes: De 8 a 17 Hs.
Sábado: De 8 a 13 Hs.');
