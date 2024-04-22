package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author TMC
 * @version 1.0
 * @description 大文件分块与合并测试
 * @date 2023/2/11 19:22
 */
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        //1.待分块文件对象
        File sourceFile = new File("D:\\Download\\bigfiletest\\movie.rmvb");

        //2.文件分块参数
        File chunkFolderPath = new File("D:\\Download\\bigfiletest\\chunk\\"); //分块文件存储目录
        if (!chunkFolderPath.exists()) {
            chunkFolderPath.mkdirs();
        }
        int chunkSize = 1024 * 1024 * 1; //分块文件大小
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize); //分块文件数量

        //3.文件分块
        //  使用流对象读取源文件,向分块文件写数据,达到分块大小不再写
        try (RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r")) {
            //3.1.创建读写缓冲字节数组
            byte[] buffer = new byte[1024];
            for (long i = 0; i < chunkNum; i++) {
                //3.2.创建分块文件对象
                File file = new File("D:\\Download\\bigfiletest\\chunk\\" + i);
                if (file.exists()) {
                    file.delete(); //如果分块文件存在,则删除
                }
                if (!file.createNewFile()) {
                    System.out.println("创建分块文件" + file.getName() + "失败");
                    break;
                }
                //3.3.向分块文件写数据流对象
                try (RandomAccessFile raf_write = new RandomAccessFile(file, "rw")) {
                    int len = -1;
                    //读取源文件
                    while ((len = raf_read.read(buffer)) != -1) {
                        //向分块文件中写数据
                        raf_write.write(buffer, 0, len);
                        //达到分块大小停止
                        if (file.length() >= chunkSize) {
                            break;
                        }
                    }
                }
            }
        }
    }

    //测试合并
    @Test
    public void testMerge() throws IOException {
        //1.创建待分块文件对象
        File sourceFile = new File("D:\\Download\\bigfiletest\\movie.rmvb");

        //2.创建合并文件对象
        File mergeFile = new File("D:\\Download\\bigfiletest\\merge.rmvb");
        boolean newFile = mergeFile.createNewFile();
        if (!newFile) {
            System.out.println("创建合并文件失败");
            return;
        }

        //2.分块文件排序
        File chunkFolderPath = new File("D:\\Download\\bigfiletest\\chunk\\");
        if (!chunkFolderPath.exists()) {
            System.out.println("分块文件目录不存在");
            return;
        }
        File[] chunkFiles = chunkFolderPath.listFiles();
        List<File> chunkFileList = Arrays.asList(chunkFiles);
        Collections.sort(chunkFileList, Comparator.comparingInt(o -> Integer.parseInt(o.getName()))); //按文件名升序排序

        //3.合并分块文件
        try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw")) {
            //3.1.创建读写缓冲字节数组
            byte[] buffer = new byte[1024];
            for (File file : chunkFileList) {
                try (RandomAccessFile raf_read = new RandomAccessFile(file, "r")) {
                    int len = -1;
                    //3.2.读取分块文件
                    while ((len = raf_read.read(buffer)) != -1) {
                        //3.3.向合并文件写数据
                        raf_write.write(buffer, 0, len);
                    }
                }
            }
        }

        //4.校验合并后的文件是否正确
        try (FileInputStream sourceFileStream = new FileInputStream(sourceFile);
             FileInputStream mergeFileStream = new FileInputStream(mergeFile)) {
            String sourceMd5Hex = DigestUtils.md5Hex(sourceFileStream);
            String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
            if (sourceMd5Hex.equals(mergeMd5Hex)) {
                System.out.println("合并成功");
            } else {
                System.out.println("合并失败");
            }
        }
    }

}
