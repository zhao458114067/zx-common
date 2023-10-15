package com.zx.common.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author ZhaoXu
 * @date 2022/9/27 10:26
 */
@Slf4j
public class ClassLoadUtils {
    public static void loadJars(ClassLoader classLoader, String filePath) {
        if (ObjectUtils.isEmpty(filePath)) {
            return;
        }
        // 系统类库路径
        File libPath = new File(filePath);
        if (!libPath.exists()) {
            return;
        }
        // 获取所有的.jar和.zip文件
        File[] jarFiles = libPath.listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"));

        if (jarFiles != null) {
            Method method = null;
            try {
                method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            method.setAccessible(true);
            for (File file : jarFiles) {
                try {
                    URL url = file.toURI().toURL();
                    method.invoke(classLoader, url);
                    log.info("load jar，name：{}，success", file.getName());
                } catch (Exception e) {
                    log.info("load jar，name：{}，fail", file.getName());
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
