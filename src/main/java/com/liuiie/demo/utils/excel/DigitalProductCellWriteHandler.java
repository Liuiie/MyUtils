package com.liuiie.demo.utils.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class DigitalProductCellWriteHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList,
                                 Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        String sheetName = cell.getSheet().getSheetName();
        if (rowIndex == 1 && columnIndex == 0) {
            // 设置表头统计的样式，由于单元格是合并的，只设置第一列就行了
            setTotalStyle(writeSheetHolder, cellDataList, cell);
            return;
        }
        if (!isHead) {
            // 内容样式处理
            Workbook workbook = cell.getSheet().getWorkbook();
            if (sheetName.equals("手机")) {
                // 判断是否为”手机“sheet
                if (columnIndex == 3) {
                    // 判断价格
                    // 注意，这里的 cell.get**Value 有多个方法，一定要准确，否则会报错，报错后不会再进入这个拦截器，直接导出了
                    // 如果无法准确判断应该用哪个 getValue，可以 debug 测试
                    double value = cell.getNumericCellValue();
                    if (value > 5000.00) {
                        // 字体样式改为红色
                        CellStyle cellStyle = getContentCellStyle(workbook, IndexedColors.WHITE.getIndex(), IndexedColors.RED.getIndex());
                        //设置当前单元格样式
                        cell.setCellStyle(cellStyle);
                        // 这里要把 WriteCellData的样式清空
                        // 不然后面还有一个拦截器 FillStyleCellWriteHandler 默认会将 WriteCellStyle 设置到cell里面去 会导致自己设置的不一样
                        cellDataList.get(0).setWriteCellStyle(null);
                    }
                } else if (columnIndex == 4) {
                    // 判断CPU
                    String value = cell.getStringCellValue();
                    if (value.contains("骁龙8 Gen3")) {
                        // 背景改为黄色
                        CellStyle cellStyle = getContentCellStyle(workbook, IndexedColors.YELLOW.getIndex(), IndexedColors.BLACK.getIndex());
                        cell.setCellStyle(cellStyle);
                        cellDataList.get(0).setWriteCellStyle(null);
                    }
                }
            } else {
                // ”电脑“sheet
                if (columnIndex == 2) {
                    // 判断价格
                    double value = cell.getNumericCellValue();
                    if (value > 10000.00) {
                        // 字体样式改为红色
                        CellStyle cellStyle = getContentCellStyle(workbook, IndexedColors.WHITE.getIndex(), IndexedColors.RED.getIndex());
                        cell.setCellStyle(cellStyle);
                        cellDataList.get(0).setWriteCellStyle(null);
                    }
                } else if (columnIndex == 4) {
                    String value = cell.getStringCellValue();
                    if (value.contains("RTX 4090")) {
                        // 背景改为蓝色
                        CellStyle cellStyle = getContentCellStyle(workbook, IndexedColors.LIGHT_BLUE.getIndex(), IndexedColors.BLACK.getIndex());
                        cell.setCellStyle(cellStyle);
                        cellDataList.get(0).setWriteCellStyle(null);
                    }
                }
            }
        }
    }

    private CellStyle getContentCellStyle(Workbook workbook, short ffColorIndex, short fontColorIndex) {
        // 单元格策略
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置背景颜色
        cellStyle.setFillForegroundColor(ffColorIndex);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置垂直居中为居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置左右对齐为居中对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 自动换行
        cellStyle.setWrapText(true);
        // 设置边框
        setBorderStyle(cellStyle);
        // 字体
        Font font = workbook.createFont();
        //设置字体名字
        font.setFontName("宋体");
        //设置字体大小
        font.setFontHeightInPoints((short) 10);
        // 设置字体颜色
        font.setColor(fontColorIndex);
        //字体加粗
        font.setBold(false);
        //在样式用应用设置的字体
        cellStyle.setFont(font);
        return cellStyle;
    }

    private void setTotalStyle(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        //设置行高
        writeSheetHolder.getSheet().getRow(cell.getRowIndex()).setHeight((short) (1.4 * 256 * 2));
        // 单元格策略
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置背景颜色灰色
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置垂直居中为居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置左右对齐为靠右对齐
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        // 自动换行
        cellStyle.setWrapText(true);
        // 设置边框
        setBorderStyle(cellStyle);
        // 字体
        Font font = workbook.createFont();
        //设置字体名字
        font.setFontName("微软雅黑");
        //设置字体大小
        font.setFontHeightInPoints((short) 10);
        //字体加粗
        font.setBold(false);
        //在样式用应用设置的字体
        cellStyle.setFont(font);
        //设置当前单元格样式
        cell.setCellStyle(cellStyle);
        // 这里要把 WriteCellData的样式清空
        // 不然后面还有一个拦截器 FillStyleCellWriteHandler 默认会将 WriteCellStyle 设置到cell里面去 会导致自己设置的不一样
        cellDataList.get(0).setWriteCellStyle(null);
    }

    /**
     * 边框样式
     */
    private static void setBorderStyle(CellStyle cellStyle) {
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
