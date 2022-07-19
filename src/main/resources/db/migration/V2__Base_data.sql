INSERT INTO place (id, code, name, type)
VALUES (4903180, 'VISION_BANCO', 'Banco Visión', 'BANK'),
       (4903287, 'INTERFISA', 'Banco Interfisa', 'BANK'),
       (1, 'MaxiCambios', 'MaxiCambios', 'BUREAU'),
       (4, 'ALBERDI', 'Cambios Alberdi', 'BUREAU'),
       (70, 'MyD', 'Cambios MyD', 'BUREAU'),
       (92, 'CAMBIOS_CHACO', 'Cambios Chaco', 'BUREAU'),
       (74, 'AMAMBAY', 'Cambios Amambay', 'BANK'),
       (5350525, 'FeCambios', 'Fe Cambios S.A.', 'BUREAU'),
       (26176804, 'BCP', 'Banco Central del Paraguay (Cotización referencial)', 'BANK'),
       (28341254, 'EURO', 'EuroCambios', 'BUREAU'),
       (28298259, 'MUNDIAL', 'Mundial Cambios', 'BUREAU'),
       (44219168, 'ALBERDI_2', 'Cambios Alberdi', 'BUREAU'),
       (44222030, 'GNB', 'Banco GNB', 'BANK')

ON CONFLICT DO NOTHING;



