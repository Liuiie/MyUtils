package com.liuiie.demo.utils.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * 单元格限制只能输入数字
 *
 * @author liuzijie
 * @since 2024/4/29 15:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataValidationHandler implements SheetWriteHandler {
    /**
     * 设置数据格式限制的起始行，默认为第二行
     */
    private int firstRow = 1;

    /**
     * 设置数据格式限制的结束行，默认为最后一行
     */
     private int lastRow = 0x10000;

    /**
     * 设置数据格式限制的起始列、默认为第二列
     */
    private int firstCol = 1;

     /**
     * 设置数据格式限制的结束列、默认为最后一列
     */
    private int lastCol = 0x10000;

    public DataValidationHandler(int firstCol, int lastCol) {
        this.firstCol = firstCol;
        this.lastCol = lastCol;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    /**
     *
     * 设置下拉列表的值
     *      DataValidationConstraint constraint = helper.createExplicitListConstraint(v.getSource());
     * 设置约束
     *      DataValidation validation = helper.createValidation(constraint, rangeList);
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 设置数据格式限制单元格的位置首行 末行 首列 末列
        CellRangeAddressList rangeList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        // DDD表示只能输入数字
        // DataValidationConstraint constraint = helper.createCustomConstraint("DDD")
        DataValidationConstraint constraint = helper.createNumericConstraint(
                DataValidationConstraint.ValidationType.DECIMAL,
                DataValidationConstraint.OperatorType.BETWEEN,
                "0", "99999999"
        );
        // 设置错误提示
        DataValidation validation = helper.createValidation(constraint, rangeList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("提示", "只能输入数字");
        sheet.addValidationData(validation);
    }
}
