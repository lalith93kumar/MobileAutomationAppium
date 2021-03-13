FROM ubuntu
LABEL maintainer "codecaigicungduoc@gmail"
WORKDIR /
SHELL ["/bin/bash", "-c"]
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-8-jdk vim curl git ruby-full unzip libglu1 libpulse-dev libasound2 libc6  libstdc++6 libx11-6 libx11-xcb1 libxcb1 libxcomposite1 libxcursor1 libxi6  libxtst6 libnss3 wget
ARG GRADLE_VERSION=5.4.1
ARG ANDROID_API_LEVEL=30
ARG ANDROID_BUILD_TOOLS_LEVEL=30.0.0
ARG EMULATOR_NAME='test'
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp \
&& unzip -d /opt/gradle /tmp/gradle-${GRADLE_VERSION}-bin.zip \
&& mkdir /opt/gradlew \
&& /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle wrapper --gradle-version ${GRADLE_VERSION} --distribution-type all -p /opt/gradlew  \
&& /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle wrapper -p /opt/gradlew
RUN wget 'https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip' -P /tmp
RUN unzip -d /opt/android /tmp/sdk-tools-linux-4333796.zip
RUN yes Y | /opt/android/tools/bin/sdkmanager --install "platform-tools" "system-images;android-${ANDROID_API_LEVEL};google_apis;x86" "platforms;android-${ANDROID_API_LEVEL}" "build-tools;${ANDROID_BUILD_TOOLS_LEVEL}" "emulator"
RUN yes Y | /opt/android/tools/bin/sdkmanager --licenses
RUN echo "no" | /opt/android/tools/bin/avdmanager --verbose create avd --force --name "test" --device "pixel" --package "system-images;android-${ANDROID_API_LEVEL};google_apis;x86" --tag "google_apis" --abi "x86"
ENV GRADLE_HOME=/opt/gradle/gradle-$GRADLE_VERSION \
ANDROID_HOME=/opt/android
ENV PATH "$PATH:$GRADLE_HOME/bin:/opt/gradlew:$ANDROID_HOME/emulator:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools"
ENV LD_LIBRARY_PATH "$ANDROID_HOME/emulator/lib64:$ANDROID_HOME/emulator/lib64/qt/lib"
RUN apt-get -qqy update && \
    apt-get -qqy --no-install-recommends install \
    ca-certificates \
    tzdata \
    zip \
    unzip \
    curl \
    wget \
    libqt5webkit5 \
    libgconf-2-4 \
    xvfb \
    gnupg \
  && rm -rf /var/lib/apt/lists/*
ARG APPIUM_VERSION=1.17.0
ENV APPIUM_VERSION=$APPIUM_VERSION
RUN apt update
RUN yes Y | apt install nodejs
RUN yes Y | apt install npm
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash
RUN npm install -g mockserver
RUN npm install -g appium@$APPIUM_VERSION --unsafe-perm=true --allow-root && \
    exit 0
ENV JAVA_HOME "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
RUN localedef -i en_US -f UTF-8 en_US.UTF-8

RUN useradd -m -s /bin/bash linuxbrew && \
    echo 'linuxbrew ALL=(ALL) NOPASSWD:ALL' >>/etc/sudoers

USER linuxbrew
RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install.sh)"

USER root
ENV PATH="/home/linuxbrew/.linuxbrew/bin:${PATH}"
CMD /usr/bin/appium