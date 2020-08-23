package com.kd.novel.backend.entity.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Version;

@Getter
@Setter
public class BaseEntity {

    @Version
    private Long version;
}
