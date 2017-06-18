package bdkj.com.englishstu.base;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.view.CropImageView;

import org.greenrobot.greendao.database.Database;

import java.io.File;

import bdkj.com.englishstu.R;
import bdkj.com.englishstu.common.beans.Admin;
import bdkj.com.englishstu.common.beans.DaoMaster;
import bdkj.com.englishstu.common.beans.DaoSession;
import bdkj.com.englishstu.common.tool.SerializeUtils;

public class Application
        extends android.app.Application {

    private static Context mContext;
    private static Admin adminInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "english-db-encrypted" : "english-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        initImgPicker();
    }

    public void initImgPicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setMultiMode(false);//是否多选
//        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    public class GlideImageLoader
            implements ImageLoader {
        @Override
        public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号
                    // 无法识别和显示)
                    .error(R.mipmap.default_image)           //设置错误图片
                    .placeholder(R.mipmap.default_image)     //设置占位图片
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                    .into(imageView);
        }

        @Override
        public void clearMemoryCache() {
        }
    }

    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = true;

    private static DaoSession daoSession;


    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static boolean setAdminInfo(Context mContext, Admin admin) {
        adminInfo = new Admin();
        adminInfo = admin;
        return SerializeUtils.writeObject(new File(mContext.getFilesDir(), Constants.ADMIN_INFO_NAME), admin);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static Admin getAdminInfo() {
        if (adminInfo == null) {
            Admin admin = null;
            Object objUser = SerializeUtils.readObject(new File(mContext.getFilesDir(), Constants.ADMIN_INFO_NAME));
            if (objUser == null || !(objUser instanceof Admin)) {
                return null;
            }
            admin = (Admin) objUser;
            adminInfo = admin;
            return admin;
        } else {
            return adminInfo;
        }
    }
}