package com.mouzhai.excelreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mouzhai.excelreader.model.Password;
import com.mouzhai.excelreader.utils.ExcelHelper;
import com.mouzhai.excelreader.utils.PasswordDao;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ImageView ivQrCode;
    private EditText etQuery;
    private TextView tvContent;
    private ProgressBar pbReading;
    private ExcelHelper excelHelper;
    private PasswordDao passwordDao;

    private static Password password;
    private static String content;

    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static String EXCEL_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "已申请文件读取权限", Toast.LENGTH_SHORT).show();
        } else {
            //运行时申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ_EXTERNAL_STORAGE);
        }
        passwordDao = PasswordDao.getInstance(MainActivity.this);
        excelHelper = ExcelHelper.getInstance(MainActivity.this);
    }

    private void initView() {
        ivQrCode = (ImageView) findViewById(R.id.iv_qr_code);
        etQuery = (EditText) findViewById(R.id.et_query);
        tvContent = (TextView) findViewById(R.id.tv_content);
        pbReading = (ProgressBar) findViewById(R.id.pb_reading);
        pbReading.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "读取权限申请成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "没有文件读取权限！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 生成二维码
     */
    public void createQrCode(View view) {
        String content = tvContent.getText().toString();

        Bitmap bitmap;
        BitMatrix matrix;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            matrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择需要读取的 Excel 文件
     */
    public void chooseExcel(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            //通过Uri获取真实路径
            EXCEL_PATH = getRealFilePath(this, data.getData());
            if (EXCEL_PATH.contains(".xls") || EXCEL_PATH.contains(".xlsx")) {
                new ReadTask().execute();
            } else {
                Toast.makeText(this, "此文件不是excel格式", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 根据Uri获取真实路径
     * <p/>
     * 一个android文件的Uri地址一般如下：
     * content://media/external/images/media/62026
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据 SN 号查询完整的数据
     */
    @SuppressLint("SetTextI18n")
    public void queryPassword(View view) {
        String sn = etQuery.getText().toString();
        if (!sn.equals("")) {
            List<Password> passwords = passwordDao.queryPasswordBySn(sn);
            if (passwords.size() > 0) {
                password = passwords.get(0);
                content = "{sn:" + password.getSn() +
                        ",pass:" + password.getPass() +
                        ",mac:" + password.getMac() +
                        ",pno:" + password.getPno() +
                        ",encryption:" + password.getEncryption() +
                        ",date:" + password.getDate() +
                        ",description:" + password.getDescription() +
                        ",key:" + password.getKey() + "}";
                tvContent.setText(content);
            } else {
                tvContent.setText("NO CONTENT!");
            }
        }
    }

    class ReadTask extends AsyncTask<Void, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            pbReading.setVisibility(View.VISIBLE);

            Toast.makeText(MainActivity.this, "正在加载Excel中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            excelHelper.readExcelContentToDb(EXCEL_PATH);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pbReading.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "数据库加载完成", Toast.LENGTH_SHORT).show();
        }
    }
}
