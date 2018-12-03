
package ir.dorsa.totalpayment.registerInformation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsRegisterInformation {

    @SerializedName("DeviceName")
    @Expose
    private String deviceName;
    @SerializedName("DeviceModel")
    @Expose
    private String deviceModel;
    @SerializedName("AppName")
    @Expose
    private String appName;
    @SerializedName("PkgName")
    @Expose
    private String pkgName;
    @SerializedName("Varsion")
    @Expose
    private String varsion;
    @SerializedName("UniqueCode")
    @Expose
    private String uniqueCode;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("Operator")
    @Expose
    private String operator;
    @SerializedName("Version")
    @Expose
    private String version;
    @SerializedName("OSVersion")
    @Expose
    private String osVersion;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVarsion() {
        return varsion;
    }

    public void setVarsion(String varsion) {
        this.varsion = varsion;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
}
