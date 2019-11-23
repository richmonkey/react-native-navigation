require 'json'
package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name             = "ReactNativeNavigation"
  s.version             = package['version']
  s.summary             = package['description']  
  s.homepage            = package['homepage']
  s.license             = package['license']  
  s.author              = package['author']  
  s.source              = { :git => 'https://github.com/richmonkey/react-native-navigation.git' }
  s.default_subspec     = 'Core'  
  s.platform         = :ios, '8.0'
  s.requires_arc     = true

  s.subspec 'Core' do |ss|
    ss.source_files     = "ios/**/*.{h,m}"
    ss.dependency 'React'
  end  
end
