package com.nd.android.common.widget.recorder.library;

import java.io.File;
import java.util.UUID;

/**
 * UUID 文件名生成器
 *
 * @author Young
 */
public class UUIDFileNameGenerator implements IFileNameGenerator {

    private String mParentDir;

    /**
     * Instantiates a new UUID file name generator.
     *
     * @param pParentDir the parent dir
     */
    public UUIDFileNameGenerator(String pParentDir) {
        mParentDir = pParentDir;
    }

    @Override
    public String getFileName() {
        return new File(mParentDir, UUID.randomUUID().toString() + ".amr").getAbsolutePath();
    }
}
