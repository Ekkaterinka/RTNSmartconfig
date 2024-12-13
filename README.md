# for ios

cd light_zont
yarn add ../RTNSmartconfig
cd ..
node light_zont/node_modules/react-native/scripts/generate-codegen-artifacts.js \
  --targetPlatform ios \
  --path light_zont/ \
  --outputPath RTNSmartconfig/generated/


# for android
cd light_zont
yarn add ../RTNSmartconfig
cd android
./gradlew generateCodegenArtifactsFromSchema
