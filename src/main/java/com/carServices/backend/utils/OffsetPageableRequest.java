package com.carServices.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

public class OffsetPageableRequest extends PageRequest {
    private final int offset;

    public OffsetPageableRequest(int offset, int limit, Sort sort) {
        super(offset, limit, sort);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof OffsetPageableRequest)) {
            return false;
        } else {
            PageRequest that = (PageRequest) obj;
            return this.offset == that.getOffset() && super.equals(that);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
