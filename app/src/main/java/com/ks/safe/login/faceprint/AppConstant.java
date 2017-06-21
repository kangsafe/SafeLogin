package com.ks.safe.login.faceprint;

/**
 * Created by Admin on 2017/6/21 0021 18:04.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class AppConstant {

    //WHAT 0-10 预留值
    public interface WHAT {
        int SUCCESS = 0;
        int FAILURE = 1;
        int ERROR = 2;
    }

    public interface KEY{
        String IMG_PATH = "IMG_PATH";
        String VIDEO_PATH = "VIDEO_PATH";
        String PIC_WIDTH = "PIC_WIDTH";
        String PIC_HEIGHT = "PIC_HEIGHT";
    }

    public interface REQUEST_CODE {
        int CAMERA = 0;
    }

    public interface RESULT_CODE {
        int RESULT_OK = -1;
        int RESULT_CANCELED = 0;
        int RESULT_ERROR = 1;
    }

}