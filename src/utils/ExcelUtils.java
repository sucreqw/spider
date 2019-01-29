package utils;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExcelUtils {

    public static void writeExcel(String filename, ArrayList<String> data){


        // 创建工作薄 xsl
        //HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建工作薄 xslx
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表 xsl
       // HSSFSheet sheet = workbook.createSheet("sheet1");
        // 创建工作表 xslx
        Sheet sheet=workbook.createSheet("sheet1");



       // Cell cell = rows.createCell(0);
        //循环行数
        for (int row = 0; row < data.size(); row++)
        {
            //创建一行数据
            //HSSFRow rows = sheet.createRow(row);
            Row rows = sheet.createRow(row);

            //
            String[] colume=data.get(row).split("\\|");
            //循环列数
            for (int col = 0; col < colume.length; col++)
            {
                // 向工作表中添加数据
                rows.createCell(col).setCellValue(colume[col]);
            }
        }
        try {
        File xlsFile = new File(filename);
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
    }catch (Exception e){
            System.out.println("写入excel出错！" + e.getMessage());
        }

    }
}
