FROM azul/zulu-openjdk:8 as jdk
# azul/zulu-openjdk is active and supports amd64 and arm64v8

FROM centos:7

# install jdk8
COPY --from=jdk /usr/lib/jvm/zulu8 /opt/java8

# add admin user
RUN groupadd --gid 500 admin && \
  useradd admin -s /bin/bash --uid 500 --gid 500 -G root

# setup java related paths
ENV JAVA_HOME=/opt/java8
ENV PATH=$PATH:$JAVA_HOME/bin
RUN ln -s /opt/java8 /usr/local/java

# install common used softwares
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
  yum -y -q install epel-release && yum install -y -q sudo net-tools iproute dstat which supervisor stress unzip jq screen nginx wget telnet cronolog less lsof gettext && \
  yum -y -q clean all && \
  rm -rf /var/cache/yum

# install arthas
RUN wget -qO /opt/arthas.zip https://arthas.aliyun.com/download/latest_version?mirror=aliyun && \
    unzip -d /opt/arthas /opt/arthas.zip >/dev/null && \
    rm /opt/arthas.zip

RUN echo 'export LANG=zh_CN.UTF-8' >> /etc/profile && \
  echo 'LC_ALL=zh_CN.UTF-8' >> /etc/profile && \
  echo 'PS1="\n\e[1;37m[\e[m\e[1;32m\u\e[m\e[1;33m@\e[m\e[1;35m\h\e[m \e[1;35m`hostname`\e[m \e[4m\`pwd\`\e[m\e[1;37m]\e[m\e[1;36m\e[m\n\\$ "' >> /etc/bashrc && \
  echo 'alias vim="vi"' >> /etc/bashrc && \
  echo 'alias ll="ls -laF"' >> /etc/bashrc && \
  echo 'shell /bin/bash' >> /root/.screenrc && \
  echo 'shell /bin/bash' >> /home/admin/.screenrc && \
  echo 'admin ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers.d/admin && \
  mkdir -p /home/admin/logs && \
  mkdir /home/admin/bin && \
  echo '[ -e "/home/admin/bin/init_bashrc.sh" ] && source /home/admin/bin/init_bashrc.sh' >> /home/admin/.bashrc

VOLUME /home/admin/logs
