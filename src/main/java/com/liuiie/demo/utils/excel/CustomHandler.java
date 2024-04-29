package com.liuiie.demo.utils.excel;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表头自适应行宽高
 *
 * @author liuzijie
 * @since 2024/4/29 15:55
 */
public class CustomHandler extends AbstractColumnWidthStyleStrategy {
    /**
     * 最大列宽
     */
    private static final int MAX_COLUMN_WIDTH = 255;
    private static final Map<Integer, Map<Integer, Integer>> CACHE = new HashMap<>(8);

    public CustomHandler() {
    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !CollectionUtil.isEmpty(cellDataList);
        if (isHead) {
            // 如果不是最后一个表头，则不改变列宽
            List<String> headNameList = head.getHeadNameList();
            if (CollectionUtil.isNotEmpty(headNameList)) {
                int size = headNameList.size();
                if (!cell.getStringCellValue().equals(headNameList.get(size - 1))) {
                    return;
                }
            }
        }
        if (needSetWidth) {
            Map<Integer, Integer> maxColumnWidthMap = CACHE.computeIfAbsent(writeSheetHolder.getSheetNo(), k -> new HashMap<>(16));
            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
            if (columnWidth >= 0) {
                if (columnWidth > MAX_COLUMN_WIDTH) {
                    columnWidth = MAX_COLUMN_WIDTH;
                }
                Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
            }
        }
    }

    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        } else {
            WriteCellData<?> cellData = cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return -1;
            } else {
                switch (type) {
                    case STRING:
                        return cellData.getStringValue().getBytes().length;
                    case BOOLEAN:
                        return cellData.getBooleanValue().toString().getBytes().length;
                    case NUMBER:
                        return cellData.getNumberValue().toString().getBytes().length;
                    case DATE:
                        return cellData.getDateValue().toString().getBytes().length;
                    default:
                        return -1;
                }
            }
        }
    }
}