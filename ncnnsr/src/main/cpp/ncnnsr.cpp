#include <android/bitmap.h>
#include <jni.h>
#include <string>
#include <vector>

#include "log.h"

// ncnn
#include "MobileSR.id.h"
#include <unistd.h>
#include "net.h"

static ncnn::UnlockedPoolAllocator g_blob_pool_allocator;
static ncnn::PoolAllocator g_workspace_pool_allocator;

static ncnn::Mat ncnn_param;
static ncnn::Mat ncnn_bin;
static ncnn::Net ncnn_net;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_seiko_compose_ncnnsr_Ncnnsr_init(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray param,
        jbyteArray bin) {
    LOGD("init ncnn_net...");

    // init param
    {
        int len = env->GetArrayLength(param);
        ncnn_param.create(len, (size_t) 1u);
        env->GetByteArrayRegion(param, 0, len, (jbyte *) ncnn_param);
        int ret = ncnn_net.load_param((const unsigned char *) ncnn_param);
//        if (ret != 0) {
//            LOGW("load_param error %d %d", ret, len);
//            return JNI_FALSE;
//        }
    }

    // init bin
    {
        int len = env->GetArrayLength(bin);
        ncnn_bin.create(len, (size_t) 1u);
        env->GetByteArrayRegion(bin, 0, len, (jbyte *) ncnn_bin);
        int ret = ncnn_net.load_model((const unsigned char *) ncnn_bin);
//        if (ret != 0) {
//            LOGW("load_model error %d %d", ret, len);
//            return JNI_FALSE;
//        }
    }

    ncnn::Option opt;
    opt.lightmode = true;
    opt.num_threads = 8;   //线程 这里可以修改
    opt.blob_allocator = &g_blob_pool_allocator;
    opt.workspace_allocator = &g_workspace_pool_allocator;

    ncnn_net.opt = opt;

    return JNI_TRUE;
}

JNIEXPORT jfloatArray JNICALL
Java_com_seiko_compose_ncnnsr_Ncnnsr_detect(
        JNIEnv *env,
        jobject /* this */,
        jobject bitmap
) {

    // ncnn from bitmap
    ncnn::Mat in;
    {
        AndroidBitmapInfo info;
        AndroidBitmap_getInfo(env, bitmap, &info);
        if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
            LOGW("only support RGBA_8888 bitmap");
            return nullptr;
        }
        void *in_data;
        AndroidBitmap_lockPixels(env, bitmap, &in_data);
        in = ncnn::Mat::from_pixels((const unsigned char *) in_data,
                                    ncnn::Mat::PIXEL_RGBA2RGB,
                                    info.width, info.height);

        AndroidBitmap_unlockPixels(env, bitmap);
    }

    // ncnn_net
    std::vector<float> cls_scores;
    {
        // 减去均值和乘上比例（这个数据和前面的归一化图片预处理形式一一对应）
        const float mean_vals[3] = {127.5f, 127.5f, 127.5f};
        const float scale[3] = {0.007843f, 0.007843f, 0.007843f};

        in.substract_mean_normalize(mean_vals, scale);// 归一化

        ncnn::Extractor ex = ncnn_net.create_extractor();//前向传播

        // 如果不加密是使用ex.input("data", in);
        // BLOB_data在id.h文件中可见，相当于datainput网络层的id
        //ex.input("data", in);
        //ex.input(MobileNetSSD_deploy_param_id::BLOB_data, in);
        //ex.input(EDSRx2_param_id::BLOB_data, in);
        ex.input(MobileSR_param_id::BLOB_data, in);
        ex.set_num_threads(8); //和上面一样一个对象

        ncnn::Mat out;
        // 如果时不加密是使用ex.extract("prob", out);
        //BLOB_detection_out.h文件中可见，相当于dataout网络层的id,输出检测的结果数据
        //ex.extract("ConvNd_37", out);
        //ex.extract(MobileNetSSD_deploy_param_id::BLOB_detection_out, out);
        //ex.extract(EDSRx2_param_id::BLOB_ConvNd_37, out);
        ex.extract(MobileSR_param_id::BLOB_reconstruct, out);
        int output_wsize = out.w;
        // int output_hsize = out.h;


        //输出整理
        jfloatArray jOutputData = env->NewFloatArray(output_wsize);
        return jOutputData;
    }
}

}