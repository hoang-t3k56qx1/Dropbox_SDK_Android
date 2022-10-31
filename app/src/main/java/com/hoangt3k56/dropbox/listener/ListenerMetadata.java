package com.hoangt3k56.dropbox.listener;

import com.dropbox.core.v2.files.Metadata;

public interface ListenerMetadata {
    void listener(Metadata metadata, int i);
}
