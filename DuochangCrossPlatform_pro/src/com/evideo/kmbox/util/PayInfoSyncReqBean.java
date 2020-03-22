package com.evideo.kmbox.util;



import java.util.List;

/**
 * Created by PoPEyE on 2019/8/2.
 */

public class PayInfoSyncReqBean {

    private String Ctype;
    private String OrderId;
    private String PayNum;
    private String BizType;
    private String StbID;
    private String ChargePolicy;
    private String CustomBizExpiryDate;
    private String OperCode;

    @Override
    public String toString() {
        return "PayInfoSyncReqBean{" +
                "Ctype='" + Ctype + '\'' +
                ", OrderId='" + OrderId + '\'' +
                ", PayNum='" + PayNum + '\'' +
                ", BizType='" + BizType + '\'' +
                ", StbID='" + StbID + '\'' +
                ", ChargePolicy='" + ChargePolicy + '\'' +
                ", CustomBizExpiryDate='" + CustomBizExpiryDate + '\'' +
                ", OperCode='" + OperCode + '\'' +
                ", payInfoList=" + payInfoList +
                '}';
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getPayNum() {
        return PayNum;
    }

    public void setPayNum(String payNum) {
        PayNum = payNum;
    }

    public String getBizType() {
        return BizType;
    }

    public void setBizType(String bizType) {
        BizType = bizType;
    }

    public String getStbID() {
        return StbID;
    }

    public void setStbID(String stbID) {
        StbID = stbID;
    }

    public String getChargePolicy() {
        return ChargePolicy;
    }

    public void setChargePolicy(String chargePolicy) {
        ChargePolicy = chargePolicy;
    }

    public String getCustomBizExpiryDate() {
        return CustomBizExpiryDate;
    }

    public void setCustomBizExpiryDate(String customBizExpiryDate) {
        CustomBizExpiryDate = customBizExpiryDate;
    }

    public String getOperCode() {
        return OperCode;
    }

    public void setOperCode(String operCode) {
        OperCode = operCode;
    }

    public String getCtype() {
        return Ctype;
    }

    public void setCtype(String ctype) {
        Ctype = ctype;
    }

    public List<PayInfo> getPayInfoList() {
        return payInfoList;
    }

    public void setPayInfoList(List<PayInfo> payInfoList) {
        this.payInfoList = payInfoList;
    }

    private List<PayInfo> payInfoList;

    public static class PayInfo{
        private String Index;
        private String IsMonthly;
        private String CustomPeriod;
        private String CooperateCode;

        public String getCooperateCode() {
            return CooperateCode;
        }

        public void setCooperateCode(String cooperateCode) {
            CooperateCode = cooperateCode;
        }

        private String BillTimes;
        private String BillInterval;
        private String CampaignId;
        private String Fee;
        private String SpCode;
        private String ServCode;
        private String ChannelCode;
        private String ProductCode;
        private String ContentCode;
        private String PlatForm_Code;
        private String Cpparam;
        private String ReserveParam;

        @Override
        public String toString() {
            return "PayInfo{" +
                    "Index='" + Index + '\'' +
                    ", IsMonthly='" + IsMonthly + '\'' +
                    ", CustomPeriod='" + CustomPeriod + '\'' +
                    ", BillTimes='" + BillTimes + '\'' +
                    ", BillInterval='" + BillInterval + '\'' +
                    ", CampaignId='" + CampaignId + '\'' +
                    ", Fee='" + Fee + '\'' +
                    ", SpCode='" + SpCode + '\'' +
                    ", ServCode='" + ServCode + '\'' +
                    ", ChannelCode='" + ChannelCode + '\'' +
                    ", ProductCode='" + ProductCode + '\'' +
                    ", ContentCode='" + ContentCode + '\'' +
                    ", PlatForm_Code='" + PlatForm_Code + '\'' +
                    ", Cpparam='" + Cpparam + '\'' +
                    ", ReserveParam='" + ReserveParam + '\'' +
                    '}';
        }

        public String getIsMonthly() {
            return IsMonthly;
        }

        public void setIsMonthly(String isMonthly) {
            IsMonthly = isMonthly;
        }

        public String getCustomPeriod() {
            return CustomPeriod;
        }

        public void setCustomPeriod(String customPeriod) {
            CustomPeriod = customPeriod;
        }

        public String getBillTimes() {
            return BillTimes;
        }

        public void setBillTimes(String billTimes) {
            BillTimes = billTimes;
        }

        public String getBillInterval() {
            return BillInterval;
        }

        public void setBillInterval(String billInterval) {
            BillInterval = billInterval;
        }

        public String getCampaignId() {
            return CampaignId;
        }

        public void setCampaignId(String campaignId) {
            CampaignId = campaignId;
        }

        public String getFee() {
            return Fee;
        }

        public void setFee(String fee) {
            Fee = fee;
        }

        public String getSpCode() {
            return SpCode;
        }

        public void setSpCode(String spCode) {
            SpCode = spCode;
        }

        public String getServCode() {
            return ServCode;
        }

        public void setServCode(String servCode) {
            ServCode = servCode;
        }

        public String getChannelCode() {
            return ChannelCode;
        }

        public void setChannelCode(String channelCode) {
            ChannelCode = channelCode;
        }

        public String getProductCode() {
            return ProductCode;
        }

        public void setProductCode(String productCode) {
            ProductCode = productCode;
        }

        public String getContentCode() {
            return ContentCode;
        }

        public void setContentCode(String contentCode) {
            ContentCode = contentCode;
        }

        public String getPlatForm_Code() {
            return PlatForm_Code;
        }

        public void setPlatForm_Code(String platForm_Code) {
            PlatForm_Code = platForm_Code;
        }

        public String getCpparam() {
            return Cpparam;
        }

        public void setCpparam(String cpparam) {
            Cpparam = cpparam;
        }

        public String getReserveParam() {
            return ReserveParam;
        }

        public void setReserveParam(String reserveParam) {
            ReserveParam = reserveParam;
        }

        public String getIndex() {
            return Index;
        }

        public void setIndex(String index) {
            Index = index;
        }

    }
}
