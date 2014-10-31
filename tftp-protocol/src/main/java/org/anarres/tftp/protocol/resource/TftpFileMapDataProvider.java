/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpFileMapDataProvider extends TftpFileChannelDataProvider {

    public static final int DEFAULT_SIZE = 64;
    private final LoadingCache<File, TftpByteBufferData> cache;

    /**
     * @param cacheSize The cache size in filedescriptors.
     */
    public TftpFileMapDataProvider(@Nonnull String prefix, @Nonnegative int cacheSize) {
        super(prefix);
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<File, TftpByteBufferData>() {
            @Override
            public TftpByteBufferData load(File key) throws Exception {
                MappedByteBuffer buffer = Files.map(key, FileChannel.MapMode.READ_ONLY);
                return new TftpByteBufferData(buffer);
            }
        });
    }

    /**
     * @param cacheSize The cache size in filedescriptors.
     */
    public TftpFileMapDataProvider(@Nonnegative int cacheSize) {
        this(DEFAULT_PREFIX, cacheSize);
    }

    /** Uses 64 filedescriptors. */
    public TftpFileMapDataProvider(@Nonnull String prefix) {
        this(prefix, DEFAULT_SIZE);
    }

    /** Uses 64 filedescriptors. */
    public TftpFileMapDataProvider() {
        this(DEFAULT_SIZE);
    }

    @Override
    public TftpData open(String filename) throws IOException {
        File file = toFile(filename);
        if (file == null)
            return null;
        return cache.getUnchecked(file);
    }
}
