
# DorsaPayment   

[![](https://jitpack.io/v/DorsaFamily/DorsaPayment.svg)](https://jitpack.io/#DorsaFamily/DorsaPayment)

**IMPORTANT**

If used lower version of 2.5, first remove paymentSdk from your project.

[Learn remove paymentSdk from Project(Video)](http://2rsa.ir/sdk/Remove-old-library.mp4)

**Add library to Project**

Follow below steps to add **DorsaPayment** to your app :

[Learn add paymentSdk (Video)](http://2rsa.ir/sdk/Add-library.mp4)

  1. Download below files and put them in libs folder (your project/app/libs) if libs folder not exist, make it:
   - [inAppPurchase](http://2rsa.ir/sdk/inAppPurchase.aar)
   - [inAppSDK](http://2rsa.ir/sdk/inAppSDK.aar)

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
			        implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
                      implementation 'com.github.DorsaFamily:DorsaPayment:3.3.1'
                            }
      ```

Now you can use Function to start, check or cancel subscription (Check sample for more details).                     
