/*
 * ImageServiceImpl.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService{
    @Value("${neya.s3.thumbnail.bucket}")
    private String bucketName;
//    private final S3Client s3Client;
    private final S3Template s3Template;

    private ByteArrayOutputStream getThumbnail(InputStream image, int width, int length)
            throws IOException {
            ByteArrayOutputStream res = new ByteArrayOutputStream();
            Thumbnails.of(image)
                    .size(width,length)
                    .keepAspectRatio(false)
                    .outputFormat("jpeg")
                    .toOutputStream(res);
            return res;

    }

    @Override
    public void saveImage(InputStream imageStream, String name) throws IOException {
        ByteArrayOutputStream thumbnailOutput =
                getThumbnail(imageStream, 200, 200);
        InputStream thumbnail = new ByteArrayInputStream(thumbnailOutput.toByteArray());
        s3Template.upload(bucketName, name, thumbnail);
        log.info("Image saved {}", name);
    }

    @Override
    public byte[] getImage(String name) throws IOException {
        InputStream stream = s3Template.download(bucketName, name).getInputStream();
        byte[] image = stream.readAllBytes();
        return image;
    }
}