INSERT INTO place_branch (id, email, image, latitude, longitude, name, phone_number, remote_code, schedule, place_id)
VALUES (2, NULL, 'http://www.maxicambios.com.py/media/1044/asuncion_multiplaza.jpg', -25.3167006000000008, -57.5722669999999965, 'Shopping Multiplaza Casa Central', '(021) 525105/8', '0', 'Lunes a Sábados: 8:00 a 21:00 Hs.
Domingos: 10:00 a 21:00 Hs.', 1),
       (3, NULL, 'http://www.maxicambios.com.py/media/1072/matriz_cde_original.jpg', -25.5083134999999999, -54.6384264000000002, 'Casa Central CDE', '(061) 573106-574270-574295-509511/13', '13', 'Lunes a viernes: 7:00 a 19:30 Hs.
 Sabados: 7:00 a 12:00 Hs.', 1),
       (5, 'matriz@cambiosalberdi.com', 'https://lh5.googleusercontent.com/p/AF1QipMxA2Nv-mtjAzqti1pUgd_Bt3z8nfbBBizklGEw=w408-h244-k-no', -25.2814109999999985, -57.6375917000000015, 'Asunción', '(021) 447.003 / (021) 447.004', 'asuncion', '07:45 horas a 17:00 horas de Lunes a Viernes, 07:45 horas a 12:00 horas Sábados', 4),
       (6, NULL, 'https://lh5.googleusercontent.com/p/AF1QipP9gq7gRfgXTFGdQFGJYWLZGq_9SSLJ_pKYN4Uk=w408-h306-k-no', -25.296214299999999, -57.5766947999999985, 'Villa Morra', '(021) 609.905 / (021) 609.906', 'villamorra', '08:00 horas a 17:00 horas de Lunes a Viernes, 08:00 horas a 12:00 horas Sábados', 4),
       (7, NULL, NULL, -25.5098203999999988, -54.6164126999999979, 'SUCURSAL 1 CDE', 'Teléfonos: (061) 500.135 / (061) 500.417', 'cde', '07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados', 4),
       (8, NULL, NULL, -24.0552759999999992, -54.3246485000000021, 'SALTO DEL GUAIRÁ', 'Teléfonos: (046) 243.158 / (046) 243.159', 'salto', '08:00 horas a 16:00 horas de Lunes a Viernes, 07:30 horas a 11:30 horas Sábados', 4),
       (9, NULL, 'https://lh5.googleusercontent.com/p/AF1QipM0yx3fvWQArt0kY6EwyaaAsgX1jYRy3OuIobjr=w408-h725-k-no', -25.3459183999999986, -57.5151255000000035, 'San Lorenzo', 'Teléfonos: (021) 571.215 / (021) 571.216', 'sanlo', '08:00 horas a 17:00 horas de Lunes a Viernes, 08:00 horas a 12:00 horas Sábados', 4),
       (10, NULL, NULL, -25.5095270999999997, -54.6485326000000029, 'CDE KM 4', 'Teléfonos: (061) 571.540 / (061) 571.536', 'cde2', '07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados', 4),
       (11, NULL, 'https://lh5.googleusercontent.com/p/AF1QipOAtjZef_kGv14qJ4h68Rt4CKOxxwYXPJW30BUY=w408-h306-k-no', -27.3314552999999982, -55.8670186000000015, 'ENCARNACIÓN', 'Teléfonos: (071) 205.154 / (071) 205.120 / (071) 205.144', 'enc', '07:45 horas a 17:00 horas de Lunes a Viernes, 07:45 horas a 12:00 horas Sábados', 4),
       (71, NULL, 'https://www.mydcambios.com.py/uploads/sucursal/2/_principal_myd_cambios_matriz.jpg', -25.2814739999999993, -57.6372590000000002, 'Casa Matriz', '021451377/9', '2', NULL, 70),
       (72, NULL, 'https://www.mydcambios.com.py/uploads/sucursal/3/_principal_img_20160205_wa0007.jpg', -25.2941819999999993, -57.579189999999997, 'Villa Morra', '021-662537 /  021-609662', '3', NULL, 70),
       (73, NULL, 'https://www.mydcambios.com.py/uploads/sucursal/4/_principal_img_20160218_wa0060.jpg', -25.5087989999999998, -54.6396129999999971, 'Agencia KM4 - Cotizaciones', '021-662537 /  021-609662', '4', NULL, 70),
       (75, NULL, NULL, NULL, NULL, 'Central', NULL, NULL, NULL, 74),
       (93, 'cmendoza@cambioschaco.com.py ', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/Casa-Matriz-1.jpeg', -25.2946524681736413, -57.5782352685928345, 'Casa Central - Asuncion', '(595) 21-659-8000', '1', 'Semana: 08:30 A 17:30hs, Sabado: 08:30 A 12:00hs, Domingo: Cerrado', 92),
       (94, 'vmorra@cambioschaco.com.py', NULL, -25.2944936275999979, -57.5810824334621429, 'Shopping Villa Morra - Asuncion', '(595) 21-604 923 (RA)', '2', 'Semana: 08:30 A 20:00 hs, Sabado: 08:30 A 20:00 hs, Domingo: null', 92),
       (95, 'mplaza@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/multiplazaweb.jpg', -25.3165456187846267, -57.5715659558773041, 'Shopping Multiplaza - Asuncion', '(595) 21- 520 410 (RA)', '3', 'Semana: 08:30hs. A 20:00hs., Sabado: 08:30hs. A 20:00hs., Domingo: null', 92),
       (96, 'cmedoza@cambioschaco.com.py', NULL, -25.2817614545092013, -57.6352925598621368, 'Palma', '(595) 21-445-315', '30', 'Semana: 08:15 A 19:00 hs, Sabado: 09:00 A 19:00 hs, Domingo: null', 92),
       (97, 'slorenzo@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/sanloweb.jpg', -25.345758, -57.5088739999999987, 'Agencia San Lorenzo', '(595) 21-576 554 / 585 500', '6', 'Semana: 08:30hs. A 17:00hs., Sabado: 08:30hs. A 13:00hs., Domingo: null', 92),
       (98, 'aeropuerto@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/aeroweb.jpg', -25.2417846640509502, -57.5157451629638672, 'Aeropuerto Intl Silvio Pettirossi', '(595) 21- 645 702 (RA)', '7', 'Semana: 24 horas, Sabado: 24 horas, Domingo: 24 horas', 92),
       (99, 'lambare@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/lambare_web.jpg', -25.3343488071345213, -57.6129055023193359, 'Paseo Lambaré', '(595) 21- 332 354 (RA)', '8', 'Semana: 08:30hs. A 18:30hs., Sabado: 08:30hs. A 13:00hs., Domingo: null', 92),
       (100, 'vlopez@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/adrian_web.jpg', -25.5105959459406897, -54.6132190525804617, 'Sucursal Adrián Jara - CDE', '(595) 61-509 600 - 509 500 (RA)', '9', 'Semana: 07:00 A 17:00 hs, Sabado: 07:00 A 16:00 hs, Domingo: null', 92),
       (101, 'mariog@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/cdekm4web.jpg', -25.5102952935592917, -54.6402985453687506, 'Supercarretera KM 4 - CDE', '(595) 61-570 048 (RA)', '10', 'Semana: 07:30 A 19:15 hs, Sabado: 07:30 A 19:00 hs, Domingo: null', 92),
       (102, 'mariog@cambioschaco.com.py', NULL, -25.5110119238925819, -54.6086261048981214, 'Itá Ybate - CDE', '(595) 61-511 911 (RA)', '11', 'Semana: 06:00 A 15:45, Sabado: Y MIERCOLES = 05:00 A 15:00 hs, Domingo: null', 92),
       (103, 'vlopez@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/km7_web.jpg', -25.4984104478873661, -54.666031122305867, 'KM 7 - CDE', '(595) 61 578790 (RA)', '12', 'Semana: 07:00 A 19:30 hs, Sabado: 07:00 A 19:15 hs, Domingo: null', 92),
       (104, 'cdekm3@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/web-km3.jpg', -25.5090643999999998, -54.6338976999999986, 'Noblesse - KM 3,5 - CDE', '(595) 61 570050 (RA)', '13', 'Semana: 07:30 A 18:00 hs, Sabado: 7:30 A 13:00 hs, Domingo: null', 92),
       (105, 'vlopez@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/curupaty-1.jpg', -25.5139662717053177, -54.612792916602757, 'Curupayty - CDE', '(595) 61 508.900 - 508.934', '14', 'Semana: 07:00 A 17:00 hs, Sabado: 07:00 A 13:00 hs, Domingo: null', 92),
       (106, 'vlopez@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/toledo_web.jpg', -25.5081969999999991, -54.6099680000000021, 'Toledo - CDE', '(595) 61 514900-510081-510089', '15', 'Semana: 06:00 A 16:00 hs, Sabado: 06:00 A 16:00 hs, Domingo: null', 92),
       (107, 'mariog@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/hernandarias1.jpg', -25.3963265000000007, -54.6393521000000035, 'Hernandarias', '(0631) 21804 - 21769 - 21592', '20', 'Semana: 07:15 A 17:00 hs, Sabado: 07:00 A 13:00 hs, Domingo: CERRADO', 92),
       (108, 'mariog@cambioschaco.com.py', NULL, -25.4179262000000001, -54.617617199999998, 'Itaipú', '(595) 5997278', '22', 'Semana: 07:30 A 15:00 hs, Sabado: CERRADO, Domingo: CERRADO', 92),
       (109, 'mesaenca@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/3.jpg', -27.340872000000001, -55.857523999999998, 'Sucursal Super 6 - ENC', '(595) 71-206 201 (RA)', '23', 'Semana: 07:45 A 19:30 hs, Sabado: 07:45 A 19:30 hs, Domingo: null', 92),
       (110, 'mesaenca@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/zona_altaweb.jpg', -27.3315437535024977, -55.8665084838867188, 'Zona Alta - ENC', '(595) 71-209 760/2', '24', 'Semana: 07:45 A 17:15 hs, Sabado: 07:45 A 13:30 hs, Domingo: CERRADO', 92),
       (111, 'mesaenca@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/2.jpg', -27.3586993469064446, -55.8490392565727234, 'EBY  Circuito Comercial - ENC', '(595) 71- 204249, 203911, 204121', '19', 'Semana: 07:45 A 17:15 hs, Sabado: 07:45 A 13:30 hs, Domingo: CERRADO', 92),
       (112, 'santarita@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/rita_web.jpg', -25.801098598609105, -55.092414915561676, 'Sucursal Santa Rita', '(0673) 220.866 (RA)', '27', 'Semana: 07:00 A 16:15 hs, Sabado: 07:00 A 12:00 hs, Domingo: CERRADO', 92),
       (113, 'chacopjc@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/pjc_web.jpg', -22.5442792297559897, -55.7289969921112061, 'Sucursal Pedro Juan Caballero', '(595) 336-274 090 (RA)', '28', 'Semana: 07:30 A 18:00 hs, Sabado: 07:30 A 18:00 hs, Domingo: CERRADO', 92),
       (114, 'chacopjc@cambioschaco.com.py', 'http://www.cambioschaco.com.py/wp-content/uploads/2015/10/pjc1_wed.jpg', -22.5537964731004301, -55.7249361276626587, 'Agencia Pedro Juan Caballero', '(595) 336-273518/9', '29', 'Semana: 07:30 A 17:00 hs, Sabado: 07:30 A 17:00 hs, Domingo: CERRADO', 92),
       (115, 'macosta@cambioschaco.com.py', NULL, -25.5098440000000011, -54.6074150000000031, 'Monseñor Rodriguez - CDE', '061 509-700', '32', 'Semana: 05:45 A 15:30 hs, Sabado: Y MIERCOLES = 05:00 A 14:30 hs, Domingo: Cerrado', 92),
       (4903181, NULL, NULL, -25.3128886195150002, -57.542591359524998, 'Supermercado Los Jardines Fdo. de la Mora Zona Norte', '4143000', 'los-jardines-fdo.-de-la-mora-zona-norte', 'Lunes - Viernes: 13:00 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903182, NULL, NULL, -25.4966120151810003, -57.4733253687870018, 'Supermercado Bonimar Guarambaré', '4143000', 'bonimar-guarambare', 'Lunes - Viernes: 11:00 - 19:00
Sábado: 09:00 - 17:00', 4903180),
       (4903183, NULL, NULL, -25.3173402148760012, -57.6196001447049966, 'Supermercado el Pueblo - San Vicente', '4143000', 'supermercado-el-pueblo-san-vicente', 'Lunes - Viernes: 13:00 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903184, NULL, NULL, -25.5449325134839995, -54.6087582491970025, 'Supermercado Gran Vía - CDE', '4143000', 'supermercado-gran-via', 'Lunes - Viernes: 13:00 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903185, NULL, NULL, -25.467242161443, -56.4460253685239977, 'Shopping D Coronel Oviedo', '4143000', 'shopping-coronel-oviedo', 'Lunes - Viernes: 09:00 - 20:00
Sábado: 09:00 - 18:00
Domingo: 10:00 - 15:00', 4903180),
       (4903186, NULL, NULL, -25.2894894575029987, -57.5007457999999971, 'Supermercado España Laurelty', '4143000', 'laurelty-luque', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903187, NULL, NULL, -22.5598529685010014, -55.7089682630560006, 'Supermercado Maxi - PJC', '4143000', 'supermercado-maxi', 'Lunes - Viernes: 13:00 - 20:00
Sábado: 08:00 - 13:00', 4903180),
       (4903188, NULL, NULL, -25.3231246773219993, -57.6356389644179998, 'Universidad Católica de Asunción', '4143000', 'universidad-catolica', 'Lunes - Viernes: 11:00 - 19:00', 4903180),
       (4903189, NULL, NULL, -25.3525010969969991, -57.4445093831349993, 'Supermercado España Capiata', '4143000', 'supermercado-espana-capiata', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903190, NULL, NULL, -25.2853714990210001, -57.6211761084599985, 'Supermercado España Centro', '4143000', 'supermercado-espana-centro', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 09:00 - 13:00', 4903180),
       (4903191, NULL, NULL, -25.4705063075640012, -55.6922702000000029, 'Curuguaty', '4143000', 'curuguaty1', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 13:00', 4903180),
       (4903192, NULL, NULL, -25.3528137780170013, -57.4444798788359989, 'Salemma Super Center', '4143000', 'salemma-super-center', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 08:00 - 12:00', 4903180),
       (4903193, NULL, NULL, -25.5104741856989996, -54.6403124951069969, 'Ciudad del Este IV', '4143000', 'ciudad-del-este-iv', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903194, NULL, NULL, -24.1957243112299984, -56.4348744500000024, 'Cruce Liberación', '4143000', 'cruce-liberacion', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903195, NULL, NULL, -25.4044750808670017, -54.6409726763889978, 'Hernandarias', '4143000', 'hernandarias', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903196, NULL, NULL, -25.3343071829190016, -57.6133430886379969, 'Supermercado El Pueblo - Lambaré', '4143000', 'supermercado-el-pueblo', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 09:00 - 20:00
Domingo: 09:00 - 13:30', 4903180),
       (4903197, NULL, NULL, -25.3433939232729983, -57.488560962967, 'San Lorenzo Shopping', '4143000', 'san-lorenzo-shopping', 'Lunes - Viernes: 09:00 - 20:00
Sábado: 09:00 - 20:00
Domingo: 10:00 - 15:00', 4903180),
       (4903198, NULL, NULL, -25.1764991026579992, -57.4839874464339999, 'Abasto Norte', '4143000', 'abasto-norte', 'Lunes - Viernes: 07:00 - 16:00
Sábado: 07:00 - 13:00', 4903180),
       (4903199, NULL, NULL, -25.3762879142370004, -57.5829631255169971, 'Villa Elisa', '4143000', 'villa-elisa', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903200, NULL, NULL, -27.3250747582050018, -55.8588741499999983, 'Encarnación III', '4143000', 'encarnacion-iii', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903201, NULL, NULL, -26.8273224678320013, -55.3377050203770011, 'Capitán Meza', '4143000', 'capitan-meza', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903202, NULL, NULL, -25.3756093575319994, -57.5845349000000013, 'Aregua', '4143000', 'aregua', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 09:00 - 12:30', 4903180),
       (4903203, NULL, NULL, -25.4022959148770013, -57.2882816503320029, 'Ypacarai', '4143000', 'ypacarai', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 09:00 - 12:30', 4903180),
       (4903204, NULL, NULL, -25.7675069945080004, -57.2394729740810035, 'Paraguarí', '4143000', 'paraguari', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903205, NULL, NULL, -24.0894187075070008, -57.073639574467002, 'San Pedro del Ykyamandiyú', '4143000', 'san-pedro-del-ykyamandiyu', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903206, NULL, NULL, -25.4418175297560012, -57.4426600060670012, 'Supermercado Bonimar J. A. Saldivar', '4143000', 'supermercado-bonimar-j.-a.-saldivar', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 09:00 - 13:00', 4903180),
       (4903207, NULL, NULL, -25.2980714052990017, -57.6355597055570001, 'Quinta Avenida', '4143000', 'quinta-avenida', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903208, NULL, NULL, -25.2725497937170012, -57.5656502717580025, 'Julio Correa', '4143000', 'julio-correa', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903209, NULL, NULL, -25.2942304392480004, -57.5802099150119986, 'Shopping Villa Morra', '4143000', 'shopping-villamorra', 'Lunes - Viernes: 09:00 - 20:00
Sábado: 09:00 - 20:00
Domingo: 11:00 - 15:00', 4903180),
       (4903210, NULL, NULL, -24.969304357395, -54.9143646500000031, 'San Alberto', '4143000', 'san-alberto', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903211, NULL, NULL, -25.3118936575109998, -57.2969319999999982, 'San Bernardino', '4143000', 'san-bernardino1', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 09:00 - 12:30', 4903180),
       (4903212, NULL, NULL, -25.2370782358289993, -57.539786375656, 'Hipermercado Luisito Mariano R. Alonso', '4143000', 'hipermercado-luisito-m.r.a', 'Lunes - Viernes: 15:30 - 20:00
Sábado: 08:00 - 20:00
Domingo: 08:00 - 12:00', 4903180),
       (4903213, NULL, NULL, -25.2904036426809995, -57.6285318886920024, 'Estados Unidos', '4143000', 'eeuu', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903214, NULL, NULL, -25.3819092501429999, -56.9621674023769984, 'Eusebio Ayala (Barrero Grande)', '4143000', 'eusebio-ayala-barrero-grande1', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903215, NULL, NULL, -25.2652961074950007, -57.5053892000000033, 'Hipermercado Luisito Felix Bogado', '4143000', 'eusebio-ayala-barrero-grande', 'Lunes - Viernes: 15:00 - 20:00
Sábado: 09:00 - 17:00
Domingo: 08:00 - 12:00', 4903180),
       (4903216, NULL, NULL, -25.2667999406539998, -57.4845407653440006, 'Hipermercado Luisito Luque', '4143000', 'hipermercado-luisito-luque', 'Lunes - Viernes: 15:30 - 20:00
Sábado: 09:00 - 17:00', 4903180),
       (4903217, NULL, NULL, -25.2610074020409989, -57.0833411150819998, 'Tobati', '4143000', 'tobati', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 09:00 - 12:30', 4903180),
       (4903218, NULL, NULL, -25.3796462075340017, -55.6958332000000027, 'Juan E. Estigarribia', '4143000', 'juan-e.-estigarria', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903219, NULL, NULL, -25.3152248225699985, -57.5786080883610012, 'Hipermercado Luisito E. Ayala', '4143000', 'hipermercado-luisito-e.-ayala', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 08:00 - 12:00', 4903180),
       (4903220, NULL, NULL, -25.0898782555889994, -57.5313407362460012, 'Villa Hayes', '4143000', 'villa-hayes', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903221, NULL, NULL, -25.3231472575140018, -57.5545819499999993, 'Fernando de la Mora II', '4143000', 'fernando-de-la-mora-ii', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903222, NULL, NULL, -25.5075886154209996, -57.3580452385539985, 'Ita', '4143000', 'ita', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 12:30', 4903180),
       (4903223, NULL, NULL, -25.5071129348720014, -57.5650239216889972, 'Shopping La Rural', '4143000', 'la-rural', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 09:00 - 20:00
Domingo: 09:00 - 13:30', 4903180),
       (4903224, NULL, NULL, -25.2652961074950007, -57.5053892000000033, 'Supermercado Los Jardines Luque', '4143000', 'supermercado-los-jardines', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 08:00 - 13:00', 4903180),
       (4903225, NULL, NULL, -25.5071056725350012, -57.5650239216889972, 'Villeta', '4143000', 'villeta', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903226, NULL, NULL, -25.4856514272630008, -54.7615537023770003, 'Minga Guazú', '4143000', 'minga-guazu', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903227, NULL, NULL, -25.2936782925570007, -57.5805457895840007, 'Mariscal López', '4143000', 'mariscal-lopez', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903228, NULL, NULL, -25.3450540826460013, -57.5794450046309976, 'Acceso Sur', '4143000', 'acceso-sur', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903229, NULL, NULL, -27.3313502525439986, -55.8665192542449986, 'Encarnación II', '4143000', 'encarnacion-ii', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 12:30', 4903180),
       (4903230, NULL, NULL, -25.2886215575029993, -57.6350765500000009, 'Sajonia', '4143000', 'sajonia', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903231, NULL, NULL, -25.7706398577919984, -56.6507933280329965, 'Tebicuary', '4143000', 'tebicuary', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903232, NULL, NULL, -25.2850344075010014, -57.6256835000000009, 'Constitución', '4143000', 'constitucion', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903233, NULL, NULL, -23.8258512070129989, -56.5189760000000021, 'Santa Rosa del Aguaray', '4143000', 'santa-rosa-del-aguaray', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903234, NULL, NULL, -23.3492863587430008, -57.0452310597869996, 'Horqueta', '4143000', 'horqueta', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903235, NULL, NULL, -27.1556999451299994, -56.2382685711639994, 'Coronel Bogado', '4143000', 'san-bernardino', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903236, NULL, NULL, -26.5303042635180013, -55.259378842590003, 'María Auxiliadora', '4143000', 'maria-auxiliadora', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903237, NULL, NULL, -22.3884673438410005, -59.8367058756410017, 'Loma Plata', '4143000', 'loma-plata', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903238, NULL, NULL, -26.2206579483950009, -56.0762215000000026, 'San Juan Nepomuceno', '4143000', 'san-juan-nepomuceno', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903239, NULL, NULL, -24.4726548059969993, -55.692378901589997, 'Curuguaty', '4143000', 'curuguaty', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903240, NULL, NULL, -24.2333353738399992, -55.1774569499999998, 'Salto del Guairá', '4143000', 'salto-del-guaira', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903241, NULL, NULL, -25.2587861950379988, -57.579672423943002, 'Santísima Trinidad', '4143000', 'santisima-trinidad', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903242, NULL, NULL, -25.3931436135490003, -57.3529598859989989, 'Itagua', '4143000', 'itagua', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903243, NULL, NULL, -25.2973865566439997, -57.5629023788360001, 'Denis Roa', '4143000', 'denis-roa', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903244, NULL, NULL, -26.8879892204700006, -57.0216326134920024, 'San Ignacio', '4143000', 'san-ignacio-del-loyola', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903245, NULL, NULL, -25.3484481730820015, -57.5119216243439979, 'San Lorenzo II', '4143000', 'san-lorenzo-ii', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903246, NULL, NULL, -25.5111805298619991, -54.6119373914710025, 'Ciudad del Este III', '4143000', 'ciudad-del-este-iii', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903247, NULL, NULL, -25.7939119256750011, -55.0873053399200003, 'Santa Rita', '4143000', 'santa-rita', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903248, NULL, NULL, -25.1685118455260017, -57.4751756517829975, 'Limpio', '4143000', 'limpio', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903249, NULL, NULL, -25.3550368705309985, -57.4416784712329971, 'Capiatá', '4143000', 'capiata', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903250, NULL, NULL, -26.8119015431339989, -55.7212444500000004, 'Obligado', '4143000', 'obligado', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903251, NULL, NULL, -25.2812484639160004, -57.6393276132140002, 'Oliva', '4143000', 'oliva', 'Lunes - Viernes: 08:00 - 00:45', 4903180),
       (4903252, NULL, NULL, -25.3165851163509998, -57.5713097171970034, 'Shopping Multiplaza', '4143000', 'multiplaza', 'Lunes - Viernes: 08:00 - 20:00
Sábado: 08:00 - 20:00
Domingo: 10:00 - 15:00', 4903180),
       (4903253, NULL, NULL, -24.2511090404350007, -54.7662768198360013, 'Katueté', '4143000', 'katuete', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903254, NULL, NULL, -25.3049692777830018, -57.607756335319003, 'Pinozá', '4143000', 'pinoza', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903255, NULL, NULL, -22.5351250494530007, -55.7278890205929969, 'Pedro Juan Caballero', '4143000', 'pedro-juan-caballero', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903256, NULL, NULL, -24.6628870001580012, -56.4433799309509965, 'San Estanislao', '4143000', 'san-estanislao', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903257, NULL, NULL, -25.5016927398189992, -54.6675047462950019, 'Ciudad del Este II', '4143000', 'ciudad-del-este-ii', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903258, NULL, NULL, -25.7811517071320004, -56.4464919193120025, 'Villarrica', '4143000', 'villarrica', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 13:00', 4903180),
       (4903259, NULL, NULL, -26.8610005668170011, -58.2991639795390029, 'Pilar', '4143000', 'pilar', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903260, NULL, NULL, -25.7649415076650001, -57.2420082499999978, 'Carapegua', '4143000', 'carapegua', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903261, NULL, NULL, -22.7955655832899993, -57.0576190000000025, 'Concepción', '4143000', 'concepcion', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903262, NULL, NULL, -25.4643692761930005, -56.0186123743590016, 'Caaguazú', '4143000', 'caaguazu', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 13:00', 4903180),
       (4903263, NULL, NULL, -25.2944109442609992, -57.57991105264, 'Villa Morra', '4143000', 'villa-morra', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903264, NULL, NULL, -25.3270554482690002, -57.5512667396579971, 'Fernando de la Mora I', '4143000', 'fernardo.-de-la-mora-i', 'Lunes - Viernes: 08:00 - 16:45
Sábado: 08:00 - 13:00', 4903180),
       (4903265, NULL, NULL, -25.4457292075559991, -56.4397702500000022, 'Coronel Oviedo', '4143000', 'coronel-oviedo', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903266, NULL, NULL, -25.5165455075799983, -54.61672875, 'Ciudad del Este I', '4143000', 'ciudad-del-este-1', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903267, NULL, NULL, -25.343284246355001, -57.6213624428679978, 'Lambaré', '4143000', 'lambare', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903268, NULL, NULL, -25.2372238074850017, -57.5404837499999999, 'Mariano Roque Alonso', '4143000', 'mariano-roque-alonso', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903269, NULL, NULL, -25.3919178575380009, -57.5454961000000011, 'Ñemby', '4143000', 'nemby', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903270, NULL, NULL, -25.2823202152059991, -57.6344010408069991, 'Palma', '4143000', 'palma', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903271, NULL, NULL, -25.3858984176860005, -57.1418634508910017, 'Caacupé', '4143000', 'caacupe', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903272, NULL, NULL, -27.3486175082129996, -55.8585596499999966, 'Encarnación I', '4143000', 'encarnacion-1', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903273, NULL, NULL, -25.2678461074959984, -57.4905827500000015, 'Luque', '4143000', 'luque', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903274, NULL, NULL, -25.3088220293779997, -57.6158834791329966, 'General Santos', '4143000', 'general-santos', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903275, NULL, NULL, -25.3461808700769993, -57.5068048436399977, 'San Lorenzo I', '4143000', 'san-lorenzo-1', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903276, NULL, NULL, -25.2894819999999996, -57.5543690000000012, 'Casa Matriz', '0214143000', 'casa-matriz1', 'Lunes - Viernes: 08:00 - 16:45', 4903180),
       (4903288, NULL, NULL, -25.2854439999999983, -57.630389000000001, 'CASA MATRIZ', '(021) 415 9500 (RA)', 'CASA MATRIZ', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903289, NULL, NULL, -25.2856670000000001, -57.6300280000000029, 'PLAZA URUGUAYA', '(021) 415 9500 (RA)', 'PLAZA URUGUAYA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903290, NULL, NULL, -25.294471999999999, -57.578916999999997, 'VILLA MORRA', '(021) 600 352 Cel.: (0971) 160 044 / (0983) 206 308', 'VILLA MORRA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903291, NULL, NULL, -25.3048889999999993, -57.6082219999999978, 'PINOZA', '(021) 558 250 Cel.: (0981) 900 268', 'PINOZA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903292, NULL, NULL, -25.2824999999999989, -57.6387500000000017, 'GENERAL DÍAZ', '(021) 491 784/5 - 447 701 Cel.: (0982) 402 809', 'GENERAL DÍAZ', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903293, NULL, NULL, NULL, NULL, 'FERNANDO DE LA MORA', '(021) 506 664 Cel.: (0981) 804 131', 'FERNANDO DE LA MORA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903294, NULL, NULL, -25.342492, -57.6239100000000022, 'LAMBARÉ', '(021) 904 402 Cel.: (0981) 900 356', 'LAMBARÉ', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903295, NULL, NULL, -25.3455559999999984, -57.505898000000002, 'SAN LORENZO', '(021) 575 905 Cel.: (0981) 900 309', 'SAN LORENZO', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903296, NULL, NULL, -25.2677499999999995, -57.4910560000000004, 'LUQUE', '(021) 647 501 Cel.: (0981) 900 213', 'LUQUE', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903297, NULL, NULL, -25.2382525000000015, -57.5430574000000021, 'LOMA PYTA', '(021) 752 061 Cel.: (0981) 900 271', 'LOMA PYTA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903298, NULL, NULL, -25.3491250000000008, -57.5744570000000024, 'ACCESO SUR', '(021) 943 628 Cel.: (0981) 110 853', 'ACCESO SUR', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903299, NULL, NULL, -25.3085829999999987, -57.5326669999999964, 'MCAL. LÓPEZ', '(021) 678 131/3 Cel.: (0984) 939 875', 'MCAL. LÓPEZ', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903300, NULL, NULL, -25.2984439999999999, -57.6355000000000004, '5TA AVENIDA', '(021) 393 600', '5TA AVENIDA', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903301, NULL, NULL, NULL, NULL, 'LIMPIO CENTRO', '(021) 782 024/6 Cel.: (0986) 124 167', 'LIMPIO CENTRO', 'Lunes a Viernes de 08:00 a 17:00 hs', 4903287),
       (4903302, NULL, NULL, -27.3262500000000017, -55.8671390000000017, 'ENCARNACION', '(071) 202 550 / 204 993 / 202 416 Cel.: (0981) 110 835 Cel.: (0972) 730 172', 'ENCARNACION', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903303, NULL, NULL, -27.1558610000000016, -56.2394170000000031, 'CORONEL BOGADO', '(0741) 252 445 Cel.: (0981) 900 343', 'CORONEL BOGADO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903304, NULL, NULL, -25.7678060000000002, -57.2399720000000016, 'CARAPEGUÁ', '(0532) 212 580 / 212 875/6 Cel.: (0981) 110 847', 'CARAPEGUÁ', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903305, NULL, NULL, -26.8854110000000013, -57.0283939999999987, 'SAN IGNACIO', '(0782) 232 892 / 232 896 Cel.: (0981) 110 846', 'SAN IGNACIO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903306, NULL, NULL, -25.3859439999999985, -57.1434169999999995, 'CAACUPE', '(0511) 243 405/6 Cel.: (0981) 900 323', 'CAACUPE', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903307, NULL, NULL, -25.4496110000000009, -56.4424440000000018, 'CORONEL OVIEDO', '(0521) 203 635 Cel.: (0981) 110 862', 'CORONEL OVIEDO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903308, NULL, NULL, -25.4636670000000009, -56.0174999999999983, 'CAAGUAZU CENTRO', '(0522) 43 428/9 Cel.: (0981) 950 126', 'CAAGUAZU CENTRO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903309, NULL, NULL, -25.3922220000000003, -57.3548609999999996, 'ITAUGUA', '(0294) 222 140 / 222 219 Cel.: (0981) 492 023', 'ITAUGUA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903310, NULL, NULL, -26.5271110000000014, -55.2620000000000005, 'MARIA AUXILIADORA', '(0764) 20 438 Cel.: (0981) 975 600', 'MARIA AUXILIADORA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903311, NULL, NULL, -25.3784439999999982, -55.7058060000000026, 'SUCURSAL CAMPO 9', '(0528) 222 861 Cel.: (0981) 975 500', 'SUCURSAL CAMPO 9', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903312, NULL, NULL, NULL, NULL, 'SAN ESTANISLAO', '(0343) 421 575 / 421 585 Cel.: (0981) 492 075 y (0972) 794 404', 'SAN ESTANISLAO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903313, NULL, NULL, -25.7811109999999992, -56.449111000000002, 'VILLARRICA', '(0541) 44 528/30 Cel.: (0984) 173 063', 'VILLARRICA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903314, NULL, NULL, -25.5001670000000011, -54.667028000000002, 'CIUDAD DEL ESTE', '(061) 574 151 / 574 156 Cel.: (0984) 422 238', 'CIUDAD DEL ESTE', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903315, NULL, NULL, -25.5106944999999996, -54.6143611000000035, 'CIUDAD DEL ESTE CENTRO', '(061) 510 119 / 510 977', 'CIUDAD DEL ESTE CENTRO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903316, NULL, NULL, -24.5075830000000003, -54.8536390000000011, 'NUEVA ESPERANZA', '(0464) 20 346/8 Cel.: (0982) 904 145', 'NUEVA ESPERANZA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903317, NULL, NULL, -23.8256389999999989, -56.519167000000003, 'SANTA ROSA DEL AGUARAY', '(0433) 240 075/8', 'SANTA ROSA DEL AGUARAY', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903318, NULL, NULL, -24.0708620000000018, -54.3081979999999973, 'SALTO DEL GUAIRA', '(046) 243 126 Cel.: (0985) 129 431', 'SALTO DEL GUAIRA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903319, NULL, NULL, -26.7585000000000015, -55.138694000000001, 'NATALIO', '(0765) 206 047 Cel.: (0982) 110 928', 'NATALIO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903320, NULL, NULL, -27.0583330000000011, -55.630749999999999, 'OBLIGADO', '(0717) 20 553/5 Cel.: (0982) 110 884', 'OBLIGADO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903321, NULL, NULL, -26.8580280000000009, -58.2997220000000027, 'PILAR', '(0786) 234 020/22 Cel.: (0983) 189 822 / (0971) 337 200', 'PILAR', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903322, NULL, NULL, -25.622167000000001, -57.1517499999999998, 'PARAGUARI', '(0531) 433 619/21 Cel.: (0984) 939 876', 'PARAGUARI', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903323, NULL, NULL, -26.1143059999999991, -55.9383610000000004, 'SAN JUAN NEPOMUCENO', '(0544) 320 807 Cel.: (0981) 300 093', 'SAN JUAN NEPOMUCENO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903324, NULL, NULL, -25.9758889999999987, -57.2340560000000025, 'QUIINDY', '(0536) 282 762/4 Cel.: (0985) 708 210', 'QUIINDY', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903325, NULL, NULL, -24.4767779999999995, -55.6893609999999981, 'CURUGUATY', '(048) 210 752 Cel.: (0985) 336 243 / (0971) 543 380', 'CURUGUATY', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903326, NULL, NULL, -22.5419720000000012, -55.7318059999999988, 'PEDRO JUAN CABALLERO', '(0336) 273 527 / 272 927 Cel.: (0972) 730 154 / (0986) 399 951', 'PEDRO JUAN CABALLERO', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903327, NULL, NULL, -23.4077219999999997, -57.4425829999999991, 'CONCEPCIÓN', '(0331) 241 881 / 242 929 / 242 555 Cel.: (0972) 730 171 / (0986) 420 020', 'CONCEPCIÓN', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903328, NULL, NULL, NULL, NULL, 'SANTA RITA', '(0673) 221 912/3 Cel.: (0986) 600 631 / (0972) 340 640', 'SANTA RITA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (4903329, NULL, NULL, -25.002358000000001, -55.8233790000000027, 'VAQUERÍA', '(021) 338 3826', 'VAQUERÍA', 'Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.', 4903287),
       (5350526, 'jose.gonzalez@fecambios.com.py', 'http://www.fecambios.com.py/upload/sucursales/1435688259.jpg', -25.2810950000000005, -57.6367509999999967, 'Casa Matriz  - Palma', '+595-21-451300 RA', '9', 'Lunes a Viernes 08:00-17:00 Sábados 08:30-12:00', 5350525),
       (5350527, 'julio@fecambios.com.py', 'http://www.fecambios.com.py/upload/sucursales/1435688899.jpg', -22.5368870000000001, -55.7335179999999966, 'Sucursal - Pedro Juan Caballero', '+595-336-273625/8', '8', 'Lunes a Viernes 08:00-17:00 - Sábados 08:00 - 12:00', 5350525),
       (5350528, 'jose.valdez@fecambios.com.py', 'http://www.fecambios.com.py/upload/sucursales/1435688771.jpg', -25.2944899999999997, -57.5801569999999998, 'Villa Morra', '+595-21-613181 RA', '10', 'Lunes a Viernes 08:00-17:00 - Sábados 08:30 - 12:00', 5350525),
       (5350529, 'carlos_a@fecambios.com.py', 'http://www.fecambios.com.py/upload/sucursales/1435688836.jpg', -25.5095390000000002, -54.6111969999999971, 'Sucursal Monseñor Rodriguez', '+595-61-510731', '11', 'Lunes a Viernes 07:00-15:00 - Sábados 07:30 - 11:30', 5350525),
       (5350530, 'nidia@fecambios.com.py', 'http://www.fecambios.com.py/upload/sucursales/1435687858.jpg', -25.3452100000000016, -57.507756999999998, 'Ag. San Lorenzo', '+595-21-590232', '13', 'Lunes a Viernes 08:00-17:00 - Sábados 08:30 - 12:00', 5350525),
       (26176805, 'nfo@bcp.gov.py ', 'https://www.bcp.gov.py/userfiles/images/banners/slider-background-01.jpg', -25.2781319000000018, -57.5765498000000022, 'Central', '+59521608011', '01', '', 26176804),
       (28298260, NULL, NULL, -25.5115489999999987, -54.6135159999999971, 'GLOBO CENTER GLOBO CENTER', '+595 61 509 570', '1', 'Lunes a Viernes: 07:00 a 16:00 hs. Sábado: 07:00 a 12:00 hs.', 28298259),
       (28298266, NULL, NULL, -25.2904112000000012, -57.5813995999999975, 'VILLA MORRA VILLA MORRA', '+ 595 21 663946', '7', 'Lunes a Viernes: 08:00 a 16:00 hs. Sábado: 08:00 a 12:00 hs.', 28298259),
       (28298265, NULL, NULL, -25.2822190000000013, -57.6374589999999998, 'ASUNCIÓN ASUNCIÓN', '+595 21 446 700', '6', 'Lunes a Viernes: 08:00 a 17:00 hs. Sábado: 08:00 a 12:00 hs.', 28298259),
       (28298263, NULL, NULL, -25.2920119999999997, -57.5848269999999971, 'KM4, SÚPER CARRETERA KM4, SÚPER CARRETERA', '+595 61 571 070', '4', 'Lunes a Viernes: 07:00 a 17:00 hs. Sábado: 07:00 a 12:00 hs.', 28298259),
       (28298264, NULL, NULL, -24.0636970000000012, -54.3073880000000031, 'SALTOS DEL GUAIRÁ SALTOS DEL GUAIRÁ', '+595 46 24 30 30', '5', 'Lunes a Viernes: 08:00 a 16:00 hs. Sábado: 08:00 a 12:00 hs.', 28298259),
       (28298262, NULL, NULL, -25.510788999999999, -54.6095260000000025, 'JEBAI CENTER JEBAI CENTER', '+595 61 511 162', '3', 'Lunes a Viernes: 06:30 a 15:30 hs. Sábado: 06:30 a 12:00 hs.', 28298259),
       (28298261, NULL, NULL, -25.5114319999999992, -54.6092220000000026, 'VENDOME VENDOME', '+595 61 509 577', '2', 'Lunes a Viernes: 06:30 a 15:30 hs. Sábado: 06:30 a 13:00 hs.', 28298259),
       (28341255, NULL, NULL, -25.2833172333068852, -57.6361779849045988, 'Casa Matriz', '(595-21) 440 550 - 440 560 - 494 858 - 493 320.', '1', 'Lunes a viernes de de 08:00 a 17:00 horas. Sábados: de 08:30 a 12:00 horas.', 28341254),
       (28341256, NULL, NULL, -25.2366930643656211, -57.5400293806130989, 'Sucursal Mariano Roque Alonso', '(595 -21) 750 808 / 750 444', '2', 'Lunes a viernes de de 08:00 a 17:00 horas. Sábados: de 08:30 a 12:00 horas.', 28341254),
       (28341257, NULL, NULL, -25.3168155837613789, -57.5736517497263947, 'Sucursal Multiplaza', '(595 -21)525 522 - 525 523 - 525 524 - 524 032.', '3', 'Lunes a viernes de de 08:00 a 18:00 horas. Sábados: de 08:30 a 13:00 horas.', 28341254),
       (28341258, NULL, NULL, -25.2954233572479019, -57.5808875943565965, 'Sucursal Villa Morra', '(595 -21) 608 727- 608 728.', '4', 'Lunes a viernes de de 08:00 a 17:00 horas. Sábados: de 08:30 a 12:00 horas.', 28341254),
       (44219169, NULL, NULL, -25.5095270999999997, -54.6485326000000029, 'CDE KM 4', '(061) 571.540 / (061) 571.536', 'km4', '07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados', 44219168),
       (44219170, NULL, 'https://lh5.googleusercontent.com/p/AF1QipP9gq7gRfgXTFGdQFGJYWLZGq_9SSLJ_pKYN4Uk=w408-h306-k-no', -25.296214299999999, -57.5766947999999985, 'Villa Morra', '(021) 609.905 / (021) 609.906', 'villamorra', '08:00 horas a 17:00 horas de Lunes a Viernes, 08:00 horas a 12:00 horas Sábados', 44219168),
       (44219171, NULL, NULL, -25.5098203999999988, -54.6164126999999979, 'Sucursal 1 CDE', '(061) 500.135 / (061) 500.417', 'ciudaddeleste', '07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados', 44219168),
       (44219172, NULL, NULL, -24.0552759999999992, -54.3246485000000021, 'Salto del Guairá', '(046) 243.158 / (046) 243.159', 'saltodelguaira', '08:00 horas a 16:00 horas de Lunes a Viernes, 07:30 horas a 11:30 horas Sábados', 44219168),
       (44219173, NULL, 'https://lh5.googleusercontent.com/p/AF1QipOAtjZef_kGv14qJ4h68Rt4CKOxxwYXPJW30BUY=w408-h306-k-no', -27.3314552999999982, -55.8670186000000015, 'Encarnación', '(071) 205.154 / (071) 205.120 / (071) 205.144', 'encarnacion', '07:45 horas a 17:00 horas de Lunes a Viernes, 07:45 horas a 12:00 horas Sábados', 44219168),
       (44222031, NULL, 'https://lh5.googleusercontent.com/p/AF1QipN6YrTaGexEo1YJMli9pK4ZTPQJfN07ufe2pn40=w408-h544-k-no', -25.2830259999999996, -57.5637486999999979, 'Casa central', '+595216183000', 'central', '8:30 a 13:30 - Lunes a viernes', 44222030),
       (44222032, NULL, 'https://lh5.googleusercontent.com/p/AF1QipMys851Z_uy9r65DYj68o38ALJ4GvtfPPCy6mh9=w408-h306-k-no', -25.2946981000000015, -57.5802288000000004, 'Villa morra', '+5956183000', 'villamorra', '8:30 a 13:30 - Lunes a viernes', 44222030),
       (44222033, NULL, 'https://lh5.googleusercontent.com/p/AF1QipNL73EsvtPvZcebT71u7mnrmwoOEJtKmx1aqoCY=w426-h240-k-no', -25.308952699999999, -57.5988751000000008, 'Eusebio Ayala', '+595216183001', 'eusebioayala', '8:30 a 13:30 - Lunes a viernes', 44222030),
       (44222034, NULL, 'https://lh5.googleusercontent.com/p/AF1QipPZwGJsIZeoRQ3SVbs9Kmy62oh5vzjBpBNLgdVh=w408-h544-k-no', -25.3438559999999988, -57.5074084999999968, 'San Lorenzo', '+59521570475', 'sanlorenzo', '8:30 a 13:30 - Lunes a viernes', 44222030),
       (44222035, NULL, 'https://lh5.googleusercontent.com/p/AF1QipPyr9me5BzUPouRx3smaBanU7OwgVWKpYoC2Pzp=w408-h306-k-no', -27.3261481000000011, -55.869666500000001, 'Encarnacion', '+59571202737', 'encarnacion', '8:30 a 13:30 - Lunes a viernes', 44222030),
       (44222036, NULL, 'https://lh5.googleusercontent.com/p/AF1QipPUgbipb9hOFz12zvHzmVLB3V3vroAtErRaKWoK=w408-h545-k-no', -25.508357199999999, -54.6386736999999982, 'Ciudad del Este', '+595214176509', 'cde', '8:30 a 13:30 - Lunes a viernes', 44222030)

ON CONFLICT DO NOTHING;


