package com.helencoder.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 文件相关操作类
 *
 * Created by zhenghailun on 2017/6/18.
 */
public class FileIO {
    public static void main(String[] args) throws FileNotFoundException {
        // 测试函数

        String path = "data/";
        List<String> fileList = getFileList(path);
        System.out.println(fileList);
    }

    // read file from url
    public static void readDataFromUrl(String urlPath) {
        try {
            URL url = new URL(urlPath);
            int count = 0;
            Scanner input = new Scanner(url.openStream());
            while (input.hasNext()) {
                String line = input.nextLine();
                count += line.length();
            }
            System.out.println("The file size is " + count + " characters");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // get the base path of the project
    public static String getBasePath() {
        String basePath = System.getProperty("user.dir");
        return basePath;
    }

    // get the size of a directory or a file
    public static long getSize(File file) {
        long size = 0;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                size += getSize(files[i]);
            }
        } else {
            size += file.length();
        }
        return size;
    }

    // get the file list of the path
    public static List<String> getFileList(String path) throws FileNotFoundException {
        List<String> fileList = new ArrayList<String>();
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                //System.out.println("文件");
                //System.out.println("path=" + file.getPath());
                //System.out.println("absolutepath=" + file.getAbsolutePath());
                //System.out.println("name=" + file.getName());
                //fileList.add(file.getPath());
                fileList.add(file.getName());
            } else if (file.isDirectory()) {
                //System.out.println("文件夹");
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(path + "/" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        //System.out.println("path=" + readfile.getPath());
                        //System.out.println("absolutepath=" + readfile.getAbsolutePath());
                        //System.out.println("name=" + readfile.getName());
                        fileList.add(readfile.getName());
                    } else if (readfile.isDirectory()) {
                        List<String> innerFileList = new ArrayList<String>();
                        innerFileList = getFileList(path + "/" + filelist[i]);
                        fileList.addAll(innerFileList);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return fileList;
    }

    // get the file data
    public static String getFileData(String filepath) {
        String content = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                content += line.toString();
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return content;
    }

    // get the file data by line
    public static List<String> getFileDataByLine(String filepath) {
        List<String> fileDataList = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // handle
                if (line.length() != 0) {
                    fileDataList.add(line);
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return fileDataList;
    }

    // get thr file or the directory size
    public static Integer getFileCount(String path) {
        int fileCount = 0;
        return fileCount;
    }

    // write file
    public static void writeFile(String filePath, String data) {
        try {
            FileWriter writer = new FileWriter(filePath, false);
            writer.write(data);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // append file
    public static void appendFile(String filePath, String data) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(data);
            writer.write("\n");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
