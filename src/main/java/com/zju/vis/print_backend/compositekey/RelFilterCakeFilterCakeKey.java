package com.zju.vis.print_backend.compositekey;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class RelFilterCakeFilterCakeKey implements Serializable{
    @Column(name = "filter_cake_id")
    Long filterCakeId;

    @Column(name = "filter_cake_id")
    Long filterCakeIdUsed;

    public Long getFilterCakeId() {
        return filterCakeId;
    }

    public void setFilterCakeId(Long filterCakeId) {
        this.filterCakeId = filterCakeId;
    }

    public Long getFilterCakeIdUsed() {
        return filterCakeIdUsed;
    }

    public void setFilterCakeIdUsed(Long filterCakeIdUsed) {
        this.filterCakeIdUsed = filterCakeIdUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelFilterCakeFilterCakeKey that = (RelFilterCakeFilterCakeKey) o;
        return Objects.equals(filterCakeId, that.filterCakeId) && Objects.equals(filterCakeIdUsed, that.filterCakeIdUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterCakeId, filterCakeIdUsed);
    }

}
