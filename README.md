# Cotizaciones del Paraguay
[![Build Status](https://travis-ci.org/aVolpe/cotizacion.svg?branch=master)](https://travis-ci.org/aVolpe/cotizacion)

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

* Make the database a volume (now it restart every time)
