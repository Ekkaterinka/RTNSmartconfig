cd light_zont
yarn add ../RTNCalculator
cd ..
node light_zont/node_modules/react-native/scripts/generate-codegen-artifacts.js \
  --targetPlatform ios \
  --path light_zont/ \
  --outputPath RTNSmartconfig/generated/
