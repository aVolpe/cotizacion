# Cotizaciones del Paraguay
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d54305ed13d9418f8e6b06319f0bacea)](https://app.codacy.com/app/aVolpe/cotizacion?utm_source=github.com&utm_medium=referral&utm_content=aVolpe/cotizacion&utm_campaign=badger)
[![Build Status](https://travis-ci.org/aVolpe/cotizacion.svg?branch=master)](https://travis-ci.org/aVolpe/cotizacion)
[![](https://images.microbadger.com/badges/image/avolpe/cotizacion.svg)](https://microbadger.com/images/avolpe/cotizacion "Get your own image badge on microbadger.com")
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=py.com.volpe%3Acotizaciones&metric=alert_status)](https://sonarcloud.io/dashboard?id=py.com.volpe%3Acotizaciones)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FaVolpe%2Fcotizacion.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FaVolpe%2Fcotizacion?ref=badge_shield)


Simple webpage and API that provides exchange information of current exchange
Rates of currencies in relation to the Paraguayan Guarani.

The API gather information from various popular exchange sites in Paraguay,
currently includes:

* Cambios Chaco
* Cambios Alberdi
* Cambios MyD
* Cambios Amambary
* MaxiCambios


# Endpoints

By default the server starts at port 8080.

* Current currencies: `/api/exchange/`
* Today exchange of Currency: `/api/exchange/{currency}`

## Develop endpoints

This endpoints are only available in develop mode (develop profile), 
and are disabled in production

* Init exchange places: `/api/places/init`
* Query current exchange: `/api/places/doQuery`

# Development

The step required to compile this application are stored in the
main Dockerfile.

## Back end

1. You need java 8
2. Execute in the root `./mvnw package`

## Front end

1. You need npm
2. Execute in the `client` folder `npm run build`

## Cronjobs

The system query the data every 10 minutes, and are only enabled
in production profile.

# CI and CD

This project is in an continuous delivery:

1. The code is hosted in Github
2. When a push occur, the [docker ci](https://hub.docker.com/r/avolpe/cotizacion/) is trigger 
   and a new image is generated (avolpe/cotizacion)
3. A web hook is invoked to the server
4. The server pull the new image and restart

# Links

* Current version: [https://www.volpe.com.py](https://www.volpe.com.py)

# TODO

- [x] Make the database a volume (now it restart every time)
* [ ] Add a chart to show the exchange evolution

# License

This work is released under [The MIT License](https://opensource.org/licenses/MIT)


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FaVolpe%2Fcotizacion.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FaVolpe%2Fcotizacion?ref=badge_large)
