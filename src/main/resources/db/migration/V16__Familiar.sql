SELECT upsert_place(code => 'FAMILIAR', name => 'Banco Familiar', type => 'BANK');


-- The rates are the same for every branch (https://www.familiar.com.py/cambios), the branches are the
-- 'Sucursales' of https://www.familiar.com.py/sucursales-cajeros-puntos-de-pago and the remote_code is
-- their slug in the site (data-ncf-slug). All of them share the phone and the schedule.
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'acceso-sur', name => 'Acceso Sur',
                     latitude => -25.34850723, longitude => -57.57477445, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'artigas', name => 'Artigas',
                     latitude => -25.25613669, longitude => -57.58378997, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'caacupe', name => 'Caacupé',
                     latitude => -25.384801, longitude => -57.14087737, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'caaguazu-i', name => 'Caaguazu I',
                     latitude => -25.46524909, longitude => -56.01865174, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'caaguazu-ii', name => 'Caaguazu II',
                     latitude => -25.47607196, longitude => -56.01577339, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'campo-9', name => 'Campo 9',
                     latitude => -25.37844312, longitude => -55.703003, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'capiata', name => 'Capiatá',
                     latitude => -25.35327801, longitude => -57.44352179, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'carapegua', name => 'Carapeguá',
                     latitude => -25.76700306, longitude => -57.23746932, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'casa-matriz', name => 'Casa Matriz',
                     latitude => -25.28883401, longitude => -57.6398536, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cde-1', name => 'CDE 1',
                     latitude => -25.53017421, longitude => -54.61080646, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cde-2', name => 'CDE 2',
                     latitude => -25.50988696, longitude => -54.63949484, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cde-3', name => 'CDE 3',
                     latitude => -25.4997236, longitude => -54.66660646, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cde-4', name => 'CDE 4',
                     latitude => -25.51072203, longitude => -54.61232494, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cde-5', name => 'CDE 5',
                     latitude => -25.51046355, longitude => -54.6187615, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'centro', name => 'Centro',
                     latitude => -25.28432435, longitude => -57.63244252, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'cnel-oviedo-i', name => 'Cnel. Oviedo I',
                     latitude => -25.44471141, longitude => -56.44082139, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'concepcion', name => 'Concepción',
                     latitude => -23.4074482, longitude => -57.44124355, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'curuguaty', name => 'Curuguaty',
                     latitude => -24.47452857, longitude => -55.69124413, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'encarnacion', name => 'Encarnación',
                     latitude => -27.34081084, longitude => -55.85813152, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'espana', name => 'España',
                     latitude => -25.2865601, longitude => -57.61390609, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'hernandarias', name => 'Hernandarias',
                     latitude => -25.39581283, longitude => -54.63878139, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'horqueta', name => 'Horqueta',
                     latitude => -23.34109919, longitude => -57.0491012, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'itaugua', name => 'Itauguá',
                     latitude => -25.39265334, longitude => -57.35361844, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'katuete', name => 'Katuete',
                     latitude => -24.25117274, longitude => -54.76632071, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'lambare', name => 'Lambaré',
                     latitude => -25.34304247, longitude => -57.62213146, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'limpio', name => 'Limpio',
                     latitude => -25.16833865, longitude => -57.47450344, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'luque-i', name => 'Luque I',
                     latitude => -25.26769259, longitude => -57.4915021, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'luque-ii', name => 'Luque II',
                     latitude => -25.27216123, longitude => -57.4862478, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'ma-auxiliadora', name => 'Ma. Auxiliadora',
                     latitude => -26.53108356, longitude => -55.25883693, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'mariano-roque-alonso', name => 'Mariano Roque Alonso',
                     latitude => -25.23901797, longitude => -57.54169971, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'nemby', name => 'Ñemby',
                     latitude => -25.39533783, longitude => -57.54444478, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'obligado', name => 'Obligado',
                     latitude => -27.0588684, longitude => -55.63172197, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'pedro-juan-caballero', name => 'Pedro Juan Caballero',
                     latitude => -22.54005614, longitude => -55.73294855, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'pilar', name => 'Pilar',
                     latitude => -26.85921239, longitude => -58.29871016, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'pinoza', name => 'Pinozá',
                     latitude => -25.3042102, longitude => -57.60918212, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'salemma-super-center', name => 'Salemma Super Center',
                     latitude => -25.33413853, longitude => -57.5354309, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'san-ignacio', name => 'San Ignacio',
                     latitude => -26.88755632, longitude => -57.02266368, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'san-juan-bautista', name => 'San Juan Bautista',
                     latitude => -26.66908728, longitude => -57.14115339, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'san-lorenzo-i', name => 'San Lorenzo I',
                     latitude => -25.3466191, longitude => -57.51129702, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'san-lorenzo-ii', name => 'San Lorenzo II',
                     latitude => -25.34298559, longitude => -57.50591481, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'san-martin', name => 'San Martín',
                     latitude => -25.2896591, longitude => -57.57337366, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'santa-rita', name => 'Santa Rita',
                     latitude => -25.79357499, longitude => -55.08689823, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'santa-rosa', name => 'Santa Rosa',
                     latitude => -23.82555229, longitude => -56.51926736, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'santani', name => 'Santaní',
                     latitude => -24.66355618, longitude => -56.44374562, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'villa-hayes', name => 'Villa Hayes',
                     latitude => -25.08959941, longitude => -57.53160449, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'villa-morra', name => 'Villa Morra',
                     latitude => -25.29483467, longitude => -57.57876842, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
SELECT upsert_branch(place_code => 'FAMILIAR', remote_code => 'villarrica', name => 'Villarrica',
                     latitude => -25.78124839, longitude => -56.44757638, phone_number => '(021) 44 65 44',
                     schedule => 'De Lunes a Viernes 8:00 a 17:00 hs y Sábados de 8:00 a 12:00 hs');
