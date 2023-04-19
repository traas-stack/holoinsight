FROM holoinsight/server-base:1.0.2

COPY --chown=admin:admin --from=apache/skywalking-java-agent:8.15.0-java8 /skywalking/agent /home/admin/skywalking-agent

COPY ./entrypoint.sh /entrypoint.sh
COPY ./target/demo-server.jar /home/admin/demo-server.jar

ENTRYPOINT ["/entrypoint.sh"]
