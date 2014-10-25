/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.io.ByteSource;
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
public class TftpFileCacheDataProvider extends TftpFileDataProvider {

    private final LoadingCache<File, byte[]> cache;

    /**
     * @param cacheSize The cache size in bytes.
     */
    public TftpFileCacheDataProvider(@Nonnull String prefix, @Nonnegative long cacheSize) {
        super(prefix);
        this.cache = CacheBuilder.newBuilder()
                .weigher(new Weigher<Object, byte[]>() {
            public int weigh(Object key, byte[] value) {
                return value.length;
            }
        }).maximumWeight(cacheSize)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<File, byte[]>() {
            @Override
            public byte[] load(File key) throws Exception {
                return Files.toByteArray(key);
            }
        });
    }

    /**
     * @param cacheSize The cache size in bytes.
     */
    public TftpFileCacheDataProvider(@Nonnegative long cacheSize) {
        this(PREFIX, cacheSize);
    }

    /** Uses a 64 Mb cache. */
    public TftpFileCacheDataProvider() {
        this(PREFIX, 64 * 1024 * 1024);
    }

    @Override
    public ByteSource open(String filename) throws IOException {
        File file = toFile(filename);
        if (file == null)
            return null;
        return ByteSource.wrap(cache.getUnchecked(file));
    }
}
