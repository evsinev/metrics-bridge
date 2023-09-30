#export NATIVE_IMAGE_USER_HOME=META-INF/native-image

native-image \
  --verbose \
  --allow-incomplete-classpath \
  --no-fallback  \
  -H:ConfigurationFileDirectories=META-INF/native-image \
  --enable-url-protocols=http,https \
  -jar ../metrics-agent/target/metrics-agent-1.0-SNAPSHOT.jar

