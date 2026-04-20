FROM devopsfaith/krakend:2.6.0

COPY krakend.json /etc/krakend/krakend.json

EXPOSE 8080

ENTRYPOINT ["/usr/bin/krakend"]
CMD ["run", "-d", "-c", "/etc/krakend/krakend.json", "-p", "8080"]
