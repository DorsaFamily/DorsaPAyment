# DorsaPayment


Follow below steps to add DorsaPayment to your app :1. Download below files and add to your project

  1. Download below files and add to your project:
   - [inAppPurchase](https://github.com/DorsaFamily/DorsaPayment/blob/master/inAppPurchase/inAppPurchase.aar)
   - [inAppSDK](https://github.com/DorsaFamily/DorsaPayment/blob/master/inAppSDK/inAppSDK.aar)

  2. Add below codes to your root build.gradle at the end of repositories:
     ```gradle
     allprojects {
                    repositories {
                        maven { url "https://jitpack.io" }
                    }
                }
     ```
  3. Add the dependency :
      ```gradle
          dependencies {
                                implementation 'com.github.DorsaFamily:DorsaPayment:v2.0'
                            }
      ```
Now you can use Function to start, check or cancel subscription (Check sample for more details).                     
