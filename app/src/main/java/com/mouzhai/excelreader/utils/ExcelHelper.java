package com.mouzhai.excelreader.utils;

import android.content.Context;

import com.mouzhai.excelreader.model.Password;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 从 Excel 文件中读取数据
 * <p>
 * Created by Mouzhai on 2017/2/20.
 */

public class ExcelHelper {

    private static Workbook workbook;
    private static Sheet sheet;
    private static Row row;
    private static PasswordDao passwordDao;
    private static ExcelHelper instance;

    private Context context;

    private ExcelHelper(Context context) {
        this.context = context;
    }

    public static ExcelHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ExcelHelper(context);
        }
        return instance;
    }

    /**
     * 读取 Excel 文件内容
     */
    public void readExcelContentToDb(String filePath) {
        InputStream is;
        try {
            is = new FileInputStream(filePath);
            String postfix = filePath.substring(filePath.lastIndexOf("."), filePath.length());
            if (postfix.equals(".xls")) {
                workbook = new HSSFWorkbook(new POIFSFileSystem(is));
            } else {
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = workbook.getSheetAt(0);
        int rowNumber = sheet.getLastRowNum();//行数
        //正文从第二行开始，第一行为标题行
        for (int i = 1; i < rowNumber; i++) {
            row = sheet.getRow(i);//获得第 i 行对象

            String sn = row.getCell(0).getStringCellValue();
            int pass = (int) row.getCell(1).getNumericCellValue();
            String mac = row.getCell(2).getStringCellValue();
            String pno = row.getCell(3).getStringCellValue();
            String encryption = row.getCell(4).getStringCellValue();
            String date = row.getCell(5).getStringCellValue();
            int description = (int) row.getCell(6).getNumericCellValue();
            int key = (int) row.getCell(7).getNumericCellValue();

            Password password = new Password(sn, pass, mac, pno, encryption, date, description, key);
            passwordDao = PasswordDao.getInstance(context);
            passwordDao.insertPassword(password);
        }
    }
}
