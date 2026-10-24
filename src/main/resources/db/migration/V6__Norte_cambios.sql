SELECT upsert_place(code => 'NORTE', name => 'Norte Cambios', type => 'BUREAU');


-- the remote_code is the branch id used by the site to load the rates (setActiveBranch), there is no 7
SELECT upsert_branch(place_code => 'NORTE', remote_code => '1', name => 'Casa Matriz - Pedro Juan Caballero',
                     latitude => -22.544444, longitude => -55.730167, phone_number => '0336 274 580 / 0971 824 454',
                     email => 'nortecambios@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '2', name => 'Asunción',
                     latitude => -25.281628, longitude => -57.635821, phone_number => '021 452 011 / +595 981 362 081',
                     email => 'norteasu@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '3', name => 'Ciudad del Este',
                     latitude => -25.511862, longitude => -54.612858, phone_number => '061 511 004 / +595 981 420 284',
                     email => 'nortecde@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '4', name => 'Bella Vista Norte',
                     latitude => -22.115884, longitude => -56.518526, phone_number => '+595 983 480 310',
                     email => 'nortebv@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '5', name => 'Capitán Bado',
                     latitude => -23.268422, longitude => -55.534384, phone_number => '+595 982 572 702',
                     email => 'nortebado@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '6', name => 'Concepción',
                     latitude => -23.407694, longitude => -57.444028, phone_number => '+595 976 442 800',
                     email => 'norteconc@nortecambios.com.py');
SELECT upsert_branch(place_code => 'NORTE', remote_code => '8', name => 'Saltos del Guairá',
                     latitude => -24.060445, longitude => -54.306949, phone_number => '+595 982 223 088',
                     email => 'nortesalto@nortecambios.com.py');
