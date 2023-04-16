package com.zju.vis.print_backend.Utils;

public interface UtilsService {
    <T> T[] pageList(T[] list, Integer pageNo, Integer pageSize);
}
