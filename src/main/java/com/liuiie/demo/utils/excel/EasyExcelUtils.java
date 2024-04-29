package com.liuiie.demo.utils.excel;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * EasyExcelUtils
 *
 * @author liuzijie
 * @since 2024/4/29 15:55
 */
public class EasyExcelUtils {
    /**
     * @param response     返回
     * @param fileName     文件名
     * @param sheetNames   sheet集合
     * @param headerLists  表头集合
     * @param dataLists    数据集合
     * @param writeHandler 自定义样式
     */
    public static void writeExcelSheets(HttpServletResponse response, String fileName, List<String> sheetNames,
                                        List<List<List<String>>> headerLists, List<List<List<Object>>> dataLists, WriteHandler writeHandler) {
        ServletOutputStream out = null;
        try {
            out = getOut(response, fileName);
            sheetNames = sheetNames.stream().distinct().collect(Collectors.toList());
            int num = sheetNames.size();

            // 设置基础样式
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(getHeadStyle(), getContentStyle());
            ExcelWriter excelWriter = EasyExcel.write(out).build();
            for (int i = 0; i < num; i++) {
                ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet(i, sheetNames.get(i)).head(headerLists.get(i))
                        // 自动列宽（按实际情况加入）
                        .registerWriteHandler(new CustomHandler())
                        .registerWriteHandler(horizontalCellStyleStrategy);
                if (Objects.nonNull(writeHandler)) {
                    // 自定义样式设置
                    sheetBuilder.registerWriteHandler(writeHandler);
                }
                WriteSheet writeSheet = sheetBuilder.build();
                if (CollectionUtil.isEmpty(dataLists)) {
                    excelWriter.write(new ArrayList<>(), writeSheet);
                } else {
                    excelWriter.write(dataLists.get(i), writeSheet);
                }
            }
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ServletOutputStream getOut(HttpServletResponse response, String fileName) throws Exception {
        fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ExcelTypeEnum.XLSX.getValue();
        setAttachmentResponseHeader(response, fileName);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        return response.getOutputStream();
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);
        String contentDispositionValue = "attachment; filename=" + percentEncodedFileName + ";filename*=utf-8''" + percentEncodedFileName;
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", contentDispositionValue);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s  需要百分号编码的字符串
     * @return   百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 头部样式
     */
    private static WriteCellStyle getHeadStyle() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

        // 字体
        WriteFont headWriteFont = new WriteFont();
        //设置字体名字
        headWriteFont.setFontName("微软雅黑");
        //设置字体大小
        headWriteFont.setFontHeightInPoints((short) 10);
        //字体加粗
        headWriteFont.setBold(false);
        //在样式用应用设置的字体
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 边框样式
        setBorderStyle(headWriteCellStyle);

        //设置自动换行
        headWriteCellStyle.setWrapped(true);

        //设置水平对齐的样式为居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置文本收缩至合适
        //        headWriteCellStyle.setShrinkToFit(true);

        return headWriteCellStyle;
    }

    /**
     * 内容样式
     */
    private static WriteCellStyle getContentStyle() {
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();

        // 背景白色
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

        // 设置字体
        WriteFont contentWriteFont = new WriteFont();
        //设置字体大小
        contentWriteFont.setFontHeightInPoints((short) 10);
        //设置字体名字
        contentWriteFont.setFontName("宋体");
        //在样式用应用设置的字体
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        //设置样式
        setBorderStyle(contentWriteCellStyle);

        // 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置自动换行
        contentWriteCellStyle.setWrapped(true);

        //设置单元格格式是：文本格式，方式长数字文本科学计数法
//        contentWriteCellStyle.setDataFormatData();

        //设置文本收缩至合适
        // contentWriteCellStyle.setShrinkToFit(true);

        return contentWriteCellStyle;
    }

    /**
     * 边框样式
     */
    private static void setBorderStyle(WriteCellStyle cellStyle) {
        //设置底边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色
        cellStyle.setBottomBorderColor(IndexedColors.BLACK1.getIndex());
        //设置左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色
        cellStyle.setLeftBorderColor(IndexedColors.BLACK1.getIndex());
        //设置右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色
        cellStyle.setRightBorderColor(IndexedColors.BLACK1.getIndex());
        //设置顶边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色
        cellStyle.setTopBorderColor(IndexedColors.BLACK1.getIndex());
    }
}
