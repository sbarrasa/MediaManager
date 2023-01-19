package com.blink.s3api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@NoArgsConstructor
@Entity
@Table(name = "FILE_META")
@Getter
@Setter
public class FileMeta {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FILE_NAME")
    private String fileName;


    @Column(name = "CRC32")
    private long crc32;

    public FileMeta(String fileName, long crc32) {
        this.fileName = fileName;
        this.crc32 = crc32;
    }


}