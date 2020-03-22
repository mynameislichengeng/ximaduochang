
package com.evideo.kmbox.widget.mainview.singer;

/**
 * [歌星头像信息]
 */
public class SingerCoverInfo {
    /** [超大头像Id] */
    public String mCoverPicH;
    /** [大头像Id] */
    public String mCoverPicL;
    /** [中等头像Id] */
    public String mCoverPicM;
    /** [小头像Id] */
    public String mCoverPicS;
    /** [头文件路径] */
    public String mPicHeadUrl;
    public SingerCoverInfo() {
        
    }
    public SingerCoverInfo(String picHead, String coverPicH,
            String coverPicL, String coverPicM, String coverPicS) {
        this.mPicHeadUrl = picHead;
        this.mCoverPicH = coverPicH;
        this.mCoverPicL = coverPicL;
        this.mCoverPicM = coverPicM;
        this.mCoverPicS = coverPicS;
    }
}
