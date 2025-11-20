package com.hcmut.voltrent.dtos.response;

import com.hcmut.voltrent.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;
    private int totalPages;

    public PagedResponse(Page<T> page) {
        this.items = page.getContent();
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.total = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
