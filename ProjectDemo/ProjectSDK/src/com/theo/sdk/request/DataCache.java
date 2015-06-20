package com.theo.sdk.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.theo.sdk.constant.Const;
import com.theo.sdk.utils.AppUtils;

import android.content.res.AssetManager;
import android.text.TextUtils;


/**
 * 数据缓存
 * 
 * @author Theo
 */
public class DataCache {

    /** DEBUG */
    public static final boolean DEBUG = true & Const.DEBUG;
    /** TAG */
    public static final String TAG = DataCache.class.getSimpleName();
    
    /** cache文件唯一id */
     String mId;
    /** cache文件 */
    File mFile;
    /** Assets */
    private AssetManager mAssets;

    /**
     * 构造
     * @param id id
     * @param dir dir
     */
    public DataCache(String id, File dir) {
        mId = id;
        mFile = new File(dir, id);
    }

    /**
     * 构造
     * @param id id
     * @param dir dir
     * @param asset AssetManager
     */
    public DataCache(String id, File dir, AssetManager asset) {
        mId = id;
        mFile = new File(dir, id);
        mAssets = asset;
    }

    /**
     * 保存
     * @param data data
     * @return 成功
     */
    public boolean save(String data) {
        if (mFile != null) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(mFile);
                fileWriter.write(data);
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否存在
     * @return 是否存在
     */
    public boolean exist() {
        return mFile.exists() || checkCacheInAssets();
    }
    
    /**
     * 检查缓存文件是否在assets中存在
     * @return 是否存在
     */
    private boolean checkCacheInAssets() {
        boolean isExist = false;
        try {
            if (mAssets != null && !TextUtils.isEmpty(mId)) {
                String[] files = mAssets.list("");
                for (String fileName : files) {
                    if (mId.equals(fileName)) {
                        isExist = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        
        return isExist;
    }

    /**
     * 加载
     * @return 数据
     */
    public String load() {
        BufferedReader br = null;
        String ret = null;
        if (mFile != null) {
            try {
                br = new BufferedReader(new FileReader(mFile));
                String line = null;
                StringBuffer sb = new StringBuffer((int) mFile.length());
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                ret = sb.toString();
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        if (DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        //如果从能在读取失败，则尝试从assets中去读取
        if (ret == null && mAssets != null) {
            InputStream in = null;
            try {
                in = mAssets.open(mId);
                ret = AppUtils.recieveData(in);
                in.close();
                in = null;
            } catch (Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        if (DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return ret;
    }
}
