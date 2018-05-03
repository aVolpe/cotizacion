./mvnw \
    org.codehaus.mojo:license-maven-plugin:1.16:add-third-party \
    -Dlicense.includeTransitiveDependencies=false \
    -Dlicense.verbose \
    -Dlicense.outputDirectory=src/main/resources/ \
    -Dlicense.thirdPartyFilename=licenses.json \
    -Dlicense.fileTemplate=./utils/mvn_license_template.ftl
