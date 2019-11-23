
Pod::Spec.new do |s|
  s.name             = "ReactNativeNavigation"
  s.version          = "0.1.0"
  s.summary          = "React Native Navigation"
  s.homepage         = "https://github.com/richmonkey/react-native-navigation:readme"
  s.license          = 'MIT'
  s.author           = { "houxh" => "houxuehua49@gmail.com" }
  s.source           = { :git => 'https://github.com/richmonkey/react-native-navigation.git' }
  s.default_subspec     = 'Core'  
  s.platform         = :ios, '8.0'
  s.requires_arc     = true


  s.subspec 'Core' do |ss|
    ss.source_files     = "ios/**/*.{h,m}"
    ss.dependency 'React'
  end  
end
