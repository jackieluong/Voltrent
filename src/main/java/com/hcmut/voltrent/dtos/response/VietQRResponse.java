package com.hcmut.voltrent.dtos.response;

import lombok.ToString;

@ToString
public class VietQRResponse {

    private String code;
    private String desc;
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {
        private long acpId;
        private String accountName;
        private String qrCode;
        private String qrDataURL;

        // Getters and Setters
        public long getAcpId() {
            return acpId;
        }

        public void setAcpId(long acpId) {
            this.acpId = acpId;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getQrDataURL() {
            return qrDataURL;
        }

        public void setQrDataURL(String qrDataURL) {
            this.qrDataURL = qrDataURL;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "acpId=" + acpId +
                    ", accountName='" + accountName + '\'' +
                    ", qrCode='" + qrCode + '\'' +
                    ", qrDataURL='" + (qrDataURL.length() > 300 ? qrDataURL.substring(0, 300) + "..." : qrDataURL )+ '\'' +
                    '}';
        }
    }
}
