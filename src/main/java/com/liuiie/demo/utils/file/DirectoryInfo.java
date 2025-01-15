package com.liuiie.demo.utils.file;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录信息
 *
 * @author Liuiie
 * @since 2025/1/15 15:42
 */
@Data
public class DirectoryInfo extends BaseFile {
    private Integer fileNum = 0;
    private Integer directoryNum = 0;
    private List<BaseFile> children = new ArrayList<>();

    public DirectoryInfo(String name, String path) {
        this.setType("Directory");
        this.setName(name);
        this.setPath(path);
    }

    /**
     * 统计文件数量
     */
    public void countFile() {
        fileNum += 1;
    }

    /**
     * 统计目录数量
     */
    public void countDir() {
        directoryNum += 1;
    }

    /**
     * 添加子目录/文件
     *
     * @param child 子目录/文件
     */
    public void addChild(BaseFile child) {
        children.add(child);
    }

    /**
     * 将文件夹下的文件转为集合
     *      递归收集子目录下所有文件
     *
     * @return 全量文件信息集合
     */
    public List<FileInfo> collectFileInfo() {
        List<FileInfo> fileInfos = new ArrayList<>();
        if (ObjectUtils.isEmpty(children)) {
            return fileInfos;
        }
        for (BaseFile child : children) {
            if (child instanceof FileInfo) {
                fileInfos.add((FileInfo) child);
            } else if (child instanceof DirectoryInfo) {
                DirectoryInfo dir = (DirectoryInfo) child;
                List<FileInfo> childFileInfos = dir.collectFileInfo();
                if (!ObjectUtils.isEmpty(childFileInfos)) {
                    fileInfos.addAll(childFileInfos);
                }
            }
        }
        return fileInfos;
    }
}
