FROM holoinsight/server-base:1.0.2

COPY --chown=admin:admin --from=apache/skywalking-java-agent:8.15.0-java8 /skywalking/agent /home/admin/skywalking-agent
# Temporarily disable the grpc plugin, because it will cause our business code to throw exceptions.
RUN rm /home/admin/skywalking-agent/plugins/apm-grpc-1.x-plugin-8.15.0.jar

#COPY --from=holoinsight/front:test --chown=admin:admin /dist.zip /home/admin/dist.zip
COPY --from=holoinsight/front:12 --chown=admin:admin /dist.zip /home/admin/dist.zip
RUN unzip -d /home/admin/holoinsight-server-static/ /home/admin/dist.zip && rm /home/admin/dist.zip

COPY scripts/docker/sc /usr/local/bin/
COPY scripts/docker/ensure_supervisord.sh /usr/local/bin/
COPY scripts/docker/supervisord.conf /etc/supervisord.conf

COPY scripts/docker/bin/app.ini /etc/supervisord.d/app.ini
RUN true
COPY --chown=admin:admin scripts/docker/bin/*.sh /home/admin/bin/
RUN true
COPY scripts/docker/entrypoint.sh /entrypoint.sh
RUN true

# fix permissions
RUN chown -R admin:admin /home/admin

USER admin
WORKDIR /home/admin

EXPOSE 80

COPY scripts/api /home/admin/api
COPY scripts/docker/nginx.conf /etc/nginx/nginx.conf

COPY --chown=admin:admin server/all-in-one/all-in-one-bootstrap/target/holoinsight-server.jar /home/admin/app.jar
RUN echo `date` > /home/admin/build-time

ENTRYPOINT ["/entrypoint.sh"]
