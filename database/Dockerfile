FROM mongo:3

ADD ./dump /
ADD mongod.conf /mongod.conf

COPY init.sh /init.sh

RUN \
 chmod +x /init.sh && \
 apt-get update && apt-get dist-upgrade -y && \
 apt-get install -y dos2unix && \
 apt-get install psmisc -y -q && \
 apt-get autoremove -y && apt-get clean && \
 dos2unix /init.sh && apt-get --purge remove -y dos2unix && \
 rm -rf /var/cache/* && rm -rf /var/lib/apt/lists/*


EXPOSE 27017

ENTRYPOINT ["/init.sh"]