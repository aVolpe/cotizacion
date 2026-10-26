-- The rates are the same for every branch (the gatherer quotes at the bank level), the branches are the
-- 'locales ueno bank x' of https://ayuda.ueno.com.py/article/donde-estan-los-locales-ueno-bank-x and the
-- remote_code is a slug of the locale name (lowercase, without accents, words joined by dashes).
-- The coordinates come from the Google Maps link of each locale (the !3d!4d place pin, or the ?q= point
-- when the link is a plain coordinate), the address of the article is in the comment above each branch.
-- The schedule is the cashier schedule (the TEDS are available 24 hours), the locales marked with (*)
-- in the article have their own schedule.

-- ueno bank x Aeropuerto Silvio Pettirossi
SELECT upsert_branch(place_code => 'UENO', remote_code => 'aeropuerto-silvio-pettirossi', name => 'Aeropuerto Silvio Pettirossi',
                     latitude => -25.2415129, longitude => -57.5141814,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Nuestra Sra. de la Candelaria esq. Gabriel Casaccia
SELECT upsert_branch(place_code => 'UENO', remote_code => 'aregua', name => 'Areguá',
                     latitude => -25.3086288, longitude => -57.385654,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Acceso Sur N° 1075 esq. S. Ovelar
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-acceso-sur', name => 'Asunción - Acceso Sur',
                     latitude => -25.3450701, longitude => -57.5794355,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Estados Unidos esq. Fulgencio R. Moreno
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-ee-uu', name => 'Asunción - EE.UU.',
                     latitude => -25.2906196, longitude => -57.6286212,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Independencia Nacional esq. 25 de Noviembre
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-eusebio-ayala', name => 'Asunción - Eusebio Ayala',
                     latitude => -25.3819046, longitude => -56.9621699,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Gral. Santos esq. Fernando de la Mora
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-gral-santos', name => 'Asunción - Gral Santos',
                     latitude => -25.3089625, longitude => -57.6160394,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Julio Correa casi Elias Ayala
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-julio-correa', name => 'Asunción - Julio Correa',
                     latitude => -25.2726326, longitude => -57.5656401,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Sta. Teresa
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-matrix', name => 'Asunción - Matrix',
                     latitude => -25.2894519, longitude => -57.5543115,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Palma esq. Ntra. Sra. de la Asunción
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-palma-microcentro', name => 'Asunción - Palma Microcentro',
                     latitude => -25.2823433, longitude => -57.634377,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Sta Teresa esq. Aviadores del Chaco
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-paseo-la-galeria', name => 'Asunción - Paseo La Galería',
                     latitude => -25.2840915, longitude => -57.5635901,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Sacramento y Santísima Trinidad
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-santisima-trinidad', name => 'Asunción - Santísima Trinidad',
                     latitude => -25.2587558, longitude => -57.5795982,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Eusebio Ayala 4501
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-shopping-multiplaza', name => 'Asunción - Shopping Multiplaza',
                     latitude => -25.3167144, longitude => -57.5719885,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Mcal. López esq. Tte. Malutin
SELECT upsert_branch(place_code => 'UENO', remote_code => 'asuncion-villa-morra', name => 'Asunción - Villa Morra',
                     latitude => -25.2936522, longitude => -57.5804096,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x Bartolome de las Casas
SELECT upsert_branch(place_code => 'UENO', remote_code => 'bartolome-de-las-casas', name => 'Bartolomé de las Casas',
                     latitude => -25.317188, longitude => -57.607655,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Eligio Ayala esq. Juan E. O'Leary
SELECT upsert_branch(place_code => 'UENO', remote_code => 'caacupe', name => 'Caacupé',
                     latitude => -25.3859404, longitude => -57.1418513,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Bernardino Caballero y Gral. Díaz
SELECT upsert_branch(place_code => 'UENO', remote_code => 'caaguazu', name => 'Caaguazú',
                     latitude => -25.46129, longitude => -56.018387,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Mcal. Estigarribia esq. Ramon Elias
SELECT upsert_branch(place_code => 'UENO', remote_code => 'capiata', name => 'Capiatá',
                     latitude => -25.3509923, longitude => -57.4476261,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Mcal. Lopez y Cristobal Colon
SELECT upsert_branch(place_code => 'UENO', remote_code => 'carapegua', name => 'Carapeguá',
                     latitude => -25.7673573, longitude => -57.2390964,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Gral. Morínigo c/ 14 de mayo
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ced-paraguari', name => 'CED Paraguarí',
                     latitude => -25.6196667, longitude => -57.1492778,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta 3 Gral. Elizardo Aquino esq. Manuel Gondra
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ced-plaza-norte', name => 'CED Plaza Norte',
                     latitude => -25.1753575, longitude => -57.4855344,
                     schedule => 'Lunes a lunes de 09:00 a 01:00');

-- Av. Mariscal López 3794, Asunción 001412
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ced-villamorra', name => 'CED Villamorra',
                     latitude => -25.294233, longitude => -57.5804542,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank - WTC Ciudad del Este
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ciudad-del-este', name => 'Ciudad del Este',
                     latitude => -25.494859, longitude => -54.633159,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x CDE I
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ciudad-del-este-i', name => 'Ciudad del Este I',
                     latitude => -25.531631, longitude => -54.610153,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x CDE II
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ciudad-del-este-ii', name => 'Ciudad del Este II',
                     latitude => -25.5016813, longitude => -54.6674706,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x CDE III
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ciudad-del-este-iii', name => 'Ciudad del Este III',
                     latitude => -25.5105119, longitude => -54.6403241,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x CDE IV LOS LAGOS
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ciudad-del-este-iv', name => 'Ciudad del Este IV',
                     latitude => -25.5103252, longitude => -54.6181941,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Pdte. Franco e/ Gral. Garay y 14 de Mayo
SELECT upsert_branch(place_code => 'UENO', remote_code => 'concepcion', name => 'Concepción',
                     latitude => -23.4075809, longitude => -57.4444525,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta 1 esq. Padre Mauricio
SELECT upsert_branch(place_code => 'UENO', remote_code => 'coronel-bogado', name => 'Coronel Bogado',
                     latitude => -27.1558903, longitude => -56.2383403,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Tuyuti esq. José Segundo Decoud
SELECT upsert_branch(place_code => 'UENO', remote_code => 'coronel-oviedo', name => 'Coronel Oviedo',
                     latitude => -25.4461426, longitude => -56.4395101,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. 14 de Mayo y Julia Miranda Cueto
SELECT upsert_branch(place_code => 'UENO', remote_code => 'curuguaty', name => 'Curuguaty',
                     latitude => -24.4726545, longitude => -55.6922668,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Denis Roa
SELECT upsert_branch(place_code => 'UENO', remote_code => 'denis-roa', name => 'Denis Roa',
                     latitude => -25.2974003, longitude => -57.5629521,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Cristo Rey esq. Ruta Internacional
SELECT upsert_branch(place_code => 'UENO', remote_code => 'encarnacion-i', name => 'Encarnación I',
                     latitude => -27.3489079, longitude => -55.8577688,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Gral Aquino 1848
SELECT upsert_branch(place_code => 'UENO', remote_code => 'encarnacion-ii-shopping-costanera', name => 'Encarnación II (Shopping Costanera)',
                     latitude => -27.3257013, longitude => -55.8757692,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x España y Sacramento
SELECT upsert_branch(place_code => 'UENO', remote_code => 'espana-y-sacramento', name => 'España y Sacramento',
                     latitude => -25.2863037, longitude => -57.5884906,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Mcal. López N° 390 esq. Pikysyry
SELECT upsert_branch(place_code => 'UENO', remote_code => 'fernando-de-la-mora-zona-norte', name => 'Fernando de la Mora - Zona Norte',
                     latitude => -25.3127847, longitude => -57.529626,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Mcal. Estigarribia N° 605 esq. Primera Junta Municipal
SELECT upsert_branch(place_code => 'UENO', remote_code => 'fernando-de-la-mora-zona-sur', name => 'Fernando de la Mora - Zona Sur',
                     latitude => -25.3227982, longitude => -57.5549946,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Departamental D027
SELECT upsert_branch(place_code => 'UENO', remote_code => 'fuente-shopping', name => 'Fuente Shopping',
                     latitude => -25.334127, longitude => -57.5369199,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Mcal López casi Iturbe
SELECT upsert_branch(place_code => 'UENO', remote_code => 'horqueta', name => 'Horqueta',
                     latitude => -23.3492441, longitude => -57.045097,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta N° 1 Mcal. López esq. Gral. Caballero
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ita', name => 'Itá',
                     latitude => -25.5075802, longitude => -57.3580501,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Santiviago V. km 30,5
SELECT upsert_branch(place_code => 'UENO', remote_code => 'itaugua-paseo-guarani', name => 'Itauguá - Paseo Guaraní',
                     latitude => -25.3954987, longitude => -57.3463738,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- 15 de Noviembre
SELECT upsert_branch(place_code => 'UENO', remote_code => 'j-augusto-saldivar', name => 'J. Augusto Saldivar',
                     latitude => -25.443143, longitude => -57.443314,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Jose A Flores esq. Roque Gonzalez
SELECT upsert_branch(place_code => 'UENO', remote_code => 'juan-eulogio-estigarribia', name => 'Juan Eulogio Estigarribia',
                     latitude => -25.3791118, longitude => -55.6960338,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Las Residentas esq. Capitan Arzamendia
SELECT upsert_branch(place_code => 'UENO', remote_code => 'katuete', name => 'Katueté',
                     latitude => -24.2510923, longitude => -54.766213,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Cacique Lambaré esq. San Vicente
SELECT upsert_branch(place_code => 'UENO', remote_code => 'lambare', name => 'Lambaré',
                     latitude => -25.3431738, longitude => -57.6213719,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Lambaré 110706
SELECT upsert_branch(place_code => 'UENO', remote_code => 'lambare-ii-el-pueblo', name => 'Lambaré II (El Pueblo)',
                     latitude => -25.3345524, longitude => -57.6128612,
                     schedule => 'Lunes a domingo de 06:00 a 22:00');

-- Av. Colón casi Mcal. López
SELECT upsert_branch(place_code => 'UENO', remote_code => 'limpio', name => 'Limpio',
                     latitude => -25.1684925, longitude => -57.4752256,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Dr. Manuel Gondra
SELECT upsert_branch(place_code => 'UENO', remote_code => 'loma-plata', name => 'Loma Plata',
                     latitude => -22.3884748, longitude => -59.8367082,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- San Lorenzo
SELECT upsert_branch(place_code => 'UENO', remote_code => 'luisito-nemby', name => 'Luisito Ñemby',
                     latitude => -25.3891095, longitude => -57.5252082,
                     schedule => 'Lunes a sábado de 07:30 a 21:00 - domingo 07:30 a 21:00');

-- Cerro Corá esq. Fulgencio Yegros y Valeriano Zeballos
SELECT upsert_branch(place_code => 'UENO', remote_code => 'luque', name => 'Luque',
                     latitude => -25.2677358, longitude => -57.4905258,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av.Elizardo Aquino N° 999
SELECT upsert_branch(place_code => 'UENO', remote_code => 'luque-los-jardines', name => 'Luque - Los Jardines',
                     latitude => -25.2649642, longitude => -57.5062015,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. María Auxiliadora esq. Ruta 6
SELECT upsert_branch(place_code => 'UENO', remote_code => 'maria-auxiliadora', name => 'María Auxiliadora',
                     latitude => -26.5303125, longitude => -55.2594375,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Transacho Km 14
SELECT upsert_branch(place_code => 'UENO', remote_code => 'mariano-alonso-expo', name => 'Mariano Alonso - Expo',
                     latitude => -25.2301068, longitude => -57.5359311,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- ueno bank x MRA
SELECT upsert_branch(place_code => 'UENO', remote_code => 'mariano-roque-alonso', name => 'Mariano Roque Alonso',
                     latitude => -25.216888, longitude => -57.530304,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Acceso Sur casi Ytororo
SELECT upsert_branch(place_code => 'UENO', remote_code => 'nemby', name => 'Ñemby',
                     latitude => -25.3922105, longitude => -57.5454887,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Gaspar R. de Francia esq. C. Unidas
SELECT upsert_branch(place_code => 'UENO', remote_code => 'obligado', name => 'Obligado',
                     latitude => -27.0603278, longitude => -55.6330379,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Mcal. López c/ Mcal. Estigarribia
SELECT upsert_branch(place_code => 'UENO', remote_code => 'pedro-juan-caballero', name => 'Pedro Juan Caballero',
                     latitude => -22.5370935, longitude => -55.7333962,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Irala esq. Alberdi. Barrio Loma Clavel
SELECT upsert_branch(place_code => 'UENO', remote_code => 'pilar', name => 'Pilar',
                     latitude => -26.8608259, longitude => -58.2991316,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Eusebio Ayala esq. Bruno Guggiari
SELECT upsert_branch(place_code => 'UENO', remote_code => 'pinoza', name => 'Pinozá',
                     latitude => -25.3050417, longitude => -57.6078063,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Carlos A. López N° 1058 casi De la Conquista
SELECT upsert_branch(place_code => 'UENO', remote_code => 'sajonia', name => 'Sajonia',
                     latitude => -25.2889744, longitude => -57.6547427,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- San Lorenzo 111439
SELECT upsert_branch(place_code => 'UENO', remote_code => 'salemma', name => 'Salemma',
                     latitude => -25.334127, longitude => -57.5369199,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Paraguay esq. Carlos Mendez
SELECT upsert_branch(place_code => 'UENO', remote_code => 'salto-del-guaira', name => 'Salto del Guairá',
                     latitude => -24.0594477, longitude => -54.3072757,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Mcal. F.S. López esq. Ntra Sra.de la Asunción
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-alberto', name => 'San Alberto',
                     latitude => -24.9764055, longitude => -54.9153217,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Luis F. Vache
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-bernardino', name => 'San Bernardino',
                     latitude => -25.3119506, longitude => -57.296967,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Mariscal Francisco Solano López
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-estanislao', name => 'San Estanislao',
                     latitude => -24.6629189, longitude => -56.4432923,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Mcal Estigarribia esq. Fulgencio Yegros
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-ignacio', name => 'San Ignacio',
                     latitude => -26.8877399, longitude => -57.0217377,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Mcal. Estigarribia N° 360 c/Paí Fariña
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-juan-nepomuceno', name => 'San Juan Nepomuceno',
                     latitude => -26.1119725, longitude => -55.9385724,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Mcal Estigarribia Km 15. Locales 15 y 16
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-lorenzo-shopping', name => 'San Lorenzo - Shopping',
                     latitude => -25.3432611, longitude => -57.4886878,
                     schedule => 'Lunes a sábado de 9:00 a 21:00 y domingos de 10:00 a 21:00');

-- ueno bank x San Pedro del Ycuamandiyú
SELECT upsert_branch(place_code => 'UENO', remote_code => 'san-pedro-del-ycuamandiyu', name => 'San Pedro del Ycuamandiyú',
                     latitude => -24.0897168, longitude => -57.0736279,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Carlos A. López esq. E. Ayala
SELECT upsert_branch(place_code => 'UENO', remote_code => 'santa-rita', name => 'Santa Rita',
                     latitude => -25.7939167, longitude => -55.0873214,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Gral. Aquino c/Prof. Pedro González
SELECT upsert_branch(place_code => 'UENO', remote_code => 'santa-rosa-del-aguaray', name => 'Santa Rosa del Aguaray',
                     latitude => -23.8264114, longitude => -56.5193704,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta 5 Gral. Bernardino Caballero
SELECT upsert_branch(place_code => 'UENO', remote_code => 'shopping-dubai-pjc', name => 'Shopping Dubai PJC',
                     latitude => -22.561768, longitude => -55.712585,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Tebicuary - Itape
SELECT upsert_branch(place_code => 'UENO', remote_code => 'tebicuary', name => 'Tebicuary',
                     latitude => -25.7706664, longitude => -56.6507848,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Cptan. Pedro Juan Caballero esq. Fidel Maíz
SELECT upsert_branch(place_code => 'UENO', remote_code => 'tobati', name => 'Tobatí',
                     latitude => -25.261613, longitude => -57.0831997,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta Transchaco Km 32 esq. Laudo Hayes
SELECT upsert_branch(place_code => 'UENO', remote_code => 'villa-hayes', name => 'Villa Hayes',
                     latitude => -25.0898548, longitude => -57.5313073,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Av. Carlos A. Lopez esq. Mcal. Estigarribia
SELECT upsert_branch(place_code => 'UENO', remote_code => 'villarrica', name => 'Villarrica',
                     latitude => -25.7811119, longitude => -56.4465201,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');

-- Ruta II Mcal. Estigarribia esq. Fulgencio Yegros
SELECT upsert_branch(place_code => 'UENO', remote_code => 'ypacarai', name => 'Ypacaraí',
                     latitude => -25.4023236, longitude => -57.2882353,
                     schedule => 'Lunes a sábados de 08:00 a 18:00 y domingos de 08:00 a 17:00');
