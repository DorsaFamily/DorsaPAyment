
# DorsaPayment

**IMPORTANT**

If used lower version of 2.5, first remove paymentSdk from your project.

[Learn remove paymentSdk from Project](www.google.com)

**Add library to Project**

Follow below steps to add **DorsaPayment** to your app :

[Learn add paymentSdk (Video)](www.google.com)

  1. Download below files and put them in libs folder (your project/app/libs) if libs folder not exist, make it:
   - [inAppPurchase](https://github.com/DorsaFamily/DorsaPayment/raw/master/Payment/libs/inAppPurchase.aar)
   - [inAppSDK](https://github.com/DorsaFamily/DorsaPayment/raw/master/Payment/libs/inAppSDK.aar)

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
                      implementation 'com.github.DorsaFamily:DorsaPayment:v3.0'
                            }
      ```

Now you can use Function to start, check or cancel subscription (Check sample for more details).                     
