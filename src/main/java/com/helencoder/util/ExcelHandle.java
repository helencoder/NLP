package com.helencoder.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Excel文件创建类
 *
 * Created by helencoder on 2017/7/3.
 */
public class ExcelHandle {

    public static void main(String[] args) {
        // excel文件读写示例

        createExcel("record.xls", "helen");
    }


    /**
     * Excel文件创建
     *
     * @param filename 创建的excel文件名称
     * @param sheetname 工作薄名称
     */
    public static void createExcel(String filename, String sheetname) {

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格(sheet)
        workbook.createSheet(sheetname);

        // 文件写入
        try {
            OutputStream out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Excel文件写入
     *
     * @param title 标题
     * @param headers 抬头
     * @param dataset 数据
     * @param outfile 写入的excel文件名
     */
    public static void writeExcel(String title, String[] headers, List<String> dataset, String outfile) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格(sheet)
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置样式
        HSSFCellStyle style = getStyle(workbook);

        // 产生表格标题行
        HSSFRow titleRow = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = titleRow.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }

        // 表格数据
        int index = 1;
        for (int i = 0; i < dataset.size(); i++) {
            HSSFRow dataRow = sheet.createRow(index);

            String[] data = dataset.get(i).split("\t");
            for (int j = 0; j < data.length; j++) {
                HSSFCell cell = dataRow.createCell(j);
                //cell.setCellStyle(style);

                // 添加数据
                cell.setCellValue(data[j]);

                // 添加超链接
                //cell.setCellFormula("HYPERLINK(\"" + "http://itchz.cmbchina.cn/"+ "\", \"" + data[j] + "\")");
                // 添加超链接样式
                //HSSFCellStyle linkStyle = getLinkStyle(workbook);
                //cell.setCellStyle(linkStyle);

            }
            index += 1;

        }

        // 文件写入
        try {
            OutputStream out = new FileOutputStream(outfile);
            workbook.write(out);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Excel文件追加
     */
    public static void appendExcel(List<String> data, String filename) {
        try {
            FileInputStream fs = new FileInputStream(filename);  //获取文件原始信息
            POIFSFileSystem ps = new POIFSFileSystem(fs);  //使用POI提供的方法得到excel的信息
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);  //获取到工作表，因为一个excel可能有多个工作表
            HSSFRow row = sheet.getRow(0);  //获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值
            System.out.println(sheet.getLastRowNum() + " " + row.getLastCellNum());  //分别得到最后一行的行号，和一条记录的最后一个单元格

            FileOutputStream out = new FileOutputStream(filename);  //向文件中重新写入数据
            row = sheet.createRow((short) (sheet.getLastRowNum() + 1)); //在现有行号后追加数据
            row.createCell(0).setCellValue("leilei"); //设置第一个（从0开始）单元格的数据
            row.createCell(1).setCellValue(24); //设置第二个（从0开始）单元格的数据

            // 进行数据添加(可参照文件写入)


            out.flush();
            wb.write(out);
            out.close();
            System.out.println(row.getPhysicalNumberOfCells() + " " + row.getLastCellNum());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Excel文件读取
     *
     */
    public static List<String> readExcel(String filename) {
        List<String> list = new ArrayList<String>();

        try {
            FileInputStream fs = new FileInputStream(filename);  //获取d://test.xls
            POIFSFileSystem ps = new POIFSFileSystem(fs);  //使用POI提供的方法得到excel的信息
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);  //获取到工作表，因为一个excel可能有多个工作表

            int rouNum = sheet.getLastRowNum();
            for (int i = 0; i < rouNum; i++) {
                HSSFRow row = sheet.getRow(i);
                int cellNum = row.getLastCellNum();
                for (int j = 0; j < cellNum; j++) {
                    HSSFCell cell = row.getCell(j);
                    System.out.println("当前cell为：" + cell);
                }
            }

            //System.out.println(sheet.getLastRowNum() + " " + row.getLastCellNum());  //分别得到最后一行的行号，和一条记录的最后一个单元格

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return list;
    }


    /**
     * Excel文件样式设置
     * 一般样式
     */
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 样式设置
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * Excel文件样式设置
     * 超链接样式
     */
    public static HSSFCellStyle getLinkStyle(HSSFWorkbook workbook) {
        // 添加超链接样式
        HSSFCellStyle linkStyle = workbook.createCellStyle();
        HSSFFont cellFont = workbook.createFont();
        cellFont.setUnderline((byte) 1);
        cellFont.setColor(HSSFColor.BLUE.index);
        linkStyle.setFont(cellFont);
        return linkStyle;
    }


    public static void exportExcel(String title, String[] headers, List<String> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet("test");
        // 设置表格默认列宽度为15个字节
        //sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("helen");

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
//        Iterator<T> it = dataset.iterator();
//        int index = 0;
//        while (it.hasNext()) {
//            index++;
//            row = sheet.createRow(index);
//            T t = (T) it.next();
//            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
//            Field[] fields = t.getClass().getDeclaredFields();
//            for (short i = 0; i < fields.length; i++) {
//                HSSFCell cell = row.createCell(i);
//                cell.setCellStyle(style2);
//                Field field = fields[i];
//                String fieldName = field.getName();
//                String getMethodName = "get"
//                        + fieldName.substring(0, 1).toUpperCase()
//                        + fieldName.substring(1);
//                try {
//                    Class tCls = t.getClass();
//                    Method getMethod = tCls.getMethod(getMethodName,
//                            new Class[] {});
//                    Object value = getMethod.invoke(t, new Object[] {});
//                    // 判断值的类型后进行强制类型转换
//                    String textValue = null;
//                    // if (value instanceof Integer) {
//                    // int intValue = (Integer) value;
//                    // cell.setCellValue(intValue);
//                    // } else if (value instanceof Float) {
//                    // float fValue = (Float) value;
//                    // textValue = new HSSFRichTextString(
//                    // String.valueOf(fValue));
//                    // cell.setCellValue(textValue);
//                    // } else if (value instanceof Double) {
//                    // double dValue = (Double) value;
//                    // textValue = new HSSFRichTextString(
//                    // String.valueOf(dValue));
//                    // cell.setCellValue(textValue);
//                    // } else if (value instanceof Long) {
//                    // long longValue = (Long) value;
//                    // cell.setCellValue(longValue);
//                    // }
//                    if (value instanceof Boolean) {
//                        boolean bValue = (Boolean) value;
//                        textValue = "男";
//                        if (!bValue) {
//                            textValue = "女";
//                        }
//                    } else if (value instanceof Date) {
//                        Date date = (Date) value;
//                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//                        textValue = sdf.format(date);
//                    } else if (value instanceof byte[]) {
//                        // 有图片时，设置行高为60px;
//                        row.setHeightInPoints(60);
//                        // 设置图片所在列宽度为80px,注意这里单位的一个换算
//                        sheet.setColumnWidth(i, (short) (35.7 * 80));
//                        // sheet.autoSizeColumn(i);
//                        byte[] bsValue = (byte[]) value;
//                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
//                                1023, 255, (short) 6, index, (short) 6, index);
//                        anchor.setAnchorType(2);
//                        patriarch.createPicture(anchor, workbook.addPicture(
//                                bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
//                    } else {
//                        // 其它数据类型都当作字符串简单处理
//                        textValue = value.toString();
//                    }
//                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
//                    if (textValue != null) {
//                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
//                        Matcher matcher = p.matcher(textValue);
//                        if (matcher.matches()) {
//                            // 是数字当作double处理
//                            cell.setCellValue(Double.parseDouble(textValue));
//                        } else {
//                            HSSFRichTextString richString = new HSSFRichTextString(
//                                    textValue);
//                            HSSFFont font3 = workbook.createFont();
//                            font3.setColor(HSSFColor.BLUE.index);
//                            richString.applyFont(font3);
//                            cell.setCellValue(richString);
//                        }
//                    }
//                } catch (SecurityException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IllegalArgumentException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } finally {
//                    // 清理资源
//                }
//            }
//
//        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

