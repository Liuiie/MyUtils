package com.liuiie.demo.utils.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 自定义表格样式
 *
 * @author liuzijie
 * @since 2024/4/29 15:47
 */
public class CustomCellStyle  extends AbstractVerticalCellStyleStrategy {
    /**
     * 表格头样式
     */
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        // 头的策略  样式调整
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置头部样式
        this.setStyle(headWriteCellStyle, true, true);
        // 设置细边框
        this.setBorder(headWriteCellStyle);
        // 表头字体样式
        WriteFont headWriteFont = this.getHeadFont(IndexedColors.SKY_BLUE.getIndex());
        headWriteCellStyle.setWriteFont(headWriteFont);
        return headWriteCellStyle;
    }


    /**
     * 单元格格内样式
     */
    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        // 内容的策略
        WriteCellStyle contentStyle = new WriteCellStyle();
        // 设置内容样式
        this.setStyle(contentStyle, false, false);
        // 设置边框
        this.setBorder(contentStyle);
        // 内容字体
        WriteFont contentWriteFont = this.getContentFont();
        contentStyle.setWriteFont(contentWriteFont);
        return contentStyle;
    }

    /**
     * 获取表头字体
     *
     * @param color 颜色
     * @return 样式
     */
    private WriteFont getHeadFont(Short color){
        //表头字体样式
        WriteFont headWriteFont = new WriteFont();
        // 头字号
        headWriteFont.setFontHeightInPoints((short) 14);
        // 字体样式
        headWriteFont.setFontName("微软雅黑");
        // 字体颜色
        headWriteFont.setColor(color);
        // 字体加粗
        headWriteFont.setBold(true);
        return headWriteFont;
    }

    /**
     * 获取内容字体
     *
     * @return 字体
     */
    private WriteFont getContentFont(){
        //内容字体
        WriteFont contentWriteFont = new WriteFont();
        // 内容字号
        contentWriteFont.setFontHeightInPoints((short) 11);
        // 字体样式
        contentWriteFont.setFontName("宋体");
        return contentWriteFont;
    }

    /**
     * 设置样式
     *
     * @param cellStyle     单元格样式
     * @param wrappedFlag   自动换行标识，true:开启自动换行
     * @param centerFlag    水平居中开关，true:开启水平居中
     */
    private void setStyle(WriteCellStyle cellStyle, boolean wrappedFlag, boolean centerFlag){
        // 背景 白色
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        if(wrappedFlag){
            // 自动换行
            cellStyle.setWrapped(true);
        }
        if(centerFlag){
            // 水平对齐方式
            cellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        }
        // 垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     * 设置边框
     *
     * @param cellStyle 单元格样式
     */
    private void setBorder(WriteCellStyle cellStyle){
        // 设置细边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 设置边框颜色 25灰度
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

}

