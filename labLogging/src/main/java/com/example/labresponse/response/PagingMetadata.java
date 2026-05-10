package com.example.labresponse.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PagingMetadata extends BaseMetadata {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
