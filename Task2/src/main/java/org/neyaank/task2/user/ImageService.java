/*
 * ImageService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
//    Image getThumbnail(MultipartFile image);
    void saveImage(InputStream imageStream, String name) throws IOException;

    byte[] getImage(String name) throws IOException;
}
