package com.cyk.spring.ioc.io.factory;

import com.cyk.spring.ioc.io.strategy.IFileScanner;
import com.cyk.spring.ioc.io.strategy.impl.FileScanner;
import com.cyk.spring.ioc.io.strategy.impl.JarFileScanner;

/**
 * The class FileScannerFactory.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class FileScannerFactory {

    private final FileScanner fileScanner;
    private final JarFileScanner jarFileScanner;

    public FileScannerFactory() {
        this.fileScanner = new FileScanner();
        this.jarFileScanner = new JarFileScanner();
    }

    public IFileScanner getFileScanner(String uriStr) {
        if (uriStr.startsWith("jar:")) {
            return jarFileScanner;
        } else if (uriStr.startsWith("file:")) {
            return fileScanner;
        } else throw new RuntimeException("Illegal uriStr!");
    }
}
