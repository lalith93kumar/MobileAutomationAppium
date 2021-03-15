FROM ubuntu
LABEL maintainer "codecaigicungduoc@gmail"
WORKDIR /app
SHELL ["/bin/bash", "-c"]
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y openjdk-8-jdk vim curl git unzip libglu1 libpulse-dev libasound2 libc6 \
libstdc++6 libx11-6 libx11-xcb1 libxcb1 libxcomposite1 libxcursor1 libxi6  libxtst6 libnss3 wget ca-certificates zip \
libqt5webkit5 libgconf-2-4 xvfb gnupg nodejs npm && rm -rf /var/lib/apt/lists/*
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
ARG APPIUM_VERSION=1.17.0
ENV APPIUM_VERSION=$APPIUM_VERSION
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash
RUN npm install -g appium@$APPIUM_VERSION --unsafe-perm=true --allow-root && \
    exit 0
ENV JAVA_HOME "/usr/lib/jvm/java-1.8.0-openjdk-amd64"
RUN wget 'http://search.maven.org/remotecontent?filepath=org/mock-server/mockserver-netty/5.11.2/mockserver-netty-5.11.2-jar-with-dependencies.jar' -O mockserver-netty-jar-with-dependencies.jar
RUN wget 'http://search.maven.org/remotecontent?filepath=org/mock-server/mockserver-netty/5.11.2/mockserver-netty-5.11.2-jar-with-dependencies.jar' -O mockserver-netty-jar-with-dependencies.jar
RUN nohup bash -c "java -Dmockserver.dynamicallyCreateCertificateAuthorityCertificate=true -Dmockserver.directoryToSaveDynamicSSLCertificate=. -Dfile.encoding=UTF-8 -jar mockserver-netty-jar-with-dependencies.jar -serverPort 1080 -logLevel INFO &" && \
sleep 10s && \
curl -k --proxy http://127.0.0.1:1080 https://www.youtube.com
CMD /usr/bin/appium