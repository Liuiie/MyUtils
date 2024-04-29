package com.liuiie.demo.utils.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 动态自动合并行列
 *
 * @author liuzijie
 * @since 2024/4/29 15:38
 */
public class CustomCellWriteHandler implements CellWriteHandler {
    /**
     * 开始合并的行
     */
    private final int mergeRowIndex;

    /**
     * 要合并的列
     */
    private final List<Integer> mergeColumnIndexes;

    /**
     * 要合并的行
     */
    private final List<Integer> mergeRowIndexes;

    public CustomCellWriteHandler(int mergeRowIndex, List<Integer> mergeColumnIndexes, List<Integer> mergeRowIndexes) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeColumnIndexes = mergeColumnIndexes;
        this.mergeRowIndexes = mergeRowIndexes;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        int curRowIndex = cell.getRowIndex();
        int curColIndex = cell.getColumnIndex();
        if (curRowIndex > mergeRowIndex) {
            for (Integer columnIndex : mergeColumnIndexes) {
                if (curColIndex == columnIndex) {
                    this.mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
                    break;
                }
            }
            for (Integer rowIndex : mergeRowIndexes) {
                if (curColIndex == rowIndex) {
                    this.mergeWithPrevCol(writeSheetHolder, cell, curRowIndex, curColIndex);
                    break;
                }
            }
        }
    }

    /**
     * 当前单元格向左合并
     *
     * @param writeSheetHolder sheet保持对象
     * @param cell             当前单元格
     * @param curRowIndex      当前行
     * @param curColIndex      当前列
     */
    private void mergeWithPrevCol(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColIndex) {
        //获取当前行的当前列的数据和上一列的当前行行数据，通过上一列数据是否相同进行合并
        final Object curData = cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.getNumericCellValue();
        final Cell preCell = cell.getSheet().getRow(curRowIndex).getCell(curColIndex - 1);
        final Object preData = preCell.getCellType() == CellType.STRING ? preCell.getStringCellValue() : preCell.getNumericCellValue();

        // 比较当前行的单元格与上一列是否相同，相同合并当前单元格与上一列
        if (curData.equals(preData)) {
            final Sheet sheet = writeSheetHolder.getSheet();
            final List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
            boolean isMerged = false;
            for (int i = 0; i < mergeRegions.size() && !isMerged; i++) {
                final CellRangeAddress cellRangeAddr = mergeRegions.get(i);
                // 若上一个单元格已经被合并，则先移出原有的合并单元，再重新添加合并单元
                if (cellRangeAddr.isInRange(curRowIndex, curColIndex - 1)) {
                    sheet.removeMergedRegion(i);
                    cellRangeAddr.setLastColumn(curColIndex);
                    sheet.addMergedRegion(cellRangeAddr);
                    isMerged = true;
                }
            }
            // 若上一个单元格未被合并，则新增合并单元
            if (!isMerged) {
                final CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex, curRowIndex, curColIndex - 1, curColIndex);
                sheet.addMergedRegion(cellRangeAddress);
            }
        }
    }

    /**
     * 当前单元格向上合并
     *
     * @param writeSheetHolder sheet保持对象
     * @param cell             当前单元格
     * @param curRowIndex      当前行
     * @param curColIndex      当前列
     */
    private void mergeWithPrevRow(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColIndex) {
        // 获取当前行的当前列的数据和上一行的当前列列数据，通过上一行数据是否相同进行合并
        Object curData = cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.getNumericCellValue();
        Row prevRow = cell.getSheet().getRow(curRowIndex - 1);
        if (prevRow != null) {
            Cell prevCell = prevRow.getCell(curColIndex);
            Object prevData = prevCell.getCellType() == CellType.STRING ? prevCell.getStringCellValue() : prevCell.getNumericCellValue();
            boolean isDataSame = curData.equals(prevData);
            // 比较当前行的第一列的单元格与上一行是否相同，相同合并当前单元格与上一行
            if (isDataSame) {
                Sheet sheet = writeSheetHolder.getSheet();
                List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
                boolean isMerged = false;
                for (int i = 0; i < mergeRegions.size(); i++) {
                    CellRangeAddress cellRangeAddress = mergeRegions.get(i);
                    // 若上一个单元格已经被合并，则先移出原有的合并单元，再重新添加合并单元
                    if (cellRangeAddress.isInRange(curRowIndex - 1, curColIndex)) {
                        sheet.removeMergedRegion(i);
                        cellRangeAddress.setLastRow(curRowIndex);
                        sheet.addMergedRegionUnsafe(cellRangeAddress);
                        isMerged = true;
                    }
                }
                // 若上一个单元格未被合并，则新增合并单元
                if (!isMerged) {
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex - 1, curRowIndex, curColIndex, curColIndex);
                    sheet.addMergedRegion(cellRangeAddress);
                }
            }
        }
    }
}