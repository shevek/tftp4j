/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpFileCacheDataProvider extends TftpFileChannelDataProvider {

    public static final long DEFAULT_SIZE = 64L * 1024 * 1024;
    private final LoadingCache<File, TftpByteArrayData> cache;

    /**
     * @param cacheSize The cache size in bytes.
     */
    public TftpFileCacheDataProvider(@Nonnull String prefix, @Nonnegative long cacheSize) {
        super(prefix);
        this.cache = CacheBuilder.newBuilder()
                .weigher(new Weigher<Object, TftpByteArrayData>() {
            public int weigh(Object key, TftpByteArrayData value) {
                return value.getSize();
            }
        }).maximumWeight(cacheSize)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .softValues()
                .build(new CacheLoader<File, TftpByteArrayData>() {
            @Override
            public TftpByteArrayData load(File key) throws Exception {
                byte[] data = Files.toByteArray(key);
                return new TftpByteArrayData(data);
            }
        });
    }

    /**
     * @param cacheSize The cache size in bytes.
     */
    public TftpFileCacheDataProvider(@Nonnegative long cacheSize) {
        this(DEFAULT_PREFIX, cacheSize);
    }

    /** Uses a 64 Mb cache. */
    public TftpFileCacheDataProvider(@Nonnull String prefix) {
        this(prefix, DEFAULT_SIZE);
    }

    /** Uses a 64 Mb cache. */
    public TftpFileCacheDataProvider() {
        this(DEFAULT_PREFIX, DEFAULT_SIZE);
    }

    @Override
    public TftpData open(String filename) throws IOException {
        File file = toFile(filename);
        if (file == null)
            return null;
        return cache.getUnchecked(file);
    }
}