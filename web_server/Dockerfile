FROM nginx

MAINTAINER Alex Bondar<abondar1992@gmail.com>


COPY nginx.conf /etc/nginx/nginx.conf
COPY fastcgi.conf /etc/nginx/fastcgi.conf
COPY proxy.conf /etc/nginx/proxy.conf
COPY mime.types /etc/nginx/mime.types
COPY access.log /etc/nginx/logs/access.log
COPY error.log /etc/nginx/logs/error.log



COPY cert.pem /etc/ssl/cert.pem
COPY plain.key /etc/ssl/plain.key
