package com.project.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    private Long roleId;
    private String roleName;
    private List<Long> userIds;
}

